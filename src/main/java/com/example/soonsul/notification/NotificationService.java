package com.example.soonsul.notification;

import com.example.soonsul.liquor.entity.Comment;
import com.example.soonsul.liquor.entity.Review;
import com.example.soonsul.liquor.repository.CommentRepository;
import com.example.soonsul.liquor.repository.ReviewGoodRepository;
import com.example.soonsul.notification.dto.NotificationDto;
import com.example.soonsul.notification.dto.PushNotification;
import com.example.soonsul.notification.entity.NotificationType;
import com.example.soonsul.notification.entity.Notifications;
import com.example.soonsul.notification.exception.NotificationNotExist;
import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.util.UserUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.util.Pair;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserUtil userUtil;
    private final NotificationRepository notificationRepository;
    private final ReviewGoodRepository reviewGoodRepository;
    private final CommentRepository commentRepository;


    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationList(Pageable pageable){
        final User user= userUtil.getUserByAuthentication();
        final List<Notifications> list= notificationRepository.findAllByUser(pageable, user.getUserId()).toList();

        final List<NotificationDto> result= new ArrayList<>();
        for(Notifications notification: list){
            final String date= dateConversion(notification.getDate());
            final Pair<Long, String> object= getObject(notification.getType(), notification.getObjectId());
            final NotificationDto dto= NotificationDto.builder()
                    .notificationId(notification.getNotificationId())
                    .content(notification.getContent())
                    .date(date)
                    .time(date!=null ? timeConversion(notification.getDate()) : null)
                    .flagRead(notification.isFlagRead())
                    .objectId(object!=null ? object.getFirst() : null)
                    .type(notification.getType())
                    .sendUserId(notification.getSendUserId())
                    .objectContent(object != null ? object.getSecond() : null)
                    .build();
            result.add(dto);
        }
        return result.stream().filter(dto-> dto.getDate()!=null).collect(Collectors.toList());
    }

    private String dateConversion(LocalDateTime request){
        final LocalDate notificationDate= request.toLocalDate();

        final LocalDate today = LocalDateTime.now().toLocalDate();
        final LocalDate yesterday = today.minusDays(1);
        final LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        final LocalDate startOfMonth = today.withDayOfMonth(1);

        if (!notificationDate.isBefore(today)) return "오늘";
        else if (!notificationDate.isBefore(yesterday)) return "어제";
        else if (!notificationDate.isBefore(startOfWeek)) return "이번주";
        else if (!notificationDate.isBefore(startOfMonth)) return "이번달";
        else return null;
    }

    private String timeConversion(LocalDateTime request){
        final LocalDateTime now= LocalDateTime.now();
        final long subSecond= ChronoUnit.SECONDS.between(request, now);

        if(subSecond<=60) return "지금";
        else if(subSecond<=3600){
            int min= Long.valueOf(subSecond).intValue()/60;
            return min+"분";
        }
        else if(subSecond<=86400){
            int min= Long.valueOf(subSecond).intValue()/3600;
            return min+"시간";
        }
        else {
            int min= Long.valueOf(subSecond).intValue()/86400;
            return min+"일";
        }
    }

    private Pair<Long, String> getObject(NotificationType type, Long objectId){
        switch (type){
            case REVIEW_GOOD:
                final Review review= reviewGoodRepository.findById(objectId).get().getReview();
                return Pair.of(review.getReviewId(), review.getContent());
            case COMMENT:
            case RECOMMENT:
                final Comment comment= commentRepository.findById(objectId).get();
                return Pair.of(comment.getCommentId(), comment.getContent());
            case PROMOTION:
                return Pair.of(objectId, "");
            default:
                return null;
        }
    }


    @Transactional(readOnly = true)
    public boolean getNewNotification(){
        final User user= userUtil.getUserByAuthentication();
        return !notificationRepository.findNewNotification(user.getUserId()).isEmpty();
    }


    @Transactional
    public void putNewNotification(List<Long> idList){
        for(Long id: idList){
            final Notifications notification= notificationRepository.findById(id)
                    .orElseThrow(()-> new NotificationNotExist("notification not exist", ErrorCode.NOTIFICATION_NOT_EXIST));
            notification.updateFlagRead(true);
        }
    }


    @Transactional
    public void sendNotification(NotificationType type, PushNotification pushNotification) throws FirebaseMessagingException {
        switch (type) {
            case FOLLOW:
            case REVIEW_GOOD:
            case COMMENT:
            case RECOMMENT:
                if(!pushNotification.getReceiveUser().isFlagActivity()) return;
                sendActivityNotification(type, pushNotification);
            case PROMOTION:
                if(!pushNotification.getReceiveUser().isFlagAdvertising()) return;
                sendAdvertisingNotification(type, pushNotification);
            default:
        }
    }

    private void sendActivityNotification(NotificationType type, PushNotification pushNotification) throws FirebaseMessagingException{
        final String token = pushNotification.getReceiveUser().getDeviceToken();
        final User user= userUtil.getUserByAuthentication();
        if(pushNotification.getReceiveUser()==user) return;
        final String content = getContent(type, user.getNickname());

        saveNotifications(content, type, pushNotification.getObjectId(), user.getUserId(), pushNotification.getReceiveUser());
        sendPersonalAlarm(content, token);
    }

    private void sendAdvertisingNotification(NotificationType type, PushNotification pushNotification) throws FirebaseMessagingException{
        final String token = pushNotification.getReceiveUser().getDeviceToken();
        final String content = getContent(type, null);

        saveNotifications(content, type, pushNotification.getObjectId(), null, pushNotification.getReceiveUser());
        sendPersonalAlarm(content, token);
    }

    private String getContent(NotificationType type, String nickName){
        switch (type){
            case FOLLOW:
                return nickName+ "님이 팔로우했습니다.";
            case REVIEW_GOOD:
                return nickName+ "님이 회원님의 리뷰를 좋아합니다.";
            case COMMENT:
                return nickName+ "님이 회원님의 리뷰에 댓글을 남겼습니다.";
            case RECOMMENT:
                return nickName+ "님이 댓글을 작성했습니다.";
            case PROMOTION:
                return "새로운 프로모션이 등록되었습니다.";
            default:
                return null;
        }
    }

    private void saveNotifications(String content, NotificationType type, Long objectId, String sendUserId, User receiveUser){
        if(type==NotificationType.PROMOTION) sendUserId= null;

        Notifications notification = Notifications.builder()
                .content(content)
                .date(LocalDateTime.now())
                .flagRead(false)
                .type(type)
                .objectId(objectId)
                .sendUserId(sendUserId)
                .receiveUser(receiveUser)
                .build();
        notificationRepository.save(notification);
    }

    private void sendPersonalAlarm(String content, String token) throws FirebaseMessagingException {
        try{
            Message message = Message.builder()
                    .putData("time", LocalDateTime.now().toString())
                    .setNotification(Notification.builder().setTitle("순술").setBody(content).build())
                    .setToken(token)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("메시지 전송 알림 완료 : " + response);
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }


    @Transactional
    public void deleteNotification(NotificationType type, Long objectId){
        switch (type){
            case FOLLOW:
                notificationRepository.deleteByTypeAndObjectId(type, objectId);
            case REVIEW_GOOD:
                notificationRepository.deleteByTypeAndObjectId(type, objectId);
            case COMMENT:
                final List<Comment> reCommentList= commentRepository.findAllByUpperCommentId(objectId);
                for(Comment reComment: reCommentList){
                    notificationRepository.deleteByTypeAndObjectId(NotificationType.RECOMMENT, reComment.getCommentId());
                }
                notificationRepository.deleteByTypeAndObjectId(NotificationType.COMMENT, objectId);
            case RECOMMENT:
                notificationRepository.deleteByTypeAndObjectId(type, objectId);
        }
    }

}
