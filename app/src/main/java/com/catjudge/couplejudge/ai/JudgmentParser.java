package com.catjudge.couplejudge.ai;

import com.catjudge.couplejudge.model.JudgmentSections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JudgmentParser {
    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("(?is)```(?:json)?\\s*(\\{[\\s\\S]*?\\})\\s*```");
    private static final Pattern FACT_TAG_PATTERN = Pattern.compile("(?is)<FACT>\\s*(.*?)\\s*</FACT>");
    private static final Pattern ADVICE_A_TAG_PATTERN = Pattern.compile("(?is)<ADVICE_A>\\s*(.*?)\\s*</ADVICE_A>");
    private static final Pattern ADVICE_B_TAG_PATTERN = Pattern.compile("(?is)<ADVICE_B>\\s*(.*?)\\s*</ADVICE_B>");
    private static final Pattern IMPROVEMENT_TAG_PATTERN = Pattern.compile("(?is)<IMPROVEMENT>\\s*(.*?)\\s*</IMPROVEMENT>");
    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "(?s)(?:^|\\n)\\s{0,4}(?:[#>*-]\\s*)*(①|②|③|④|1[.、]|2[.、]|3[.、]|4[.、])\\s*" +
                    "(事实查明|对\\s*A\\s*方\\s*温柔点评|对\\s*A\\s*方\\s*的?悄悄话|对\\s*B\\s*方\\s*温柔点评|对\\s*B\\s*方\\s*的?悄悄话|猫猫的改善建议)" +
                    "\\s*[：:]?\\s*(.*?)(?=(?:\\n\\s{0,4}(?:[#>*-]\\s*)*(?:①|②|③|④|1[.、]|2[.、]|3[.、]|4[.、])\\s*(?:事实查明|对\\s*A\\s*方\\s*温柔点评|对\\s*A\\s*方\\s*的?悄悄话|对\\s*B\\s*方\\s*温柔点评|对\\s*B\\s*方\\s*的?悄悄话|猫猫的改善建议)\\s*[：:]?)|$)"
    );

    private JudgmentParser() {
    }

    public static JudgmentSections parse(String rawText) {
        String normalized = rawText == null ? "" : rawText.trim();
        String fact = "";
        String adviceA = "";
        String adviceB = "";
        String improvement = "";

        String jsonCandidate = unwrapJson(normalized);
        try {
            JSONObject jsonObject = new JSONObject(jsonCandidate);
            fact = clean(readJsonValue(jsonObject, "fact"));
            adviceA = clean(readJsonValue(jsonObject, "adviceA"));
            adviceB = clean(readJsonValue(jsonObject, "adviceB"));
            improvement = clean(readJsonValue(jsonObject, "improvement"));
        } catch (JSONException ignored) {
            // 如果模型没有严格返回 JSON，再走旧兜底逻辑
        }

        if (fact.isEmpty()) {
            fact = clean(extractByTag(normalized, FACT_TAG_PATTERN));
        }
        if (adviceA.isEmpty()) {
            adviceA = clean(extractByTag(normalized, ADVICE_A_TAG_PATTERN));
        }
        if (adviceB.isEmpty()) {
            adviceB = clean(extractByTag(normalized, ADVICE_B_TAG_PATTERN));
        }
        if (improvement.isEmpty()) {
            improvement = clean(extractByTag(normalized, IMPROVEMENT_TAG_PATTERN));
        }

        if (fact.isEmpty() || adviceA.isEmpty() || adviceB.isEmpty() || improvement.isEmpty()) {
            Matcher matcher = SECTION_PATTERN.matcher(normalized);
            while (matcher.find()) {
                String sectionTitle = matcher.group(2);
                String sectionContent = clean(matcher.group(3));
                if (matchesFact(sectionTitle) && fact.isEmpty()) {
                    fact = sectionContent;
                } else if (matchesAdviceA(sectionTitle) && adviceA.isEmpty()) {
                    adviceA = sectionContent;
                } else if (matchesAdviceB(sectionTitle) && adviceB.isEmpty()) {
                    adviceB = sectionContent;
                } else if (matchesImprovement(sectionTitle) && improvement.isEmpty()) {
                    improvement = sectionContent;
                }
            }
        }

        if (fact.isEmpty()) {
            fact = pick(normalized, "①事实查明", "②对 A 方温柔点评");
        }
        if (adviceA.isEmpty()) {
            adviceA = pick(normalized, "②对 A 方温柔点评", "③对 B 方温柔点评");
        }
        if (adviceB.isEmpty()) {
            adviceB = pick(normalized, "③对 B 方温柔点评", "④猫猫的改善建议");
        }
        if (improvement.isEmpty()) {
            improvement = after(normalized, "④猫猫的改善建议");
        }

        if (fact.isEmpty() && adviceA.isEmpty() && adviceB.isEmpty() && improvement.isEmpty()) {
            return new JudgmentSections(normalized, "请再温柔表达自己的感受喵。", "请先理解对方没有说出口的需要喵。", "1. 先暂停争执\n2. 用事实而不是指责表达\n3. 约定一次复盘时间");
        }
        return new JudgmentSections(clean(fact), clean(adviceA), clean(adviceB), clean(improvement));
    }

    private static String pick(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        int endIndex = text.indexOf(end);
        if (startIndex < 0 || endIndex < 0 || endIndex <= startIndex) {
            return "";
        }
        return text.substring(startIndex + start.length(), endIndex);
    }

    private static String after(String text, String start) {
        int startIndex = text.indexOf(start);
        if (startIndex < 0) {
            return "";
        }
        return text.substring(startIndex + start.length());
    }

    private static String clean(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value
                .replaceFirst("^[：:\\s]+", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
        }
        if ((cleaned.startsWith("\"") && cleaned.endsWith("\""))
                || (cleaned.startsWith("'") && cleaned.endsWith("'"))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
        }
        return cleaned;
    }

    private static String unwrapJson(String text) {
        Matcher matcher = JSON_BLOCK_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return text;
    }

    private static String extractByTag(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1);
    }

    private static String readJsonValue(JSONObject jsonObject, String key) {
        Object value = jsonObject.opt(key);
        if (value == null || JSONObject.NULL.equals(value)) {
            return "";
        }
        if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                String item = clean(String.valueOf(jsonArray.opt(i)));
                if (item.isEmpty()) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append("\n");
                }
                builder.append(item);
            }
            return builder.toString();
        }
        return String.valueOf(value);
    }

    private static boolean matchesFact(String title) {
        return normalizedTitle(title).contains("事实查明");
    }

    private static boolean matchesAdviceA(String title) {
        String normalized = normalizedTitle(title);
        return normalized.contains("对a方温柔点评") || normalized.contains("对a方的悄悄话");
    }

    private static boolean matchesAdviceB(String title) {
        String normalized = normalizedTitle(title);
        return normalized.contains("对b方温柔点评") || normalized.contains("对b方的悄悄话");
    }

    private static boolean matchesImprovement(String title) {
        return normalizedTitle(title).contains("猫猫的改善建议");
    }

    private static String normalizedTitle(String title) {
        return title == null ? "" : title.replaceAll("\\s+", "").toLowerCase();
    }
}
