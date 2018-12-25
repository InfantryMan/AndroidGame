package com.game.rk6cooperation.androidgame.Network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.game.rk6cooperation.androidgame.Config;
import com.game.rk6cooperation.androidgame.UserHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static final String BASE_URL = Config.BASE_URL;
    private static final Api INSTANCE = new Api();
    private static final Gson GSON = new GsonBuilder()
            .create();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final RetrofitApi retrofitApi;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Api() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
        okHttpClient.interceptors().add(new AddCookiesInterceptor());
        okHttpClient.interceptors().add(new ReceivedCookiesInterceptor());
        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient.build())
                .build();
        retrofitApi = retrofit.create(RetrofitApi.class);
    }

    public static Api getInstance() {
        return INSTANCE;
    }

    class ReceivedCookiesInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                for (String header : originalResponse.headers("Set-Cookie")) {
                    Pattern pattern = Pattern.compile("(sessionid=[a-zA-Z0-9]+;)");
                    Matcher matcher = pattern.matcher(header);
                    if (matcher.find())
                    {
                        String sessionId = matcher.group(1);
                        UserHolder.getUserHolder().setCookie(sessionId);
                    }
                }
            }
            return originalResponse;
        }
    }

    public class AddCookiesInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
            okhttp3.Request.Builder builder = chain.request().newBuilder();
            String cookie = UserHolder.getUserHolder().getCookie();
            if (cookie != null) {
                builder.addHeader("Cookie", cookie);
            }
            return chain.proceed(builder.build());
        }
    }

    public ListenerHandler<OnAuthorizeListener> authorize(final String nickname, final String password, final OnAuthorizeListener listener) {
        final ListenerHandler<OnAuthorizeListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AuthUserRequest authUserRequest = new AuthUserRequest(nickname, password);
                    final Response<ResponseBody> response = retrofitApi.login(authUserRequest).execute();

                    try (final ResponseBody responseBody = response.body()) {
                        if (response.code() == 400) {
                            invokeInvalidAuth(handler);
                        }
                        else if (response.code() != 200) {
                            throw new IOException("HTTP code " + response.code());
                        }
                        else if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        else {
                            final String body = responseBody.string();
                            invokeSuccessAuth(handler, parseAuthUser(body));
                        }
                    }

                } catch (IOException e) {
                    invokeErrorAuth(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnRegisterListener> register(final String nickname, final String password, final OnRegisterListener listener) {
        final ListenerHandler<OnRegisterListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RegUserRequest authUserRequest = new RegUserRequest(nickname, password);
                    final Response<ResponseBody> response = retrofitApi.register(authUserRequest).execute();

                    try (final ResponseBody responseBody = response.body()) {
                        if (response.code() == 400) {
                            invokeInvalidRegister(handler);
                        }
                        else if (response.code() != 200) {
                            throw new IOException("HTTP code " + response.code());
                        }
                        else if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        else {
                            final String body = responseBody.string();
                            invokeSuccessReg(handler, parseRegUser(body));
                        }
                    }

                } catch (IOException e) {
                    invokeErrorReg(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnCheckAuthListener> checkAuth(final OnCheckAuthListener listener) {
        final ListenerHandler<OnCheckAuthListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = retrofitApi.checkAuth().execute();

                    try (final ResponseBody responseBody = response.body()) {
                        if (response.code() == 401) {
                            invokeSessionInvalidCheckAuth(handler);
                        }
                        else if (response.code() != 200) {
                            throw new IOException("HTTP code " + response.code());
                        }
                        else if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        invokeSuccessCheckAuth(handler);
                    }

                } catch (IOException e) {
                    invokeErrorCheckAuth(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnLogoutListener> logout(final OnLogoutListener listener) {
        final ListenerHandler<OnLogoutListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = retrofitApi.logout().execute();

                    try (final ResponseBody responseBody = response.body()) {
                        if (response.code() != 200) {
                            throw new IOException("HTTP code " + response.code());
                        }
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        invokeSuccessLogout(handler);
                    }

                } catch (IOException e) {
                    invokeErrorLogout(handler, e);
                }
            }
        });
        return handler;
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
                        invokeSuccessScoreboard(handler, parseScoreboardUsers(body));
                    }

                } catch (IOException e) {
                    invokeErrorScoreboard(handler, e);
                }
            }
        });
        return handler;
    }


    private ScoreboardUsers parseScoreboardUsers(final String body) throws IOException {
        try {
            return GSON.fromJson(body, ScoreboardUsers.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private AuthUserResponse parseAuthUser(final String body) throws IOException {
        try {
            return GSON.fromJson(body, AuthUserResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private RegUserResponse parseRegUser(final String body) throws IOException {
        try {
            return GSON.fromJson(body, RegUserResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private void invokeSuccessScoreboard(final ListenerHandler<OnUsersListGetListener> handler, final ScoreboardUsers users) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUsersListGetListener listener = handler.getListener();
                if (listener != null) {
                    listener.onSuccess(users);
                } else {
                }
            }
        });
    }

    private void invokeErrorScoreboard(final ListenerHandler<OnUsersListGetListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUsersListGetListener listener = handler.getListener();
            }
        });
    }


    private void invokeSuccessAuth(final ListenerHandler<OnAuthorizeListener> handler, final AuthUserResponse user) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnAuthorizeListener listener = handler.getListener();
                if (listener != null) {
                    listener.onSuccess(user);
                }
            }
        });
    }

    private void invokeErrorAuth(final ListenerHandler<OnAuthorizeListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnAuthorizeListener listener = handler.getListener();
                if (listener != null) {
                    listener.onError(error);
                }
            }
        });
    }

    private void invokeInvalidAuth(final ListenerHandler<OnAuthorizeListener> handler) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnAuthorizeListener listener = handler.getListener();
                if (listener != null) {
                    listener.onSessionInvalid();
                }
            }
        });
    }

    private void invokeInvalidRegister(final ListenerHandler<OnRegisterListener> handler) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnRegisterListener listener = handler.getListener();
                if (listener != null) {
                    listener.onRegisterInvalid();
                }
            }
        });
    }

    private void invokeSuccessReg(final ListenerHandler<OnRegisterListener> handler, final RegUserResponse user) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnRegisterListener listener = handler.getListener();
                if (listener != null) {
                    listener.onSuccess(user);
                }
            }
        });
    }

    private void invokeErrorReg(final ListenerHandler<OnRegisterListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnRegisterListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }


    private void invokeSuccessCheckAuth(final ListenerHandler<OnCheckAuthListener> handler) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnCheckAuthListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onSuccess();
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeErrorCheckAuth(final ListenerHandler<OnCheckAuthListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnCheckAuthListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeSessionInvalidCheckAuth(final ListenerHandler<OnCheckAuthListener> handler) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnCheckAuthListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onSessionInvalid();
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }


    private void invokeSuccessLogout(final ListenerHandler<OnLogoutListener> handler) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnLogoutListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onSuccess();
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeErrorLogout(final ListenerHandler<OnLogoutListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnLogoutListener listener = handler.getListener();
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

    public interface OnAuthorizeListener {
        void onSuccess(final AuthUserResponse user);
        void onSessionInvalid();
        void onError(final Exception error);
    }

    public interface OnRegisterListener {
        void onSuccess(final RegUserResponse user);
        void onRegisterInvalid();
        void onError(final Exception error);
    }

    public interface OnCheckAuthListener {
        void onSuccess();
        void onSessionInvalid();
        void onError(final Exception error);
    }

    public interface OnLogoutListener {
        void onSuccess();

        void onError(final Exception error);
    }
}
