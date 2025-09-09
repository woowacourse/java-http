package org.apache.coyote.http11.service;

import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.parser.RequestResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserServiceTest {

    @Test
    void 유저_로그인을_할_수_있다() {
        UserService userService = new UserService();

        assertDoesNotThrow(() -> userService.doGet(
                Map.of("account", "gugu", "password", "password"),
                new HttpCookies(new HashMap<>()),
                new Session(null)
        ));
    }

    @Test
    void 존재하지_않는_유저일시_401로_반환한다() throws IOException {
        UserService userService = new UserService();

        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/401.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        byte[] bytes = fileInputStream.readAllBytes();

        RequestResult requestResult = userService.doPost(
                Map.of("account", "gugu", "password", "password2"),
                new HttpCookies(new HashMap<>()),
                new Session(null)
        );

        Assertions.assertArrayEquals(bytes, requestResult.getParseContent());
    }

    @Test
    void 쿼리가_없으면_유저_로그인_화면을_반환한다() throws IOException {
        UserService userService = new UserService();

        RequestResult requestResult =
                userService.doGet(new HashMap<>(), new HttpCookies(new HashMap<>()), new Session(null));

        org.assertj.core.api.Assertions.assertThat(requestResult.getHttpResponseStatus())
                .contains("200");
    }

    @Test
    void 유저_로그인_프로세스는_302를_반환한다() throws IOException {
        UserService userService = new UserService();

        RequestResult requestResult = userService.doPost(
                Map.of("account", "gugu", "password", "password"),
                new HttpCookies(new HashMap<>()),
                new Session(null)
        );

        org.assertj.core.api.Assertions.assertThat(requestResult.getHttpResponseStatus())
                .contains("302");
    }
}
