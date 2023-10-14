package com.example.soonsul.notification.dto;

import com.example.soonsul.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotification {
    private Long objectId;
    private User receiveUser;
}
