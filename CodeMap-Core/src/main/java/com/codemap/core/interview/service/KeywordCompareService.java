package com.codemap.core.interview.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class KeywordCompareService {

    private final Set<String> stopwords = Set.of(
        "입니다", "습니다", "합니다", "있습니다", "없습니다", "됩니다",
        "이다", "하다", "되다", "있다", "없다", "위해", "통해", "때문에",
        "경우", "것", "등", "및", "또는", "그리고", "하지만", "따라서","하여","해"
    );

    private final Pattern particlePattern = Pattern.compile(
        "(은|는|이|가|을|를|의|에서|로|으로|와|과|도|만|부터|까지|에게|한테|께|보다|마저|조차|라도|나|이나|적)$"
    );

    /** 키워드 문자열(DB) 파싱 */
    public List<String> parseKeywordsFromDB(String keywordsString) {
        if (keywordsString == null || keywordsString.trim().isEmpty()) return new ArrayList<>();
        String[] tokens = keywordsString.split(",");
        List<String> result = new ArrayList<>();
        for (String t : tokens) {
            String keyword = t.trim();
            if (!keyword.isEmpty()) result.add(keyword);
        }
        return result;
    }

    /** 모범 답안 기반 키워드 추출 */
    public List<String> extractCoreKeywords(String modelAnswer) {
        if (modelAnswer == null || modelAnswer.trim().isEmpty()) return new ArrayList<>();
        String[] tokens = modelAnswer.replaceAll("[^가-힣a-zA-Z0-9\\s]", "").split("\\s+");
        Set<String> keywords = new LinkedHashSet<>();
        for (String token : tokens) {
            String clean = removeParticles(token.trim().toLowerCase());
            if (isValidKeyword(clean)) keywords.add(clean);
        }
        return new ArrayList<>(keywords);
    }

    /** 불용어 제거 및 조사 제거 */
    private String removeParticles(String word) {
        Matcher matcher = particlePattern.matcher(word);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1)); // 조사 제외
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private boolean isValidKeyword(String word) {
        return word.length() > 1 && !stopwords.contains(word);
    }

    /** 사용자 답변에서 조사 제거 */
    private String removeParticlesFromText(String text) {
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String cleanWord = removeParticles(word);
            if (result.length() > 0) result.append(" ");
            result.append(cleanWord);
        }

        return result.toString();
    }

    /** 정확한 단어 경계 기반 매칭 - 조사 제거 후 비교 */
    private boolean isKeywordMatched(String text, String keyword) {
        String normalizedText = normalize(text);
        String normalizedKeyword = normalize(keyword);

        // 1. 정확한 매칭 시도
        Pattern exactPattern = Pattern.compile("(?<!\\S)" + Pattern.quote(normalizedKeyword) + "(?!\\S)", Pattern.CASE_INSENSITIVE);
        if (exactPattern.matcher(normalizedText).find()) {
            return true;
        }

        // 2. 사용자 답변에서 조사 제거 후 매칭 시도
        String textWithoutParticles = removeParticlesFromText(normalizedText);
        return exactPattern.matcher(textWithoutParticles).find();
    }

    private String normalize(String input) {
        return input.toLowerCase().replaceAll("[^가-힣a-zA-Z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }

    public List<String> findMatchedKeywords(String userAnswer, List<String> keywords) {
        List<String> matched = new ArrayList<>();
        for (String keyword : keywords) {
            if (isKeywordMatched(userAnswer, keyword)) {
                matched.add(keyword);
            }
            System.out.println("TRY MATCHING KEYWORD: " + keyword + " -> " + isKeywordMatched(userAnswer, keyword));
        }
        return matched;
    }

    public List<String> findMissingKeywords(String userAnswer, List<String> keywords) {
        List<String> matched = findMatchedKeywords(userAnswer, keywords);
        List<String> missing = new ArrayList<>();
        for (String keyword : keywords) {
            if (!matched.contains(keyword)) missing.add(keyword);
        }
        return missing;
    }

    public double calculateAccuracy(String userAnswer, List<String> keywords) {
        if (keywords.isEmpty()) return 100.0;
        int matched = findMatchedKeywords(userAnswer, keywords).size();
        return (double) matched / keywords.size() * 100;
    }

    public String getGradeByAccuracy(double accuracy) {
        if (accuracy >= 90) return "A+";
        if (accuracy >= 80) return "A";
        if (accuracy >= 70) return "B+";
        if (accuracy >= 60) return "B";
        if (accuracy >= 50) return "C";
        return "D";
    }

    public String generateEvaluationComment(double accuracy, int total, int matched) {
        if (accuracy >= 90) return "🎉 훌륭합니다! 핵심 개념을 모두 포함한 완벽한 답변입니다. (" + matched + "/" + total + ")";
        if (accuracy >= 70) return "👍 좋은 답변입니다! 대부분의 핵심 개념을 포함했습니다. (" + matched + "/" + total + ")";
        if (accuracy >= 50) return "📝 괜찮은 답변이지만 일부 핵심 개념이 누락되었습니다. (" + matched + "/" + total + ")";
        return "📚 보완이 필요합니다. 중요한 개념이 많이 빠졌습니다. (" + matched + "/" + total + ")";
    }

    public String generateHighlightedAnswer(String modelAnswer, List<String> matched, List<String> missing) {
        String highlighted = modelAnswer;

        // matched 키워드 처리 - 키워드 부분만 하이라이팅
        for (String keyword : matched) {
            String pattern = "(?i)(?<!\\p{L})(" + Pattern.quote(keyword) + ")(?=[은는이가을를의에서로으로와과도만부터까지에게한테께보다마저조차라도나이나적]*[\\p{Punct}\\s]|[은는이가을를의에서로으로와과도만부터까지에게한테께보다마저조차라도나이나적]*$)";
            highlighted = highlighted.replaceAll(pattern,
                "<mark style=\"background-color:#d8f5d0; color:#2e7d32; font-weight:bold;\">$1</mark>");
        }

        // missing 키워드 처리 - 키워드 부분만 하이라이팅
        for (String keyword : missing) {
            String pattern = "(?i)(?<!\\p{L})(" + Pattern.quote(keyword) + ")(?=[은는이가을를의에서로으로와과도만부터까지에게한테께보다마저조차라도나이나적]*[\\p{Punct}\\s]|[은는이가을를의에서로으로와과도만부터까지에게한테께보다마저조차라도나이나적]*$)";
            highlighted = highlighted.replaceAll(pattern,
                "<mark style=\"background-color:#ffe6e6; color:#d32f2f; font-weight:bold;\">$1</mark>");
        }

        return highlighted;
    }

    public Map<String, Object> generateDetailedAnalysisWithDBKeywords(String userAnswer, String dbKeywords, String modelAnswer) {
        List<String> coreKeywords = parseKeywordsFromDB(dbKeywords);
        if (coreKeywords.isEmpty()) coreKeywords = extractCoreKeywords(modelAnswer);
        return generateDetailedAnalysis(userAnswer, coreKeywords);
    }

    public Map<String, Object> generateDetailedAnalysis(String userAnswer, List<String> coreKeywords) {
        Map<String, Object> analysis = new HashMap<>();
        List<String> matched = findMatchedKeywords(userAnswer, coreKeywords);
        List<String> missing = findMissingKeywords(userAnswer, coreKeywords);
        double accuracy = calculateAccuracy(userAnswer, coreKeywords);

        analysis.put("totalKeywords", coreKeywords.size());
        analysis.put("matchedKeywords", matched);
        analysis.put("missingKeywords", missing);
        analysis.put("matchedCount", matched.size());
        analysis.put("missingCount", missing.size());
        analysis.put("accuracy", accuracy);
        analysis.put("grade", getGradeByAccuracy(accuracy));
        analysis.put("comment", generateEvaluationComment(accuracy, coreKeywords.size(), matched.size()));

        return analysis;
    }
}