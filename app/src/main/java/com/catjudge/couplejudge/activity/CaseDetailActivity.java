package com.catjudge.couplejudge.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.data.CasesRepository;
import com.catjudge.couplejudge.model.CaseRecord;
import com.catjudge.couplejudge.util.AppConstants;

public class CaseDetailActivity extends AppCompatActivity {
    public static final String EXTRA_CASE_ID = "case_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvMeta = findViewById(R.id.tvMeta);
        TextView tvEvent = findViewById(R.id.tvEvent);
        TextView tvContent = findViewById(R.id.tvContent);

        CaseRecord caseRecord = new CasesRepository(this)
                .getCaseById(getIntent().getLongExtra(EXTRA_CASE_ID, -1L));
        if (caseRecord == null) {
            finish();
            return;
        }
        btnBack.setOnClickListener(v -> finish());
        String judgeName = AppConstants.findJudgeByType(caseRecord.getJudgeType()).getName();
        tvMeta.setText(caseRecord.getCreatedAt() + " ｜ " + caseRecord.getCategory() + " ｜ " + judgeName + "法官");
        tvEvent.setText("事件描述：\n" + caseRecord.getEventDescription()
                + "\n\nA 方视角：\n" + safe(caseRecord.getViewA())
                + "\n\nB 方视角：\n" + safe(caseRecord.getViewB()));
        tvContent.setText(caseRecord.getJudgmentResult() == null || caseRecord.getJudgmentResult().trim().isEmpty()
                ? "这条记录还在进行中，暂未生成完整判决书。"
                : caseRecord.getJudgmentResult());
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "暂无内容" : value;
    }
}
