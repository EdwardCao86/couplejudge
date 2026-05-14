package com.catjudge.couplejudge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.catjudge.couplejudge.MainActivity;
import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.ai.JudgmentParser;
import com.catjudge.couplejudge.ai.OfflineJudgmentFactory;
import com.catjudge.couplejudge.ai.PromptBuilder;
import com.catjudge.couplejudge.data.CasesRepository;
import com.catjudge.couplejudge.model.CaseRecord;
import com.catjudge.couplejudge.model.JudgeRole;
import com.catjudge.couplejudge.model.JudgmentSections;
import com.catjudge.couplejudge.network.DeepSeekApiClient;
import com.catjudge.couplejudge.util.AppConstants;
import com.catjudge.couplejudge.util.NetworkUtils;
import com.catjudge.couplejudge.util.PrefsManager;

public class JudgmentResultActivity extends AppCompatActivity {
    public static final String EXTRA_CASE_ID = "case_id";
    private static final long REQUEST_TIMEOUT_MS = 20000L;

    private CasesRepository repository;
    private PrefsManager prefsManager;
    private DeepSeekApiClient apiClient;
    private CaseRecord caseRecord;
    private JudgeRole judgeRole;
    private ScrollView scrollContent;
    private LinearLayout layoutLoading;
    private LinearLayout cardB;
    private TextView tvTitle;
    private TextView tvFact;
    private TextView tvAdviceA;
    private TextView tvAdviceB;
    private TextView tvImprovement;
    private String currentJudgment = "";
    private boolean isRequestFinished = false;
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeoutRunnable = () -> {
        if (isRequestFinished) {
            return;
        }
        isRequestFinished = true;
        String fallback = OfflineJudgmentFactory.createFallback(caseRecord, judgeRole);
        renderResult(fallback);
        Toast.makeText(this, "AI 响应太慢，已自动切换为离线判词。", Toast.LENGTH_SHORT).show();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judgment_result);

        repository = new CasesRepository(this);
        prefsManager = new PrefsManager(this);
        apiClient = new DeepSeekApiClient();
        caseRecord = repository.getCaseById(getIntent().getLongExtra(EXTRA_CASE_ID, -1L));
        if (caseRecord == null) {
            finish();
            return;
        }
        judgeRole = AppConstants.findJudgeByType(caseRecord.getJudgeType());

        tvTitle = findViewById(R.id.tvTitle);
        tvFact = findViewById(R.id.tvFact);
        TextView tvAdviceATitle = findViewById(R.id.tvAdviceATitle);
        tvAdviceA = findViewById(R.id.tvAdviceA);
        TextView tvAdviceBTitle = findViewById(R.id.tvAdviceBTitle);
        tvAdviceB = findViewById(R.id.tvAdviceB);
        tvImprovement = findViewById(R.id.tvImprovement);
        scrollContent = findViewById(R.id.scrollContent);
        layoutLoading = findViewById(R.id.layoutLoading);
        cardB = findViewById(R.id.cardB);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnFinish = findViewById(R.id.btnFinish);
        Button btnRegenerate = findViewById(R.id.btnRegenerate);
        Button btnSave = findViewById(R.id.btnSave);

