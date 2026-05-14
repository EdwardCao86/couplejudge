package com.catjudge.couplejudge.ai;

import com.catjudge.couplejudge.model.CaseRecord;
import com.catjudge.couplejudge.model.JudgeRole;
import com.catjudge.couplejudge.util.AppConstants;

public final class OfflineJudgmentFactory {
    private OfflineJudgmentFactory() {
    }

    public static String createFallback(CaseRecord caseRecord, JudgeRole judgeRole) {
        if (AppConstants.MODE_SINGLE.equals(caseRecord.getMode())) {
            return "{"
                    + "\"fact\":\"目前网络开小差了，" + judgeRole.getName() + "法官先根据你填写的信息做一次离线复盘：这次冲突里，你正在努力表达自己的在意和委屈，但当下还只有单方信息，因此结论会更偏向情绪安抚与自我整理。\","
                    + "\"adviceA\":\"你的情绪值得被看见。如果能把‘我现在难过什么、我期待什么、我希望对方怎么做’分开说，会更容易被理解喵。\","
                    + "\"improvement\":\"1. 先暂停 10 分钟，等情绪回稳再继续说。\\n2. 用‘我感到……’代替‘你总是……’。\\n3. 等愿意沟通时，再把这次事件拆成事实、感受和期待三部分说清楚。\""
                    + "}";
        }
        return "{"
                + "\"fact\":\"目前网络开小差了，" + judgeRole.getName() + "法官先根据双方填写的信息做一次离线复盘：这次矛盾里，双方都在表达需求，但表达方式可能让彼此更受伤。\","
                + "\"adviceA\":\"你的情绪值得被看见，但如果能把感受、需求和期待分开说，会更容易被理解喵。\","
                + "\"adviceB\":\"也许对方当下有自己的委屈或防御，建议先把真实需求说清楚，而不是只把情绪顶回去喵。\","
                + "\"improvement\":\"1. 先暂停 10 分钟，等情绪回稳再继续说。\\n2. 用‘我感到……’代替‘你总是……’。\\n3. 约定一个下次遇到同类问题时的具体做法。\""
                + "}";
    }
}
