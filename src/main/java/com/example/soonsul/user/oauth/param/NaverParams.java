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
public class NaverParams implements OAuthLoginParams {
    private String code;
    private String state;
    //private String error;
    //private String error_description;


    @Override
    public OAuthProvider oAuthProvider(){
        return OAuthProvider.NAVER;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("state", state);
        return body;
    }
}