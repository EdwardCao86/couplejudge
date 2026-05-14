package com.catjudge.couplejudge.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.model.JudgeRole;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class JudgeAdapter extends RecyclerView.Adapter<JudgeAdapter.JudgeViewHolder> {
    public interface OnJudgeClickListener {
        void onClick(JudgeRole judgeRole);
    }

    private final List<JudgeRole> judgeRoles;
    private final OnJudgeClickListener listener;
    private String selectedType;

    public JudgeAdapter(List<JudgeRole> judgeRoles, String selectedType, OnJudgeClickListener listener) {
        this.judgeRoles = judgeRoles;
        this.selectedType = selectedType;
        this.listener = listener;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JudgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_judge_card, parent, false);
        return new JudgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JudgeViewHolder holder, int position) {
        JudgeRole role = judgeRoles.get(position);
        holder.tvEmoji.setText(role.getEmoji());
        holder.tvName.setText(role.getName());
        holder.tvStyle.setText(role.getStyle());
        boolean selected = role.getType().equals(selectedType);
        holder.cardView.setStrokeWidth(selected ? 3 : 1);
        holder.cardView.setStrokeColor(holder.itemView.getContext().getColor(selected ? R.color.pink_primary : R.color.divider_soft));
        holder.itemView.setOnClickListener(v -> listener.onClick(role));
    }

    @Override
    public int getItemCount() {
        return judgeRoles.size();
    }

    static class JudgeViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvEmoji;
        TextView tvName;
        TextView tvStyle;

        JudgeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardJudge);
            tvEmoji = itemView.findViewById(R.id.tvJudgeEmoji);
            tvName = itemView.findViewById(R.id.tvJudgeName);
            tvStyle = itemView.findViewById(R.id.tvJudgeStyle);
        }
    }
}
