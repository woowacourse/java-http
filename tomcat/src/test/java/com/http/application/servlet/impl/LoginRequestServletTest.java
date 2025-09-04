package com.http.application.servlet.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.http.domain.HttpRequest;
import com.http.domain.StartLine;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginRequestServletTest {

    private LoginRequestServlet loginRequestHandler;

    @BeforeEach
    void setUp() {
        loginRequestHandler = new LoginRequestServlet();
        
        // 테스트용 사용자 데이터 초기화
        User testUser = new User(1L, "admin", "password123", "admin@test.com");
        InMemoryUserRepository.save(testUser);
    }

    @Test
    void 올바른_계정정보로_로그인_성공() {
        // given
        StartLine startLine = new StartLine("GET", "/login", "HTTP/1.1");
        Map<String, String> queryStrings = Map.of(
            "account", "admin",
            "password", "password123"
        );
        HttpRequest httpRequest = new HttpRequest(startLine, queryStrings, Map.of());

        // when & then
        assertDoesNotThrow(() -> loginRequestHandler.handle(httpRequest));
    }

    @Test
    void 잘못된_계정으로_로그인_실패() {
        // given
        StartLine startLine = new StartLine("GET", "/login", "HTTP/1.1");
        Map<String, String> queryStrings = Map.of(
            "account", "wronguser",
            "password", "password123"
        );
        HttpRequest httpRequest = new HttpRequest(startLine, queryStrings, Map.of());

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> loginRequestHandler.handle(httpRequest)
        );
        assertEquals("해당 회원을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 잘못된_비밀번호로_로그인_시도() {
        // given
        StartLine startLine = new StartLine("GET", "/login", "HTTP/1.1");
        Map<String, String> queryStrings = Map.of(
            "account", "admin",
            "password", "wrongpassword"
        );
        HttpRequest httpRequest = new HttpRequest(startLine, queryStrings, Map.of());

        // when & then - 현재 구현에서는 비밀번호 틀려도 예외가 발생하지 않음
        assertDoesNotThrow(() -> loginRequestHandler.handle(httpRequest));
    }

    @Test
    void account_파라미터가_없으면_예외_발생() {
        // given
        StartLine startLine = new StartLine("GET", "/login", "HTTP/1.1");
        Map<String, String> queryStrings = Map.of("password", "password123");
        HttpRequest httpRequest = new HttpRequest(startLine, queryStrings, Map.of());

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> loginRequestHandler.handle(httpRequest)
        );
        assertEquals("account와 password는 필수입니다.", exception.getMessage());
    }

    @Test
    void password_파라미터가_없으면_예외_발생() {
        // given
        StartLine startLine = new StartLine("GET", "/login", "HTTP/1.1");
        Map<String, String> queryStrings = Map.of("account", "admin");
        HttpRequest httpRequest = new HttpRequest(startLine, queryStrings, Map.of());

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> loginRequestHandler.handle(httpRequest)
        );
        assertEquals("account와 password는 필수입니다.", exception.getMessage());
    }

    @Test
    void 쿼리_파라미터가_모두_없으면_예외_발생() {
        // given
        StartLine startLine = new StartLine("GET", "/login", "HTTP/1.1");
        HttpRequest httpRequest = new HttpRequest(startLine, Map.of(), Map.of());

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> loginRequestHandler.handle(httpRequest)
        );
        assertEquals("account와 password는 필수입니다.", exception.getMessage());
    }

    @Test
    void 빈_문자열_account로_로그인_시도() {
        // given
        StartLine startLine = new StartLine("GET", "/login", "HTTP/1.1");
        Map<String, String> queryStrings = Map.of(
            "account", "",
            "password", "password123"
        );
        HttpRequest httpRequest = new HttpRequest(startLine, queryStrings, Map.of());

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> loginRequestHandler.handle(httpRequest)
        );
        assertEquals("해당 회원을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 빈_문자열_password로_로그인_시도() {
        // given
        StartLine startLine = new StartLine("GET", "/login", "HTTP/1.1");
        Map<String, String> queryStrings = Map.of(
            "account", "admin",
            "password", ""
        );
        HttpRequest httpRequest = new HttpRequest(startLine, queryStrings, Map.of());

        // when & then - 현재 구현에서는 빈 비밀번호도 처리됨
        assertDoesNotThrow(() -> loginRequestHandler.handle(httpRequest));
    }
}
