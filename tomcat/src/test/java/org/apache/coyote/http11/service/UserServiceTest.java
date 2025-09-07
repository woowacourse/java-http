package org.apache.coyote.http11.service;

import org.apache.coyote.http11.parser.ContentParseResult;
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

        assertDoesNotThrow(() -> userService.doGet(Map.of("account", "gugu", "password", "password")));
    }

    @Test
    void 존재하지_않는_유저일시_401로_반환한다() throws IOException {
        UserService userService = new UserService();

        URL resource = ClassLoader.getSystemClassLoader()
                .getResource("static/401.html");

        FileInputStream fileInputStream = new FileInputStream(resource.getFile());
        byte[] bytes = fileInputStream.readAllBytes();

        ContentParseResult contentParseResult = userService.doGet(Map.of("account", "gugu", "password", "password2"));

        Assertions.assertArrayEquals(bytes, contentParseResult.getParseContent());
    }

    @Test
    void 쿼리가_없으면_유저_로그인_화면을_반환한다() throws IOException {
        UserService userService = new UserService();

        ContentParseResult contentParseResult = userService.doGet(new HashMap<>());

        org.assertj.core.api.Assertions.assertThat(contentParseResult.getHttpResponseStatus())
                .contains("200");
    }

    @Test
    void 유저_로그인_프로세스는_302를_반환한다() throws IOException {
        UserService userService = new UserService();

        ContentParseResult contentParseResult = userService.doGet(Map.of("account", "gugu", "password", "password"));

        org.assertj.core.api.Assertions.assertThat(contentParseResult.getHttpResponseStatus())
                .contains("302");
    }
}
