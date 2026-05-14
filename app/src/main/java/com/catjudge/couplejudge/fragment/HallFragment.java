package com.catjudge.couplejudge.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.activity.JudgmentResultActivity;
import com.catjudge.couplejudge.activity.PerspectiveInputActivity;
import com.catjudge.couplejudge.adapter.JudgeAdapter;
import com.catjudge.couplejudge.data.CasesRepository;
import com.catjudge.couplejudge.model.CaseRecord;
import com.catjudge.couplejudge.model.JudgeRole;
import com.catjudge.couplejudge.util.AppConstants;
import com.catjudge.couplejudge.util.CategoryClassifier;

import java.util.List;

public class HallFragment extends Fragment {
    private static final String ARG_CLEAR_FORM = "clear_form";
    private String selectedJudgeType = "xiaoju";
    private String selectedMode = AppConstants.MODE_SINGLE;

    public static HallFragment newInstance(boolean clearForm) {
        HallFragment fragment = new HallFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_CLEAR_FORM, clearForm);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hall, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvJudges = view.findViewById(R.id.rvJudges);
        Button btnModeSingle = view.findViewById(R.id.btnModeSingle);
        Button btnModeDouble = view.findViewById(R.id.btnModeDouble);
        EditText etEvent = view.findViewById(R.id.etEvent);
        EditText etMood = view.findViewById(R.id.etMood);
        TextView tvExtraTitle = view.findViewById(R.id.tvExtraTitle);
        Button btnContinue = view.findViewById(R.id.btnContinue);
        LinearLayout layoutResumeCase = view.findViewById(R.id.layoutResumeCase);
        TextView tvResumeSummary = view.findViewById(R.id.tvResumeSummary);
        Button btnResumeCase = view.findViewById(R.id.btnResumeCase);
        CasesRepository repository = new CasesRepository(requireContext());

        List<JudgeRole> judgeRoles = AppConstants.getJudgeRoles();
        final JudgeAdapter[] adapterHolder = new JudgeAdapter[1];
        adapterHolder[0] = new JudgeAdapter(judgeRoles, selectedJudgeType, judgeRole -> {
            selectedJudgeType = judgeRole.getType();
            adapterHolder[0].setSelectedType(selectedJudgeType);
        });
        JudgeAdapter judgeAdapter = adapterHolder[0];
        rvJudges.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvJudges.setAdapter(judgeAdapter);
        updateModeButtons(btnModeSingle, btnModeDouble);
        btnModeSingle.setOnClickListener(v -> {
            selectedMode = AppConstants.MODE_SINGLE;
            updateModeButtons(btnModeSingle, btnModeDouble);
            updateExtraFieldCopy(tvExtraTitle, etMood);
        });
        btnModeDouble.setOnClickListener(v -> {
            selectedMode = AppConstants.MODE_DOUBLE;
            updateModeButtons(btnModeSingle, btnModeDouble);
            updateExtraFieldCopy(tvExtraTitle, etMood);
        });
        updateExtraFieldCopy(tvExtraTitle, etMood);
        if (getArguments() != null && getArguments().getBoolean(ARG_CLEAR_FORM, false)) {
            clearInputForm(etEvent, etMood, btnModeSingle, btnModeDouble, tvExtraTitle);
        }

        btnContinue.setOnClickListener(v -> {
            String event = etEvent.getText().toString().trim();
            String mood = etMood.getText().toString().trim();
            if (TextUtils.isEmpty(event)) {
                Toast.makeText(requireContext(), "请先填写事件描述。", Toast.LENGTH_SHORT).show();
                return;
            }
            String mode = selectedMode;
            String extraPrefix = AppConstants.MODE_SINGLE.equals(mode)
                    ? "💭 我还想说："
                    : "💭 还有这些补充：";
            String mergedEvent = mood.isEmpty() ? event : event + "\n\n" + extraPrefix + mood;
            long caseId = new CasesRepository(requireContext()).createDraft(
                    selectedJudgeType,
                    mode,
                    mergedEvent,
                    CategoryClassifier.classify(event),
                    AppConstants.STATUS_DRAFT_A
            );
            Intent intent = new Intent(requireContext(), PerspectiveInputActivity.class);
            intent.putExtra(PerspectiveInputActivity.EXTRA_CASE_ID, caseId);
            startActivity(intent);
        });

