package com.example.soonsul.user.oauth.client;


import com.example.soonsul.user.oauth.OAuthProvider;
import com.example.soonsul.user.oauth.param.OAuthLoginParams;
import com.example.soonsul.user.oauth.response.OAuthInfoResponse;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params) throws Exception;
    OAuthInfoResponse requestOauthInfo(String accessToken);
}