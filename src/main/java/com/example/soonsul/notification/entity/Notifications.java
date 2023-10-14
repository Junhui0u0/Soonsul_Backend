package com.example.soonsul.notification.entity;

import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.oauth.OAuthProvider;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="notification")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false, unique = true)
    private Long notificationId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "flag_read")
    private boolean flagRead;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "send_user_id")
    private String sendUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User receiveUser;


    public void updateFlagRead(boolean flagRead){
        this.flagRead= flagRead;
    }
}
