// src/main/java/com/smartlead/service/AILeadScoringService.java
package com.smartlead.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlead.entity.Lead;
import com.smartlead.entity.Interaction;
import com.smartlead.repository.InteractionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-Powered Lead Scoring Service
 * Provides intelligent lead classification and scoring using:
 * - Rule-based scoring algorithms
 * - OpenAI GPT integration for intelligent analysis
 * - Interaction history analysis
 * - Dynamic score updates based on behavior
 */
@Service
public class AILeadScoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(AILeadScoringService.class);
    
    @Value("${openai.api.key:}")
    private String openaiApiKey;
    
    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;
    
    @Value("${openai.model:gpt-4}")
    private String openaiModel;
    
    @Value("${lead.scoring.ai.enabled:true}")
    private boolean aiScoringEnabled;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final InteractionRepository interactionRepository;
    
    // Scoring weights
    private static final int EMAIL_DOMAIN_WEIGHT = 25;
    private static final int COMPANY_WEIGHT = 20;
    private static final int JOB_TITLE_WEIGHT = 30;
    private static final int PHONE_WEIGHT = 15;
    private static final int ENGAGEMENT_WEIGHT = 35;
    private static final int RECENCY_WEIGHT = 25;
    private static final int BUDGET_WEIGHT = 30;
    private static final int AUTHORITY_WEIGHT = 25;
    
    public AILeadScoringService(RestTemplate restTemplate, 
                                ObjectMapper objectMapper,
                                InteractionRepository interactionRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.interactionRepository = interactionRepository;
    }
    
    /**
     * Calculate comprehensive lead score
     * Combines rule-based and AI-powered scoring
     */
    public int calculateLeadScore(Lead lead) {
        try {
            int ruleBasedScore = calculateRuleBasedScore(lead);
            int engagementScore = calculateEngagementScore(lead);
            int bafntScore = calculateBANTScore(lead); // Budget, Authority, Need, Timeline
            
            // Combine scores with weights
            double finalScore = (ruleBasedScore * 0.4) + 
                              (engagementScore * 0.35) + 
                              (bafntScore * 0.25);
            
            // Apply AI enhancement if enabled
            if (aiScoringEnabled && openaiApiKey != null && !openaiApiKey.isEmpty()) {
                int aiAdjustment = calculateAIAdjustment(lead);
                finalScore = finalScore + aiAdjustment;
            }
            
            int score = (int) Math.min(Math.max(finalScore, 0), 100);
            
            logger.info("Lead {} scored: {} (Rule: {}, Engagement: {}, BANT: {})", 
                       lead.getId(), score, ruleBasedScore, engagementScore, bafntScore);
            
            return score;
            
        } catch (Exception e) {
            logger.error("Error calculating lead score for lead {}: {}", lead.getId(), e.getMessage());
            return calculateRuleBasedScore(lead); // Fallback to rule-based
        }
    }
    
    /**
     * Rule-based scoring using lead attributes
     */
    private int calculateRuleBasedScore(Lead lead) {
        int score = 0;
        
        // 1. Email Domain Scoring (0-25 points)
        score += scoreEmailDomain(lead.getEmail());
        
        // 2. Company Information (0-20 points)
        score += scoreCompany(lead.getCompany());
        
        // 3. Job Title/Authority (0-30 points)
        score += scoreJobTitle(lead.getJobTitle());
        
        // 4. Contact Information (0-15 points)
        score += scoreContactInfo(lead);
        
        // 5. Lead Source Quality (0-10 points)
        score += scoreLeadSource(lead.getSource());
        
        return Math.min(score, 100);
    }
    
    /**
     * Score email domain
     */
    private int scoreEmailDomain(String email) {
        if (email == null || email.isEmpty()) return 0;
        
        email = email.toLowerCase();
        
        // Free email providers - lower score
        List<String> freeProviders = Arrays.asList("gmail.com", "yahoo.com", "hotmail.com", 
                                                   "outlook.com", "aol.com", "icloud.com");
        
        for (String provider : freeProviders) {
            if (email.contains("@" + provider)) {
                return 10;
            }
        }
        
        // Business email - higher score
        if (email.contains("@") && !email.endsWith(".edu")) {
            return EMAIL_DOMAIN_WEIGHT;
        }
        
        return 5;
    }
    
    /**
     * Score company information
     */
    private int scoreCompany(String company) {
        if (company == null || company.trim().isEmpty()) return 0;
        
        company = company.toLowerCase();
        
        // Fortune 500 or well-known companies
        List<String> premiumIndicators = Arrays.asList("inc", "corp", "ltd", "llc", 
                                                       "technologies", "solutions");
        
        for (String indicator : premiumIndicators) {
            if (company.contains(indicator)) {
                return COMPANY_WEIGHT;
            }
        }
        
        return 15; // Has company name
    }
    
    /**
     * Score job title for decision-making authority
     */
    private int scoreJobTitle(String jobTitle) {
        if (jobTitle == null || jobTitle.isEmpty()) return 0;
        
        String title = jobTitle.toLowerCase();
        
        // C-Level executives
        List<String> cLevel = Arrays.asList("ceo", "cto", "cfo", "coo", "cmo", "chief", "president");
        for (String role : cLevel) {
            if (title.contains(role)) {
                return JOB_TITLE_WEIGHT;
            }
        }
        
        // VP/Director level
        List<String> vpLevel = Arrays.asList("vp", "vice president", "director", "head of", "founder");
        for (String role : vpLevel) {
            if (title.contains(role)) {
                return 25;
            }
        }
        
        // Manager level
        List<String> managerLevel = Arrays.asList("manager", "lead", "senior");
        for (String role : managerLevel) {
            if (title.contains(role)) {
                return 20;
            }
        }
        
        return 10; // Has job title
    }
    
    /**
     * Score contact information completeness
     */
    private int scoreContactInfo(Lead lead) {
        int score = 0;
        
        if (lead.getPhone() != null && !lead.getPhone().isEmpty()) {
            score += 8;
        }
        
        if (lead.getLinkedinUrl() != null && !lead.getLinkedinUrl().isEmpty()) {
            score += 4;
        }
        
        if (lead.getWebsite() != null && !lead.getWebsite().isEmpty()) {
            score += 3;
        }
        
        return Math.min(score, PHONE_WEIGHT);
    }
    
    /**
     * Score lead source quality
     */
    private int scoreLeadSource(String source) {
        if (source == null) return 5;
        
        source = source.toLowerCase();
        
        Map<String, Integer> sourceScores = new HashMap<>();
        sourceScores.put("referral", 10);
        sourceScores.put("direct", 9);
        sourceScores.put("website", 8);
        sourceScores.put("linkedin", 7);
        sourceScores.put("email campaign", 6);
        sourceScores.put("social media", 5);
        sourceScores.put("cold outreach", 3);
        
        return sourceScores.entrySet().stream()
            .filter(entry -> source.contains(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(5);
    }
    
    /**
     * Calculate engagement score based on interaction history
     */
    private int calculateEngagementScore(Lead lead) {
        if (lead.getId() == null) return 0;
        
        try {
            List<Interaction> interactions = interactionRepository.findByLeadId(lead.getId());
            
            if (interactions.isEmpty()) return 0;
            
            int score = 0;
            
            // Interaction frequency
            score += Math.min(interactions.size() * 5, 20);
            
            // Recent activity bonus
            long recentInteractions = interactions.stream()
                .filter(i -> i.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .count();
            score += Math.min(recentInteractions * 3, 15);
            
            // Interaction quality
            for (Interaction interaction : interactions) {
                score += scoreInteractionType(interaction.getType());
            }
            
            return Math.min(score, ENGAGEMENT_WEIGHT);
            
        } catch (Exception e) {
            logger.warn("Error calculating engagement score: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Score different interaction types
     */
    private int scoreInteractionType(String type) {
        if (type == null) return 0;
        
        type = type.toLowerCase();
        
        Map<String, Integer> typeScores = new HashMap<>();
        typeScores.put("meeting", 5);
        typeScores.put("demo", 5);
        typeScores.put("call", 3);
        typeScores.put("email_reply", 2);
        typeScores.put("email_open", 1);
        typeScores.put("website_visit", 1);
        
        return typeScores.entrySet().stream()
            .filter(entry -> type.contains(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(1);
    }
    
    /**
     * Calculate BANT Score (Budget, Authority, Need, Timeline)
     */
    private int calculateBANTScore(Lead lead) {
        int score = 0;
        
        // Budget indicator
        if (lead.getEstimatedBudget() != null && lead.getEstimatedBudget() > 0) {
            score += Math.min(lead.getEstimatedBudget() / 1000, BUDGET_WEIGHT);
        }
        
        // Authority (from job title score)
        score += scoreJobTitle(lead.getJobTitle()) / 3;
        
        // Need indicator (if notes mention pain points)
        if (lead.getNotes() != null && !lead.getNotes().isEmpty()) {
            String notes = lead.getNotes().toLowerCase();
            List<String> needIndicators = Arrays.asList("problem", "challenge", "issue", 
                                                        "looking for", "need", "urgent");
            
            long needCount = needIndicators.stream()
                .filter(notes::contains)
                .count();
            
            score += Math.min(needCount * 3, 10);
        }
        
        // Timeline urgency
        if (lead.getExpectedCloseDate() != null) {
            long daysUntilClose = ChronoUnit.DAYS.between(LocalDateTime.now(), 
                                                         lead.getExpectedCloseDate());
            
            if (daysUntilClose < 30) {
                score += 15; // Urgent
            } else if (daysUntilClose < 90) {
                score += 10; // Near-term
            } else {
                score += 5; // Long-term
            }
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * Use OpenAI to provide intelligent lead scoring adjustment
     */
    @Cacheable(value = "aiScores", key = "#lead.id")
    private int calculateAIAdjustment(Lead lead) {
        try {
            String prompt = buildScoringPrompt(lead);
            String aiResponse = callOpenAI(prompt);
            
            return parseAIResponse(aiResponse);
            
        } catch (Exception e) {
            logger.error("Error calling OpenAI API: {}", e.getMessage());
            return 0; // No adjustment on error
        }
    }
    
    /**
     * Build prompt for OpenAI
     */
    private String buildScoringPrompt(Lead lead) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this sales lead and provide a scoring adjustment (-20 to +20):\n\n");
        prompt.append("Lead Information:\n");
        prompt.append("- Name: ").append(lead.getName()).append("\n");
        prompt.append("- Company: ").append(lead.getCompany()).append("\n");
        prompt.append("- Job Title: ").append(lead.getJobTitle()).append("\n");
        prompt.append("- Email: ").append(lead.getEmail()).append("\n");
        
        if (lead.getNotes() != null && !lead.getNotes().isEmpty()) {
            prompt.append("- Notes: ").append(lead.getNotes()).append("\n");
        }
        
        prompt.append("\nBased on this information, provide:\n");
        prompt.append("1. A score adjustment between -20 and +20\n");
        prompt.append("2. Brief reasoning (one sentence)\n");
        prompt.append("3. Recommended next action\n\n");
        prompt.append("Format your response as JSON:\n");
        prompt.append("{\n");
        prompt.append("  \"adjustment\": <number>,\n");
        prompt.append("  \"reasoning\": \"<string>\",\n");
        prompt.append("  \"nextAction\": \"<string>\"\n");
        prompt.append("}");
        
        return prompt.toString();
    }
    
    /**
     * Call OpenAI API
     */
    private String callOpenAI(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openaiModel);
        requestBody.put("messages", Arrays.asList(
            Map.of("role", "system", "content", "You are an expert sales analyst."),
            Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 200);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            openaiApiUrl,
            HttpMethod.POST,
            request,
            String.class
        );
        
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        }
        
        throw new RuntimeException("OpenAI API call failed: " + response.getStatusCode());
    }
    
    /**
     * Parse AI response
     */
    private int parseAIResponse(String response) {
        try {
            // Try to extract JSON from response
            int startIdx = response.indexOf("{");
            int endIdx = response.lastIndexOf("}") + 1;
            
            if (startIdx >= 0 && endIdx > startIdx) {
                String jsonStr = response.substring(startIdx, endIdx);
                JsonNode json = objectMapper.readTree(jsonStr);
                
                int adjustment = json.path("adjustment").asInt(0);
                String reasoning = json.path("reasoning").asText("");
                
                logger.info("AI Adjustment: {} - Reasoning: {}", adjustment, reasoning);
                
                return Math.max(-20, Math.min(20, adjustment));
            }
            
            return 0;
            
        } catch (Exception e) {
            logger.warn("Error parsing AI response: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Classify lead based on score
     */
    public String classifyLead(int score) {
        if (score >= 75) {
            return "HOT";
        } else if (score >= 50) {
            return "WARM";
        } else {
            return "COLD";
        }
    }
    
    /**
     * Get lead classification with score
     */
    public Map<String, Object> getLeadClassification(Lead lead) {
        int score = calculateLeadScore(lead);
        String classification = classifyLead(score);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("classification", classification);
        result.put("priority", getPriority(score));
        result.put("recommendations", getRecommendations(lead, score));
        
        return result;
    }
    
    /**
     * Get priority level
     */
    private String getPriority(int score) {
        if (score >= 80) return "IMMEDIATE";
        if (score >= 60) return "HIGH";
        if (score >= 40) return "MEDIUM";
        return "LOW";
    }
    
    /**
     * Get action recommendations
     */
    private List<String> getRecommendations(Lead lead, int score) {
        List<String> recommendations = new ArrayList<>();
        
        if (score >= 75) {
            recommendations.add("Schedule demo call immediately");
            recommendations.add("Assign to senior sales representative");
        } else if (score >= 50) {
            recommendations.add("Send personalized email");
            recommendations.add("Schedule discovery call");
        } else {
            recommendations.add("Add to nurture campaign");
            recommendations.add("Gather more information");
        }
        
        // Specific recommendations based on gaps
        if (lead.getPhone() == null || lead.getPhone().isEmpty()) {
            recommendations.add("Obtain phone number");
        }
        
        if (lead.getCompany() == null || lead.getCompany().isEmpty()) {
            recommendations.add("Research company information");
        }
        
        return recommendations;
    }
    
    /**
     * Batch score multiple leads
     */
    public Map<Long, Integer> batchScoreLeads(List<Lead> leads) {
        return leads.parallelStream()
            .collect(Collectors.toMap(
                Lead::getId,
                this::calculateLeadScore,
                (a, b) -> a
            ));
    }
    
    /**
     * Recalculate score when lead is updated
     */
    public void updateLeadScore(Lead lead) {
        int newScore = calculateLeadScore(lead);
        lead.setScore(newScore);
        lead.setClassification(classifyLead(newScore));
        lead.setLastScoredAt(LocalDateTime.now());
        
        logger.info("Updated lead {} score to {} ({})", 
                   lead.getId(), newScore, lead.getClassification());
    }
}
