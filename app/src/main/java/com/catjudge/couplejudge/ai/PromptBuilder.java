package com.catjudge.couplejudge.ai;

import com.catjudge.couplejudge.model.CaseRecord;
import com.catjudge.couplejudge.model.JudgeRole;
import com.catjudge.couplejudge.util.AppConstants;

public final class PromptBuilder {
    private PromptBuilder() {
    }

    public static String buildSystemPrompt(JudgeRole judgeRole, String mode) {
        boolean isSingleMode = AppConstants.MODE_SINGLE.equals(mode);
        String modeRule = isSingleMode
                ? "当前是单人诉苦模式。请只基于 A 方已提供的信息复盘，不要编造 B 方视角，也不要输出 B 方模块。"
                : "当前是双人裁判模式，你需要综合 A、B 双方视角，做中立复盘。";
        String outputFormat = isSingleMode
                ? "{\n"
                + "  \"fact\": \"事实查明内容\",\n"
                + "  \"adviceA\": \"猫猫想对你说的内容\",\n"
                + "  \"improvement\": \"2-3 条改善建议内容\"\n"
                + "}"
                : "{\n"
                + "  \"fact\": \"事实查明内容\",\n"
                + "  \"adviceA\": \"对 A 方的悄悄话内容\",\n"
                + "  \"adviceB\": \"对 B 方的悄悄话内容\",\n"
                + "  \"improvement\": \"2-3 条改善建议内容\"\n"
                + "}";
        String outputReminder = isSingleMode
                ? "只能输出一个合法 JSON 对象，且只能包含 fact、adviceA、improvement 这 3 个字段。"
                : "只能输出一个合法 JSON 对象，且只能包含 fact、adviceA、adviceB、improvement 这 4 个字段。";
        return judgeRole.getPersonaPrompt() + "\n"
                + "你是情侣冲突复盘法官，必须温柔、可爱、客观、中立，不能偏袒任何一方。\n"
                + modeRule + "\n"
                + "你必须严格按照下面的 JSON 格式输出，不得添加任何前言、总结、解释、Markdown 标题、代码块标记或多余文字。\n"
                + "只能输出以下 JSON 对象：\n"
                + outputFormat
                + "\n要求：事实部分客观；点评部分温柔；改善建议给出 2-3 条可执行建议；保留轻柔可爱的语气；"
                + outputReminder;
    }

    public static String buildUserPrompt(CaseRecord caseRecord) {
        boolean isSingleMode = AppConstants.MODE_SINGLE.equals(caseRecord.getMode());
        return "请根据以下内容输出判决书：\n"
                + "模式：" + caseRecord.getMode() + "\n"
                + "事件描述：" + caseRecord.getEventDescription() + "\n"
                + (isSingleMode ? "我的视角：" : "A 方视角：") + safe(caseRecord.getViewA()) + "\n"
                + (isSingleMode ? "" : "B 方视角：" + safe(caseRecord.getViewB()) + "\n")
                + (isSingleMode
                ? "禁止编造 B 方观点，也不要分析 B 方心理。\n再次提醒：最终回复只能是一个 JSON 对象，并且只能包含 fact、adviceA、improvement 这 3 个字段。"
                : "禁止编造不存在的事实。\n再次提醒：最终回复只能是一个 JSON 对象，并且只能包含 fact、adviceA、adviceB、improvement 这 4 个字段。");
    }

    private static String safe(String value) {
        return value == null || value.trim().isEmpty() ? "暂无信息" : value.trim();
    }
}
