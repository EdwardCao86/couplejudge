package com.catjudge.couplejudge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.data.CasesRepository;
import com.catjudge.couplejudge.model.CaseRecord;
import com.catjudge.couplejudge.model.JudgeRole;
import com.catjudge.couplejudge.util.AppConstants;

public class PerspectiveInputActivity extends AppCompatActivity {
    public static final String EXTRA_CASE_ID = "case_id";

    private CaseRecord caseRecord;
    private CasesRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perspective_input);

        repository = new CasesRepository(this);
        long caseId = getIntent().getLongExtra(EXTRA_CASE_ID, -1L);
        caseRecord = repository.getCaseById(caseId);
        if (caseRecord == null) {
            finish();
            return;
        }

        JudgeRole judgeRole = AppConstants.findJudgeByType(caseRecord.getJudgeType());
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvStageHint = findViewById(R.id.tvStageHint);
        TextView tvEventSummary = findViewById(R.id.tvEventSummary);
        TextView tvSectionTitleA = findViewById(R.id.tvSectionTitleA);
        TextView tvNameA = findViewById(R.id.tvNameA);
        TextView tvNameB = findViewById(R.id.tvNameB);
        TextView tvCompletedA = findViewById(R.id.tvCompletedA);
        TextView tvCompletedB = findViewById(R.id.tvCompletedB);
        EditText etViewA = findViewById(R.id.etViewA);
        EditText etViewB = findViewById(R.id.etViewB);
        LinearLayout layoutB = findViewById(R.id.layoutB);
        Button btnJudge = findViewById(R.id.btnJudge);
        ImageButton btnBack = findViewById(R.id.btnBack);

        tvTitle.setText(judgeRole.getName() + "法官");
        tvEventSummary.setText(caseRecord.getEventDescription());
        String nickname = new com.catjudge.couplejudge.util.PrefsManager(this).getNickname();
        tvNameA.setText("A 方（" + nickname + "）");
        tvNameB.setText("B 方");

        boolean isSingleMode = AppConstants.MODE_SINGLE.equals(caseRecord.getMode());
        boolean isSecondStageDouble = !isSingleMode
                && !TextUtils.isEmpty(caseRecord.getViewA())
                && TextUtils.isEmpty(caseRecord.getViewB());
        boolean isReadyForJudgment = !isSingleMode
                && !TextUtils.isEmpty(caseRecord.getViewA())
                && !TextUtils.isEmpty(caseRecord.getViewB());

        if (isSingleMode) {
            tvStageHint.setText("请先写下你的感受和想法，猫猫法官会根据你的描述做单人复盘。");
            tvSectionTitleA.setText("═══ 我的视角 ═══");
            tvNameA.setText("我（" + nickname + "）");
            etViewA.setText(caseRecord.getViewA());
            layoutB.setVisibility(View.GONE);
            tvCompletedA.setVisibility(TextUtils.isEmpty(caseRecord.getViewA()) ? View.GONE : View.VISIBLE);
            tvCompletedA.setText("✓ 我已完成填写");
            tvCompletedB.setVisibility(View.GONE);
            btnJudge.setText("🐾 请猫猫法官帮忙审判");
        } else if (isSecondStageDouble) {
            tvSectionTitleA.setText("═══ A 方视角 ═══");
            tvStageHint.setText("A 方已完成填写，请把手机交给 B 方。B 方不会看到 A 方的输入内容。");
            etViewA.setVisibility(View.GONE);
            tvCompletedA.setVisibility(View.VISIBLE);
            tvCompletedA.setText("✓ A 方已完成填写");
            layoutB.setVisibility(View.VISIBLE);
            etViewB.setText(caseRecord.getViewB());
            tvCompletedB.setVisibility(TextUtils.isEmpty(caseRecord.getViewB()) ? View.GONE : View.VISIBLE);
            tvCompletedB.setText("✓ B 方已完成填写");
            btnJudge.setText("✓ B 方填写完成，开始审判");
        } else if (isReadyForJudgment) {
            tvSectionTitleA.setText("═══ A 方视角 ═══");
            tvStageHint.setText("双方视角都已填写完成，可以直接进入判决结果。");
            etViewA.setVisibility(View.GONE);
            etViewB.setVisibility(View.GONE);
            tvCompletedA.setVisibility(View.VISIBLE);
            tvCompletedB.setVisibility(View.VISIBLE);
            tvCompletedA.setText("✓ A 方已完成填写");
            tvCompletedB.setText("✓ B 方已完成填写");
            layoutB.setVisibility(View.VISIBLE);
            btnJudge.setText("🐾 请猫猫法官帮忙审判");
        } else {
            tvSectionTitleA.setText("═══ A 方视角 ═══");
            tvStageHint.setText("请先由 A 方填写，完成后再把手机交给 B 方，双方视角互相不可见。");
            etViewA.setText(caseRecord.getViewA());
            layoutB.setVisibility(View.GONE);
            tvCompletedA.setVisibility(TextUtils.isEmpty(caseRecord.getViewA()) ? View.GONE : View.VISIBLE);
            tvCompletedA.setText("✓ A 方已完成填写");
            tvCompletedB.setVisibility(View.GONE);
            btnJudge.setText("✓ A 方填写完成，切换给 B 方");
        }

        etViewA.addTextChangedListener(simpleWatcher(tvCompletedA));
        etViewB.addTextChangedListener(simpleWatcher(tvCompletedB));
        btnBack.setOnClickListener(v -> finish());
        btnJudge.setOnClickListener(v -> {
            String viewA = etViewA.getText().toString().trim();
            String viewB = etViewB.getText().toString().trim();
            if (isSingleMode) {
                if (TextUtils.isEmpty(viewA)) {
                    Toast.makeText(this, "请先填写你的视角。", Toast.LENGTH_SHORT).show();
                    return;
                }
                repository.updateViews(caseRecord.getId(), viewA, "");
                repository.updateStatus(caseRecord.getId(), AppConstants.STATUS_READY_TO_SAVE);
            } else if (isSecondStageDouble || isReadyForJudgment) {
                if (TextUtils.isEmpty(viewB)) {
                    Toast.makeText(this, "请先填写 B 方视角。", Toast.LENGTH_SHORT).show();
                    return;
                }
                repository.updateViews(caseRecord.getId(), caseRecord.getViewA(), viewB);
                repository.updateStatus(caseRecord.getId(), AppConstants.STATUS_READY_TO_SAVE);
            } else {
                if (TextUtils.isEmpty(viewA)) {
                    Toast.makeText(this, "请先填写 A 方视角。", Toast.LENGTH_SHORT).show();
                    return;
                }
                repository.updateViews(caseRecord.getId(), viewA, "");
                repository.updateStatus(caseRecord.getId(), AppConstants.STATUS_DRAFT_B);
                Toast.makeText(this, "A 方已完成，请把手机交给 B 方继续填写。", Toast.LENGTH_SHORT).show();
                recreate();
                return;
            }
            Intent intent = new Intent(this, JudgmentResultActivity.class);
            intent.putExtra(JudgmentResultActivity.EXTRA_CASE_ID, caseRecord.getId());
            startActivity(intent);
        });
    }

    private TextWatcher simpleWatcher(TextView indicator) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                indicator.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }
}
