package com.github.soulaway.journals.service;

import java.util.Optional;

import com.github.soulaway.journals.model.User;

public interface UserService {

    Optional<User> getUserByLoginName(String loginName);

    void subscribe(User user, Long categoryId);

    User findById(Long id);

}