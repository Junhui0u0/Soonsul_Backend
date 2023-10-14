package com.example.soonsul.user.oauth.param;

import com.example.soonsul.user.oauth.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@Builder
@AllArgsConstructor
public class AppleParams implements OAuthLoginParams{
    private String code;

    @Override
    public OAuthProvider oAuthProvider(){
        return OAuthProvider.APPLE;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        return body;
    }
}
