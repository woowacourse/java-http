package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class UserService {

    private static final UserService INSTANCE = new UserService();

    private UserService() {
    }

    public static UserService getInstance() {
        return INSTANCE;
    }

    public boolean isPasswordCorrect(User user, String password) {
        return user.checkPassword(password);
    }

    public boolean isAccountExist(String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    public User findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    public void registerUser(String account, String password, String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        System.out.println("회원가입 성공: " + user.getAccount());
    }
}
