package com.freelaflow.back_freelaflow.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.freelaflow.back_freelaflow.models.User;
import com.freelaflow.back_freelaflow.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public void postMethodName(@RequestBody User user) {
        userService.createUser(user.getEmail(), user.getPwd(), user.getNome());
    }
    
}
