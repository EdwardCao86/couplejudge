package com.catjudge.couplejudge.util;

public final class CategoryClassifier {
    private CategoryClassifier() {
    }

    public static String classify(String event) {
        if (event == null) {
            return "其他";
        }
        String content = event.toLowerCase();
        if (content.contains("家务") || content.contains("做饭") || content.contains("洗碗") || content.contains("打扫")) {
            return "家务";
        }
        if (content.contains("聊天") || content.contains("沟通") || content.contains("语气") || content.contains("冷战")) {
            return "沟通";
        }
        if (content.contains("陪") || content.contains("约会") || content.contains("见面")) {
            return "陪伴";
        }
        if (content.contains("钱") || content.contains("红包") || content.contains("消费")) {
            return "金钱";
        }
        return "其他";
    }
}
