package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.DashboardException;
import com.techcourse.model.User;

public class UserService {

    public User login(String account, String password){
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new DashboardException("Cannot find account"));

        if (!user.checkPassword(password)) {
            throw new DashboardException("Wrong password");
        }
        return user;
    }
}
