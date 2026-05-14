package com.catjudge.couplejudge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.adapter.CaseAdapter;
import com.catjudge.couplejudge.data.CasesRepository;
import com.catjudge.couplejudge.model.CaseRecord;
import com.catjudge.couplejudge.util.AppConstants;

public class HistoryCasesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_cases);

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvHistorySummary = findViewById(R.id.tvHistorySummary);
        RecyclerView rvHistoryCases = findViewById(R.id.rvHistoryCases);
        CasesRepository repository = new CasesRepository(this);

        btnBack.setOnClickListener(v -> finish());
        rvHistoryCases.setLayoutManager(new LinearLayoutManager(this));
        rvHistoryCases.setAdapter(new CaseAdapter(false, new CaseAdapter.OnCaseActionListener() {
            @Override
            public void onOpen(CaseRecord caseRecord) {
                Intent intent;
                if (AppConstants.STATUS_JUDGED.equals(caseRecord.getStatus())) {
                    intent = new Intent(HistoryCasesActivity.this, CaseDetailActivity.class);
                    intent.putExtra(CaseDetailActivity.EXTRA_CASE_ID, caseRecord.getId());
                } else if (caseRecord.getJudgmentResult() != null && !caseRecord.getJudgmentResult().trim().isEmpty()) {
                    intent = new Intent(HistoryCasesActivity.this, JudgmentResultActivity.class);
                    intent.putExtra(JudgmentResultActivity.EXTRA_CASE_ID, caseRecord.getId());
                } else {
                    intent = new Intent(HistoryCasesActivity.this, PerspectiveInputActivity.class);
                    intent.putExtra(PerspectiveInputActivity.EXTRA_CASE_ID, caseRecord.getId());
                }
                startActivity(intent);
            }

            @Override
            public void onDelete(CaseRecord caseRecord) {
                // 历史案件页不提供删除入口
            }
        }));
        ((CaseAdapter) rvHistoryCases.getAdapter()).submitList(repository.getCases(null));
        tvHistorySummary.setText("全部历史案件 " + repository.getCases(null).size() + " 条，含已完成和进行中");
    }
}
