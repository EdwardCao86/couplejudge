package com.catjudge.couplejudge.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.activity.SettingsActivity;
import com.catjudge.couplejudge.util.PrefsManager;

public class ProfileFragment extends Fragment {
    private PrefsManager prefsManager;
    private TextView tvNickname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefsManager = new PrefsManager(requireContext());
        tvNickname = view.findViewById(R.id.tvNickname);
        Button btnAvatar = view.findViewById(R.id.btnAvatar);
        Button btnSettingsPage = view.findViewById(R.id.btnSettingsPage);

        bindProfileInfo();
        btnAvatar.setOnClickListener(v ->
                Toast.makeText(requireContext(), "头像入口已预留，当前版本暂不接入上传。", Toast.LENGTH_SHORT).show());
        btnSettingsPage.setOnClickListener(v -> startActivity(new Intent(requireContext(), SettingsActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefsManager != null && tvNickname != null) {
            bindProfileInfo();
        }
    }

    private void bindProfileInfo() {
        tvNickname.setText(prefsManager.getNickname());
    }
}