        bindResumeCase(repository, layoutResumeCase, tvResumeSummary, btnResumeCase);
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view == null) {
            return;
        }
        bindResumeCase(
                new CasesRepository(requireContext()),
                view.findViewById(R.id.layoutResumeCase),
                view.findViewById(R.id.tvResumeSummary),
                view.findViewById(R.id.btnResumeCase)
        );
    }

    private void updateModeButtons(Button btnModeSingle, Button btnModeDouble) {
        boolean isSingleMode = AppConstants.MODE_SINGLE.equals(selectedMode);
        btnModeSingle.setBackgroundResource(isSingleMode ? R.drawable.bg_primary_button : R.drawable.bg_secondary_button);
        btnModeDouble.setBackgroundResource(isSingleMode ? R.drawable.bg_secondary_button : R.drawable.bg_primary_button);
        btnModeSingle.setBackgroundTintList(null);
        btnModeDouble.setBackgroundTintList(null);
        btnModeSingle.setTextColor(requireContext().getColor(isSingleMode ? R.color.white : R.color.pink_primary));
        btnModeDouble.setTextColor(requireContext().getColor(isSingleMode ? R.color.pink_primary : R.color.white));
    }

    private void updateExtraFieldCopy(TextView tvExtraTitle, EditText etMood) {
        boolean isSingleMode = AppConstants.MODE_SINGLE.equals(selectedMode);
        tvExtraTitle.setText(isSingleMode ? "💭 还有什么想说的" : "💭 还有什么想补充的");
        etMood.setHint(isSingleMode
                ? "可以补充你的感受、委屈，或还有什么想说的..."
                : "可以补充双方背景、当时气氛，或还有什么想说明的...");
    }

    private void clearInputForm(EditText etEvent, EditText etMood, Button btnModeSingle,
                                Button btnModeDouble, TextView tvExtraTitle) {
        etEvent.setText("");
        etMood.setText("");
        selectedMode = AppConstants.MODE_SINGLE;
        updateModeButtons(btnModeSingle, btnModeDouble);
        updateExtraFieldCopy(tvExtraTitle, etMood);
        Bundle args = getArguments();
        if (args != null) {
            args.putBoolean(ARG_CLEAR_FORM, false);
        }
    }

    private void bindResumeCase(CasesRepository repository, LinearLayout layoutResumeCase,
                                TextView tvResumeSummary, Button btnResumeCase) {
        CaseRecord ongoingCase = repository.getLatestOngoingCase();
        if (ongoingCase == null) {
            layoutResumeCase.setVisibility(View.GONE);
            return;
        }
        layoutResumeCase.setVisibility(View.VISIBLE);
        tvResumeSummary.setText(ongoingCase.getStatus() + " ｜ " + ongoingCase.getEventDescription());
        btnResumeCase.setOnClickListener(v -> {
            Intent intent;
            if (ongoingCase.getJudgmentResult() != null
                    && !ongoingCase.getJudgmentResult().trim().isEmpty()) {
                intent = new Intent(requireContext(), JudgmentResultActivity.class);
                intent.putExtra(JudgmentResultActivity.EXTRA_CASE_ID, ongoingCase.getId());
            } else {
                intent = new Intent(requireContext(), PerspectiveInputActivity.class);
                intent.putExtra(PerspectiveInputActivity.EXTRA_CASE_ID, ongoingCase.getId());
            }
            startActivity(intent);
        });
    }
}
