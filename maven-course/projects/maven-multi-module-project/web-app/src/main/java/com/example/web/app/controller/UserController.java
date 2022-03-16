package com.example.web.app.controller;

import com.example.mm.convertor.UserMapper;
import com.example.mm.model.User;
import guru.springframework.domain.UserCommand;

public class UserController {

    public User saveUser(UserCommand user) {
        return UserMapper.userConvertor.userCommandToUser(user);
    }

}
