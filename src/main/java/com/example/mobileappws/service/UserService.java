package com.example.mobileappws.service;

import java.util.List;

import com.example.mobileappws.shared.dto.UserDto;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * UserService
 */
public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto user);
    UserDto updateUser(String id, UserDto user);
    UserDto getUser(String email);
    List<UserDto> getUsers(int page, int limit);
    UserDto getUserByUserId(String userId);
    void deleteUser(String userId);
    
}