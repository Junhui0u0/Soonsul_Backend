package com.example.soonsul.user.entity;

import com.example.soonsul.user.oauth.OAuthProvider;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user")
public class User {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "flag_age", nullable = false)
    private boolean flagAge;

    @Column(name = "flag_terms", nullable = false)
    private boolean flagTerms;

    @Column(name = "flag_privacy", nullable = false)
    private boolean flagPrivacy;

    @Column(name = "flag_withdrawal", nullable = false)
    private boolean flagWithdrawal;

    @Column(name = "oauth_provider")
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "period")
    private String period;

    @Column(name = "liquor")
    private String liquor;

    @Column(name = "place")
    private String place;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "flag_activity")
    private boolean flagActivity;

    @Column(name = "flag_advertising")
    private boolean flagAdvertising;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public void updateNickname(String nickname){
        this.nickname= nickname;
    }

    public void updateProfileImage(String profileImage){
        this.profileImage= profileImage;
    }

    public void updateFlagWithdrawal(boolean flagWithdrawal){
        this.flagWithdrawal= flagWithdrawal;
    }

    public void updateDeviceToken(String deviceToken){
        this.deviceToken= deviceToken;
    }

    public void updateFlagActivity(boolean flagActivity){
        this.flagActivity= flagActivity;
    }

    public void updateFlagAdvertising(boolean flagAdvertising){
        this.flagAdvertising= flagAdvertising;
    }
}