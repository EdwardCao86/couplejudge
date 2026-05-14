package com.catjudge.couplejudge.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.catjudge.couplejudge.R;
import com.catjudge.couplejudge.util.AppConstants;
import com.catjudge.couplejudge.util.PrefsManager;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PrefsManager prefsManager = new PrefsManager(this);
        ImageButton btnBack = findViewById(R.id.btnBack);
        EditText etNickname = findViewById(R.id.etNickname);
        EditText etApiKey = findViewById(R.id.etApiKey);
        RadioButton rbDeepSeek = findViewById(R.id.rbDeepSeek);
        RadioButton rbQwen = findViewById(R.id.rbQwen);
        Button btnSave = findViewById(R.id.btnSaveSettings);

        etNickname.setText(prefsManager.getNickname());
        etApiKey.setText(prefsManager.getApiKey());
        rbDeepSeek.setChecked(AppConstants.PROVIDER_DEEPSEEK.equals(prefsManager.getProvider()));
        rbQwen.setChecked(AppConstants.PROVIDER_QWEN.equals(prefsManager.getProvider()));

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            prefsManager.saveNickname(etNickname.getText().toString().trim());
            prefsManager.saveApiKey(etApiKey.getText().toString().trim());
            prefsManager.saveProvider(rbDeepSeek.isChecked() ? AppConstants.PROVIDER_DEEPSEEK : AppConstants.PROVIDER_QWEN);
            Toast.makeText(this, "设置已保存。", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
