package com.example.soonsul.notification;

import com.example.soonsul.notification.dto.NotificationDto;
import com.example.soonsul.notification.response.NotificationListResponse;
import com.example.soonsul.response.result.ResultCode;
import com.example.soonsul.response.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags="Notification (알림)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class NotificationController {
    private final NotificationService notificationService;


    @ApiOperation(value = "알림 내역 모두 조회")
    @GetMapping(value = "/notifications")
    public ResponseEntity<NotificationListResponse> getNotificationList(@PageableDefault(size=10, sort = "notification_id", direction = Sort.Direction.DESC) Pageable pageable) {
        final List<NotificationDto> data= notificationService.getNotificationList(pageable);
        return ResponseEntity.ok(NotificationListResponse.of(ResultCode.GET_NOTIFICATION_LIST_SUCCESS, data));
    }


    @ApiOperation(value = "새로운 알림이 존재하는지 확인", notes = "새로운 알림이 한개라도 있을 경우 true 반환, 그렇지 않을 경우 false 반환")
    @GetMapping(value = "/notifications/new")
    public ResponseEntity<ResultResponse> getNewNotification() {
        final boolean data= notificationService.getNewNotification();
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_NEW_NOTIFICATION_SUCCESS, data));
    }


    @ApiOperation(value = "새로운 알림을 읽었을 경우, 알림의 상태를 읽음으로 바꾸는 api", notes = "새로운 알림의 id값을 리스트 형태로 서버에 요청")
    @PutMapping("/notifications/new")
    public ResponseEntity<ResultResponse> putNewNotification(@RequestParam("notificationId") List<Long> idList) {
        notificationService.putNewNotification(idList);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PUT_NEW_NOTIFICATION_SUCCESS));
    }

}
