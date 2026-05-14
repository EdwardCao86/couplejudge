package com.catjudge.couplejudge.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.activity.CaseDetailActivity;
import com.catjudge.couplejudge.activity.HistoryCasesActivity;
import com.catjudge.couplejudge.adapter.CaseAdapter;
import com.catjudge.couplejudge.data.CasesRepository;

public class NotebookFragment extends Fragment {
    private CaseAdapter caseAdapter;
    private CasesRepository repository;
    private TextView tvNotebookSummary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notebook, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new CasesRepository(requireContext());
        tvNotebookSummary = view.findViewById(R.id.tvNotebookSummary);
        Button btnHistory = view.findViewById(R.id.btnHistory);
        RecyclerView rvCases = view.findViewById(R.id.rvCases);
        rvCases.setLayoutManager(new LinearLayoutManager(requireContext()));
        caseAdapter = new CaseAdapter(true, new CaseAdapter.OnCaseActionListener() {
            @Override
            public void onOpen(com.catjudge.couplejudge.model.CaseRecord caseRecord) {
                Intent intent = new Intent(requireContext(), CaseDetailActivity.class);
                intent.putExtra(CaseDetailActivity.EXTRA_CASE_ID, caseRecord.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(com.catjudge.couplejudge.model.CaseRecord caseRecord) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("删除错题")
                        .setMessage("确认把这条错题从错题本中移除吗？历史案件里仍然可以看到。")
                        .setPositiveButton("删除", (dialog, which) -> {
                            repository.removeFromNotebook(caseRecord.getId());
                            bindData();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        rvCases.setAdapter(caseAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 不直接删除，仅用于承接左滑手势
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 2f;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                CaseAdapter.CaseViewHolder holder = (CaseAdapter.CaseViewHolder) viewHolder;
                float currentX = holder.layoutForeground.getTranslationX();
                int position = viewHolder.getBindingAdapterPosition();
                if (currentX <= -holder.getRevealWidth() / 2f && position != RecyclerView.NO_POSITION) {
                    holder.layoutForeground.animate().translationX(-holder.getRevealWidth()).setDuration(160).start();
                    caseAdapter.setOpenedPosition(position);
                } else {
                    holder.layoutForeground.animate().translationX(0f).setDuration(160).start();
                    caseAdapter.closeOpenedItem();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                CaseAdapter.CaseViewHolder holder = (CaseAdapter.CaseViewHolder) viewHolder;
                float clampedX = Math.max(-holder.getRevealWidth(), Math.min(0, dX));
                holder.layoutForeground.setTranslationX(clampedX);
            }
        }).attachToRecyclerView(rvCases);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), HistoryCasesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (repository != null) {
            bindData();
        }
    }

    private void bindData() {
        caseAdapter.submitList(repository.getArchivedCases());
        tvNotebookSummary.setText("已存入错题本 " + repository.getArchivedCases().size() + " 条");
    }
}
