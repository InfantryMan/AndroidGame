package com.game.rk6cooperation.androidgame.Network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Api {
    public static final String BASE_URL = "http://192.168.0.111:3000";
    private static final Api INSTANCE = new Api();
    private static final Gson GSON = new GsonBuilder()
            .create();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final RetrofitApi retrofitApi;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Api() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        retrofitApi = retrofit.create(RetrofitApi.class);
    }

    public static Api getInstance() {
        return INSTANCE;
    }

    public ListenerHandler<OnUsersListGetListener> getUsersList(final Integer page, final Integer on_page, final OnUsersListGetListener listener) {
        final ListenerHandler<OnUsersListGetListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = retrofitApi.getUsersList(page, on_page).execute();

                    try (final ResponseBody responseBody = response.body()) {
                        if (response.code() != 200) {
                            throw new IOException("HTTP code " + response.code());
                        }
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseUsers(body));
                    }

                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    private ScoreboardUsers parseUsers(final String body) throws IOException {
        try {
            return GSON.fromJson(body, ScoreboardUsers.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private void invokeSuccess(final ListenerHandler<OnUsersListGetListener> handler, final ScoreboardUsers users) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUsersListGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onSuccess(users);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeError(final ListenerHandler<OnUsersListGetListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUsersListGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    public interface OnUsersListGetListener {
        void onSuccess(final ScoreboardUsers users);

        void onError(final Exception error);
    }
}
