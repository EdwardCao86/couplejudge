package com.catjudge.couplejudge.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREFS_NAME = "cat_couple_judge_prefs";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_PROVIDER = "provider";
    private static final String KEY_API_KEY = "api_key";

    private final SharedPreferences preferences;

    public PrefsManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getNickname() {
        return preferences.getString(KEY_NICKNAME, "恋爱中的你");
    }

    public void saveNickname(String nickname) {
        preferences.edit().putString(KEY_NICKNAME, nickname).apply();
    }

    public String getProvider() {
        return preferences.getString(KEY_PROVIDER, AppConstants.PROVIDER_DEEPSEEK);
    }

    public void saveProvider(String provider) {
        preferences.edit().putString(KEY_PROVIDER, provider).apply();
    }

    public String getApiKey() {
        return preferences.getString(KEY_API_KEY, "");
    }

    public void saveApiKey(String apiKey) {
        preferences.edit().putString(KEY_API_KEY, apiKey).apply();
    }
}
