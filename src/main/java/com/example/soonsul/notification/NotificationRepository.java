package com.example.soonsul.notification;

import com.example.soonsul.notification.entity.NotificationType;
import com.example.soonsul.notification.entity.Notifications;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    @Query(nativeQuery = true,
            value="SELECT * FROM notification n WHERE n.user_id = :userId" +
                    " ORDER BY n.notification_id DESC")
    Page<Notifications> findAllByUser(Pageable pageable, @Param("userId") String userId);

    @Query(nativeQuery = true,
            value="SELECT * FROM notification n WHERE n.user_id = :userId" +
                    " AND n.flag_read = false")
    List<Notifications> findNewNotification(@Param("userId") String userId);

    void deleteByTypeAndObjectId(NotificationType type, Long objectId);
}
