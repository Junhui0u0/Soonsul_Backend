package com.example.soonsul.user;

import com.example.soonsul.config.s3.S3Uploader;
import com.example.soonsul.notification.dto.PushNotification;
import com.example.soonsul.user.dto.FollowDto;
import com.example.soonsul.user.dto.NotificationFlag;
import com.example.soonsul.user.dto.UserProfileDto;
import com.example.soonsul.user.entity.Follow;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.repository.FollowRepository;
import com.example.soonsul.user.repository.UserRepository;
import com.example.soonsul.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserUtil userUtil;
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Value("${cloud.aws.s3.bucket.url}")
    private String AWS_S3_BUCKET_URL;


    @Transactional
    public void putNickname(String nickname){
        final User user= userUtil.getUserByAuthentication();

        if(!user.getNickname().equals(nickname)) user.updateNickname(nickname);
    }


    @Transactional
    public void putProfileImage(MultipartFile image){
        final User user= userUtil.getUserByAuthentication();

        if(user.getProfileImage()!=null) s3Uploader.deleteFile(user.getProfileImage().substring(AWS_S3_BUCKET_URL.length()));
        if(image==null|| image.isEmpty()) user.updateProfileImage(null);
        else user.updateProfileImage(s3Uploader.upload(image,"user-profile"));
    }


    @Transactional(readOnly = true)
    public boolean getNicknameCheck(String nickname){
        final Optional<User> user= userRepository.findByNickname(nickname);
        return user.isEmpty();
    }


    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(String userId){
        User user;
        if(userId==null) user= userUtil.getUserByAuthentication();
        else user= userUtil.getUserById(userId);

        return UserProfileDto.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .flagActivity((userId==null)? user.isFlagActivity(): null)
                .flagAdvertising((userId==null)? user.isFlagAdvertising(): null)
                .build();
    }


    @Transactional
    public PushNotification postFollowing(String userId){
        final User user= userUtil.getUserByAuthentication();
        final User following= userUtil.getUserById(userId);

        final Follow follow= Follow.builder()
                .follower(user)
                .following(following)
                .build();

        return PushNotification.builder()
                .objectId(followRepository.save(follow).getFollowId())
                .receiveUser(following)
                .build();
    }


    @Transactional
    public Long deleteFollowing(String userId){
        final User user= userUtil.getUserByAuthentication();
        final User following= userUtil.getUserById(userId);
        final Long objectId= followRepository.findByFollowerAndFollowing(user, following).get().getFollowId();

        followRepository.deleteByFollowerAndFollowing(user, following);
        return objectId;
    }


    @Transactional(readOnly = true)
    public List<FollowDto> getFollowingList(String userId){
        final User user= userUtil.getUserById(userId);
        final List<Follow> followingList= followRepository.findAllByFollower(user);

        final List<FollowDto> result= new ArrayList<>();
        for(Follow follow: followingList){
            final User following= follow.getFollowing();
            final FollowDto dto= FollowDto.builder()
                    .userId(following.getUserId())
                    .nickname(following.getNickname())
                    .profileImage(following.getProfileImage())
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public List<FollowDto> getFollowerList(String userId){
        final User user= userUtil.getUserById(userId);
        final List<Follow> followerList= followRepository.findAllByFollowing(user);

        final List<FollowDto> result= new ArrayList<>();
        for(Follow follow: followerList){
            final User follower= follow.getFollower();
            final FollowDto dto= FollowDto.builder()
                    .userId(follower.getUserId())
                    .nickname(follower.getNickname())
                    .profileImage(follower.getProfileImage())
                    .build();
            result.add(dto);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public NotificationFlag getFlagNotification(){
        final User user= userUtil.getUserByAuthentication();
        return NotificationFlag.builder()
                .flagActivity(user.isFlagActivity())
                .flagAdvertising(user.isFlagAdvertising())
                .build();
    }


    @Transactional
    public void putFlagNotification(NotificationFlag flag){
        final User user= userUtil.getUserByAuthentication();
        user.updateFlagActivity(flag.isFlagActivity());
        user.updateFlagAdvertising(flag.isFlagAdvertising());
    }

}
