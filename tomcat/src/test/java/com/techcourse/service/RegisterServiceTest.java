package com.techcourse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.prefix.UserPrefix;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterServiceTest {

    private final RegisterService registerService;

    public RegisterServiceTest() {
        this.registerService = new RegisterService();
    }

    @Test
    @DisplayName("이미 존재하는 회원 정보로 가입할 경우 에러를 발생한다.")
    void addRegister_WhenExistUser() {
        User registeredUser = UserPrefix.REGISTERED_USER;

        assertThrows(IllegalArgumentException.class,
                () -> registerService.addRegister(
                        registeredUser.getAccount(),
                        registeredUser.getPassword(),
                        registeredUser.getEmail()
                ));
    }

    @Test
    @DisplayName("존재하는 회원이 아닌 경우 정보를 저장한다.")
    void addRegister_WhenNewUser() {
        User newUser = UserPrefix.NEW_USER2;

        registerService.addRegister(
                newUser.getAccount(),
                newUser.getPassword(),
                newUser.getEmail()
        );

        Optional<User> savedUser = InMemoryUserRepository.findByAccount(newUser.getAccount());
        assertEquals(newUser, savedUser.get());
    }
}
