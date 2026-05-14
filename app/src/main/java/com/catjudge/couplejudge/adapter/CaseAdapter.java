package com.catjudge.couplejudge.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.model.CaseRecord;
import com.catjudge.couplejudge.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseViewHolder> {
    public interface OnCaseActionListener {
        void onOpen(CaseRecord caseRecord);

        void onDelete(CaseRecord caseRecord);
    }

    private final List<CaseRecord> caseRecords = new ArrayList<>();
    private final OnCaseActionListener listener;
    private final boolean deleteEnabled;
    private int openedPosition = RecyclerView.NO_POSITION;

    public CaseAdapter(boolean deleteEnabled, OnCaseActionListener listener) {
        this.deleteEnabled = deleteEnabled;
        this.listener = listener;
    }

    public void submitList(List<CaseRecord> records) {
        caseRecords.clear();
        caseRecords.addAll(records);
        openedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public CaseRecord getItem(int position) {
        return caseRecords.get(position);
    }

    public void setOpenedPosition(int position) {
        int previousPosition = openedPosition;
        openedPosition = position;
        if (previousPosition != RecyclerView.NO_POSITION && previousPosition != position) {
            notifyItemChanged(previousPosition);
        }
        if (position != RecyclerView.NO_POSITION) {
            notifyItemChanged(position);
        }
    }

    public void closeOpenedItem() {
        if (openedPosition != RecyclerView.NO_POSITION) {
            int previousPosition = openedPosition;
            openedPosition = RecyclerView.NO_POSITION;
            notifyItemChanged(previousPosition);
        }
    }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_case_card, parent, false);
        return new CaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
        CaseRecord record = caseRecords.get(position);
        holder.tvDate.setText(record.getCreatedAt());
        String judgeName = AppConstants.findJudgeByType(record.getJudgeType()).getName();
        holder.tvMeta.setText(record.getCategory() + " ｜ " + judgeName + "法官 ｜ " + record.getStatus());
        holder.tvSummary.setText(record.getEventDescription());
        holder.layoutDeleteBackground.setVisibility(deleteEnabled ? View.VISIBLE : View.GONE);
        holder.layoutForeground.setTranslationX(deleteEnabled && openedPosition == position
                ? -holder.getRevealWidth()
                : 0f);
        holder.itemView.setOnClickListener(v -> {
            if (deleteEnabled && openedPosition == position) {
                closeOpenedItem();
                return;
            }
            listener.onOpen(record);
        });
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(record));
    }

    @Override
    public int getItemCount() {
        return caseRecords.size();
    }

    public static class CaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvMeta;
        TextView tvSummary;
        ImageView ivChevron;
        Button btnDelete;
        public LinearLayout layoutForeground;
        LinearLayout layoutDeleteBackground;

        CaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            ivChevron = itemView.findViewById(R.id.ivChevron);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            layoutForeground = itemView.findViewById(R.id.layoutForeground);
            layoutDeleteBackground = itemView.findViewById(R.id.layoutDeleteBackground);
        }

        public int getRevealWidth() {
            return layoutDeleteBackground.getLayoutParams().width;
        }
    }
}
