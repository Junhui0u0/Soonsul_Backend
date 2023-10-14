package com.example.soonsul.util;

import com.example.soonsul.response.error.ErrorCode;
import com.example.soonsul.user.entity.User;
import com.example.soonsul.user.exception.UserNotExist;
import com.example.soonsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;

    public User getUserByAuthentication(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findById(authentication.getName())
                .orElseThrow(()-> new UserNotExist("login user not exist", ErrorCode.USER_NOT_EXIST));
    }

    public User getUserById(String userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new UserNotExist("login user not exist", ErrorCode.USER_NOT_EXIST));
    }
}