        tvTitle.setText("⚖️ " + judgeRole.getName() + "法官的判决书 🐱");
        if (AppConstants.MODE_SINGLE.equals(caseRecord.getMode())) {
            tvAdviceATitle.setText("🐱 猫猫想对你说");
        } else {
            tvAdviceATitle.setText("🐱 对 A 方的悄悄话");
            tvAdviceBTitle.setText("🐱 对 B 方的悄悄话");
        }
        btnBack.setOnClickListener(v -> finish());
        btnFinish.setOnClickListener(v -> {
            repository.deleteCase(caseRecord.getId());
            Toast.makeText(this, "本次判决已结束，未存入错题本。", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_OPEN_TAB, "hall");
            intent.putExtra(MainActivity.EXTRA_CLEAR_HALL_FORM, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        btnRegenerate.setOnClickListener(v -> requestJudgment());
        btnSave.setOnClickListener(v -> {
            repository.updateJudgment(caseRecord.getId(), currentJudgment, AppConstants.STATUS_JUDGED);
            Toast.makeText(this, "已存入错题本。", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_OPEN_TAB, "notebook");
            intent.putExtra(MainActivity.EXTRA_CLEAR_HALL_FORM, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        requestJudgment();
    }

    private void requestJudgment() {
        showLoading(true);
        isRequestFinished = false;
        timeoutHandler.removeCallbacks(timeoutRunnable);
        timeoutHandler.postDelayed(timeoutRunnable, REQUEST_TIMEOUT_MS);
        caseRecord = repository.getCaseById(caseRecord.getId());
        if (caseRecord == null) {
            isRequestFinished = true;
            timeoutHandler.removeCallbacks(timeoutRunnable);
            showLoading(false);
            Toast.makeText(this, "案件记录读取失败，请返回重试。", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean canUseOnline = NetworkUtils.isNetworkAvailable(this)
                && AppConstants.PROVIDER_DEEPSEEK.equals(prefsManager.getProvider())
                && !prefsManager.getApiKey().trim().isEmpty();
        if (!canUseOnline) {
            String fallback = OfflineJudgmentFactory.createFallback(caseRecord, judgeRole);
            isRequestFinished = true;
            timeoutHandler.removeCallbacks(timeoutRunnable);
            renderResult(fallback);
            Toast.makeText(this, "当前使用离线兜底判词。", Toast.LENGTH_SHORT).show();
            return;
        }
        apiClient.requestJudgment(
                prefsManager.getProvider(),
                prefsManager.getApiKey(),
                PromptBuilder.buildSystemPrompt(judgeRole, caseRecord.getMode()),
                PromptBuilder.buildUserPrompt(caseRecord),
                new DeepSeekApiClient.AiCallback() {
                    @Override
                    public void onSuccess(String content) {
                        runOnUiThread(() -> {
                            if (isRequestFinished) {
                                return;
                            }
                            isRequestFinished = true;
                            timeoutHandler.removeCallbacks(timeoutRunnable);
                            if (content == null || content.trim().isEmpty()) {
                                String fallback = OfflineJudgmentFactory.createFallback(caseRecord, judgeRole);
                                renderResult(fallback);
                                Toast.makeText(JudgmentResultActivity.this, "AI 返回为空，已切换为离线判词。", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            renderResult(content);
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        runOnUiThread(() -> {
                            if (isRequestFinished) {
                                return;
                            }
                            isRequestFinished = true;
                            timeoutHandler.removeCallbacks(timeoutRunnable);
                            String fallback = OfflineJudgmentFactory.createFallback(caseRecord, judgeRole);
                            renderResult(fallback);
                            Toast.makeText(JudgmentResultActivity.this, message, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
        );
    }

    private void renderResult(String rawText) {
        try {
            currentJudgment = rawText == null ? "" : rawText;
            repository.updateJudgment(caseRecord.getId(), currentJudgment, AppConstants.STATUS_READY_TO_SAVE);
            JudgmentSections sections = JudgmentParser.parse(currentJudgment);
            tvFact.setText(safeSection(sections.getFact(), "猫猫正在努力整理事实喵，请稍后再试一次。"));
            tvAdviceA.setText(safeSection(sections.getAdviceA(), "猫猫想先轻轻抱抱你，也欢迎你重新生成一次判词。"));
            tvAdviceB.setText(safeSection(sections.getAdviceB(), ""));
            tvImprovement.setText(safeSection(sections.getImprovement(), "1. 先暂停争执。\n2. 把感受和期待分开说。\n3. 等情绪平稳后再继续聊。"));
            cardB.setVisibility(AppConstants.MODE_SINGLE.equals(caseRecord.getMode()) ? View.GONE : View.VISIBLE);
            showLoading(false);
        } catch (Exception e) {
            currentJudgment = rawText == null ? "" : rawText;
            tvFact.setText("猫猫刚刚绊了一下，暂时没能完整展示判词。");
            tvAdviceA.setText("可以点“太难听了”重新生成一次，或者先结束本次喵。");
            tvAdviceB.setText("");
            tvImprovement.setText("1. 检查网络是否稳定。\n2. 确认 API Key 可用。\n3. 再试一次生成判词。");
            cardB.setVisibility(AppConstants.MODE_SINGLE.equals(caseRecord.getMode()) ? View.GONE : View.VISIBLE);
            showLoading(false);
            Toast.makeText(this, "判决结果展示失败，已使用安全兜底内容。", Toast.LENGTH_SHORT).show();
        }
    }

    private String safeSection(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    @Override
    protected void onDestroy() {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        super.onDestroy();
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        scrollContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
