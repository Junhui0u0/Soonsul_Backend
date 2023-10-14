package com.example.soonsul.user.oauth.response;


import com.example.soonsul.user.oauth.OAuthProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleInfoResponse implements OAuthInfoResponse{

    @JsonProperty("id")
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.APPLE;
    }
}
