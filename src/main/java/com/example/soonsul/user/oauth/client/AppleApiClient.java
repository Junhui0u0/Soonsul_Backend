package com.example.soonsul.user.oauth.client;

import com.example.soonsul.user.oauth.OAuthProvider;
import com.example.soonsul.user.oauth.param.OAuthLoginParams;
import com.example.soonsul.user.oauth.response.AppleInfoResponse;
import com.example.soonsul.user.oauth.response.OAuthInfoResponse;
import com.example.soonsul.user.oauth.token.AppleTokens;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.*;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.nimbusds.jose.crypto.ECDSASigner;

import java.io.*;
import java.security.interfaces.ECPrivateKey;
import java.util.Date;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.StringReader;

@Component
@RequiredArgsConstructor
public class AppleApiClient implements OAuthApiClient {
    private static final String GRANT_TYPE = "authorization_code";
    private static final String APPLE_AUTH_URL = "https://appleid.apple.com";

    @Value("${oauth.apple.team-id}")
    private String teamId;

    @Value("${oauth.apple.login-key}")
    private String loginKey;

    @Value("${oauth.apple.client-id}")
    private String clientId;

    @Value("${oauth.apple.redirect-url}")
    private String redirectUri;

    @Value("${oauth.apple.key-path}")
    private String keyPath;

    private final RestTemplate restTemplate;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.APPLE;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("client_secret", createClientSecret());
        body.add("redirect_uri", redirectUri);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);
        AppleTokens response = restTemplate.postForObject(APPLE_AUTH_URL + "/auth/token", request, AppleTokens.class);
        assert response != null;

        SignedJWT signedJWT = SignedJWT.parse(response.getIdToken());
        ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();

        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject payload = objectMapper.readValue(getPayload.toJSONObject().toJSONString(), JSONObject.class);

        return String.valueOf(payload.get("sub"));
    }

    @Override
    public OAuthInfoResponse requestOauthInfo(String accessToken) {
        return AppleInfoResponse.builder()
                .id(accessToken)
                .build();
    }

    private String createClientSecret() throws Exception {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(loginKey).build();
        JWTClaimsSet claimsSet = new JWTClaimsSet();

        Date now = new Date();
        claimsSet.setIssuer(teamId);
        claimsSet.setIssueTime(now);
        claimsSet.setExpirationTime(new Date(now.getTime() + 3600000));
        claimsSet.setAudience(APPLE_AUTH_URL);
        claimsSet.setSubject(clientId);

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            ECPrivateKey ecPrivateKey = convertToECPrivateKey();
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey.getS());
            jwt.sign(jwsSigner);
        } catch (Exception e) {
            throw new Exception("Failed create client secret");
        }
        return jwt.serialize();
    }

    private ECPrivateKey convertToECPrivateKey() throws Exception {
        ClassPathResource resource = new ClassPathResource(keyPath);
        String privateKey = new String(resource.getInputStream().readAllBytes());

        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

        return (ECPrivateKey) converter.getPrivateKey(object);
    }

}
