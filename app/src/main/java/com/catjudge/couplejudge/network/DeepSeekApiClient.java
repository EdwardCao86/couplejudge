package com.catjudge.couplejudge.network;

import androidx.annotation.NonNull;

import com.catjudge.couplejudge.util.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeepSeekApiClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(25, TimeUnit.SECONDS)
            .build();

    public interface AiCallback {
        void onSuccess(String content);

        void onFailure(String message);
    }

    public void requestJudgment(String provider, String apiKey, String systemPrompt, String userPrompt, AiCallback callback) {
        if (!AppConstants.PROVIDER_DEEPSEEK.equals(provider)) {
            callback.onFailure("当前版本仅接入 DeepSeek，请先在设置中切换为 DeepSeek。");
            return;
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            callback.onFailure("请先在设置中填写 DeepSeek API Key。");
            return;
        }
        try {
            JSONObject payload = new JSONObject();
            payload.put("model", "deepseek-chat");
            payload.put("stream", false);
            payload.put("temperature", 0.7);

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
            messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
            payload.put("messages", messages);

            Request request = new Request.Builder()
                    .url("https://api.deepseek.com/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey.trim())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(payload.toString(), JSON))
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String raw = response.body() == null ? "" : response.body().string();
                    if (!response.isSuccessful()) {
                        callback.onFailure("AI 请求失败：" + response.code());
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(raw);
                        JSONArray choices = jsonObject.optJSONArray("choices");
                        if (choices == null || choices.length() == 0) {
                            callback.onFailure("AI 没有返回可用判决内容。");
                            return;
                        }
                        JSONObject message = choices.getJSONObject(0).optJSONObject("message");
                        String content = message == null ? "" : message.optString("content");
                        callback.onSuccess(content);
                    } catch (JSONException e) {
                        callback.onFailure("解析 AI 返回内容失败。");
                    }
                }
            });
        } catch (JSONException e) {
            callback.onFailure("构建 AI 请求失败。");
        }
    }
}
