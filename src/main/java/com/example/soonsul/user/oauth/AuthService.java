package com.example.soonsul.user.oauth;

import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.exception.WithdrawalUser;
import com.example.soonsul.user.oauth.dto.ValidationDto;
import com.example.soonsul.user.oauth.jwt.JwtTokenProvider;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.repository.UserRepository;
import com.example.soonsul.user.exception.RefreshTokenExpired;
import com.example.soonsul.user.exception.UserNotExist;
import com.example.soonsul.user.oauth.dto.SignupDto;
import com.example.soonsul.user.oauth.dto.TokenDto;
import com.example.soonsul.user.oauth.param.OAuthLoginParams;
import com.example.soonsul.user.redis.RefreshToken;
import com.example.soonsul.user.redis.RefreshTokenRepository;
import com.example.soonsul.user.oauth.response.OAuthInfoResponse;
import com.example.soonsul.util.RandomNickName;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public TokenDto login(OAuthLoginParams params) throws Exception {
        final OAuthInfoResponse response = requestOAuthInfoService.request(params);
        final String oauthId= response.getId();
        final OAuthProvider oauthProvider= params.oAuthProvider();
        final Optional<User> user= userRepository.findById(oauthId);

        if(user.isPresent()) {          //이미 가입된 사용자
            if(user.get().isFlagWithdrawal()) throw new WithdrawalUser("withdrawal user", ErrorCode.WITHDRAWAL_USER);
            final RefreshToken refreshToken= jwtTokenProvider.generateJwtRefreshToken(user.get());
            refreshTokenRepository.save(refreshToken);
            return TokenDto.builder()
                    .accessToken(jwtTokenProvider.generateJwtToken(user.get()))
                    .refreshToken(refreshToken.getRefreshToken())
                    .nickname(user.get().getNickname())
                    .profileImage(user.get().getProfileImage())
                    .oauthId(oauthId)
                    .oauthProvider(oauthProvider)
                    .build();
        }
        else{                          //신규가입자
            return TokenDto.builder()
                    .oauthId(oauthId)
                    .oauthProvider(oauthProvider)
                    .build();
        }
    }


    @Transactional
    public TokenDto signup(SignupDto signupDto){
        final User user= User.builder()
                .userId(signupDto.getOauthId())
                .nickname(RandomNickName.generate().toString())
                .profileImage(null)
                .phoneNumber(null)
                .gender(signupDto.getGender())
                .age(getAgeGroup(LocalDate.now(), signupDto.getBirthday()))
                .flagAge(signupDto.isFlagAge())
                .flagTerms(signupDto.isFlagTerms())
                .flagPrivacy(signupDto.isFlagPrivacy())
                .flagWithdrawal(false)
                .oAuthProvider(OAuthProvider.valueOf(signupDto.getOauthProvider()))
                .birthday(signupDto.getBirthday())
                .period(signupDto.getPeriod())
                .liquor(signupDto.getLiquor())
                .place(signupDto.getPlace())
                .flagActivity(signupDto.isFlagActivity())
                .flagAdvertising(signupDto.isFlagAdvertising())
                .createdDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        return TokenDto.builder()
                .accessToken(jwtTokenProvider.generateJwtToken(user))
                .refreshToken(jwtTokenProvider.generateJwtRefreshToken(user).getRefreshToken())
                .build();
    }

    //New Access Token 재발급
    @Transactional
    public String refreshToken(String refreshToken) {
        Optional<RefreshToken> getRefreshToken= refreshTokenRepository.findById(refreshToken);
        if(getRefreshToken.isPresent()){
            User user= userRepository.findById(getRefreshToken.get().getUserId())
                    .orElseThrow(()-> new UserNotExist("user not exist", ErrorCode.USER_NOT_EXIST));
            return jwtTokenProvider.generateJwtToken(user);
        }
        else{
            throw new RefreshTokenExpired("refresh token expired", ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
    }


    //액세스토큰 유효성 검사
    @Transactional(readOnly = true)
    public ValidationDto isValidToken(String token) {
        final boolean flag= jwtTokenProvider.isValidToken(token);
        if(flag) {
            final User user= userRepository.findById(jwtTokenProvider.getUserIdFromToken(token))
                    .orElseThrow(()-> new UserNotExist("user not exist", ErrorCode.USER_NOT_EXIST));
            return ValidationDto.builder()
                    .flagValidation(true)
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .build();
        }
        return ValidationDto.builder()
                .flagValidation(false)
                .build();
    }


    @Transactional
    public void withdrawal() {
        final User user= userUtil.getUserByAuthentication();
        user.updateFlagWithdrawal(true);
    }


    @Transactional
    public void postDeviceToken(String deviceToken) {
        final User user= userUtil.getUserByAuthentication();
        user.updateDeviceToken(deviceToken);
    }


    private Integer getAgeGroup(LocalDate now, LocalDate birthday){
        final int age= now.getYear()- birthday.getYear()+ 1;
        if(age>=20 && age<=29) return 20;
        else if(age>=30 && age<=39) return 30;
        else if(age>=40 && age<=49) return 40;
        else if(age>=50 && age<=59) return 50;
        else if(age>=60) return 60;
        else return 0;
    }
}