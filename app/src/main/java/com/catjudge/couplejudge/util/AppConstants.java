package com.catjudge.couplejudge.util;

import com.catjudge.couplejudge.model.JudgeRole;

import java.util.ArrayList;
import java.util.List;

public final class AppConstants {
    public static final String MODE_SINGLE = "单人";
    public static final String MODE_DOUBLE = "双人";
    public static final String STATUS_DRAFT_A = "待A填写";
    public static final String STATUS_DRAFT_B = "待B填写";
    public static final String STATUS_READY_TO_SAVE = "待存入";
    public static final String STATUS_JUDGED = "已判决";
    public static final String PROVIDER_DEEPSEEK = "DeepSeek";
    public static final String PROVIDER_QWEN = "通义千问";

    private AppConstants() {
    }

    public static List<JudgeRole> getJudgeRoles() {
        List<JudgeRole> roles = new ArrayList<>();
        roles.add(new JudgeRole("xiaoju", "小橘", "治愈", "🧡",
                "你是小橘法官，语气治愈、柔软、有陪伴感，擅长安抚情绪，但依然保持客观中立。"));
        roles.add(new JudgeRole("buou", "布偶", "优雅理性", "🤍",
                "你是布偶法官，语气优雅、理性、清晰，善于帮情侣把情绪翻译成事实与需求。"));
        roles.add(new JudgeRole("naiuniu", "奶牛", "贪吃幽默", "🤎",
                "你是奶牛法官，语气幽默可爱、带一点轻松感，但不能轻视情绪与问题本身。"));
        roles.add(new JudgeRole("heitan", "黑炭", "严谨", "🖤",
                "你是黑炭法官，语气严谨、克制、铁面但不伤人，重视边界、责任和执行建议。"));
        return roles;
    }

    public static JudgeRole findJudgeByType(String type) {
        for (JudgeRole role : getJudgeRoles()) {
            if (role.getType().equals(type)) {
                return role;
            }
        }
        return getJudgeRoles().get(0);
    }
}
