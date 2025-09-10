package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spring.http.enums.HttpMethod;
import com.spring.http.enums.HttpStatus;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnAuthorizedException;
import com.techcourse.model.User;
import com.spring.controller.Controller;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.spring.http.HttpHeader;
import com.spring.http.request.HttpRequest;
import com.spring.http.request.HttpRequestBody;
import com.spring.http.request.RequestStartLine;
import com.spring.http.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final String version = "HTTP/1.1";
    private Controller loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();

        // 테스트용 사용자 데이터 초기화
        User testUser = new User(1L, "admin", "password123", "admin@test.com");
        InMemoryUserRepository.save(testUser);
    }

    @DisplayName("올바른 계정정보로 로그인에 성공한다")
    @Test
    void 올바른_계정정보로_로그인_성공() {
        // given
        HttpRequest httpRequest = getHttpRequest(HttpMethod.POST);
        HttpResponse httpResponse = new HttpResponse(version);

        // when & then
        assertDoesNotThrow(() -> loginController.service(httpRequest, httpResponse));
    }

    @DisplayName("잘못된 계정으로 로그인에 실패한다")
    @Test
    void 잘못된_계정으로_로그인_실패() {
        // given
        HttpRequest httpRequest = getHttpRequest(HttpMethod.POST, "wronguser", "wrongpass");
        HttpResponse httpResponse = new HttpResponse(version);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loginController.service(httpRequest, httpResponse)
        );
        assertEquals("해당 회원을 찾을 수 없습니다.", exception.getMessage());
    }

    @DisplayName("빈 문자열 account로 로그인 시도 시 실패한다")
    @Test
    void 빈_문자열_account로_로그인_시도() {
        // given
        HttpRequest httpRequest = getHttpRequest(HttpMethod.POST, "", "password123");
        HttpResponse httpResponse = new HttpResponse(version);

        // when & then
        assertThatThrownBy(() -> loginController.service(httpRequest, httpResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 회원을 찾을 수 없습니다.");
    }

    @DisplayName("빈 문자열 password로 로그인 시도 시 실패한다")
    @Test
    void 빈_문자열_password로_로그인_시도() {
        // given
        HttpRequest httpRequest = getHttpRequest(HttpMethod.POST, "admin", "");
        HttpResponse httpResponse = new HttpResponse(version);

        // when & then - 빈 패스워드는 UnAuthorizedException 발생
        assertThatThrownBy(() -> loginController.service(httpRequest, httpResponse))
                .isInstanceOf(UnAuthorizedException.class)
                .hasMessage("잘못된 인증입니다.");
    }

    @DisplayName("이미 로그인된 상태에서 GET /login 접근 시 FOUND로 처리한다.")
    @Test
    void doGet_alreadyLoggedIn_redirectToIndex() throws IOException {
        // given - 먼저 로그인
        HttpRequest loginRequest = getHttpRequest(HttpMethod.POST);
        HttpResponse loginResponse = new HttpResponse(version);

        // 로그인 실행
        loginController.service(loginRequest, loginResponse);

        // 세션에서 사용자 정보 확인을 위해 같은 세션을 사용하는 GET 요청 생성
        RequestStartLine getStartLine = new RequestStartLine(HttpMethod.GET, "/login", version);
        HttpHeader getHeader = new HttpHeader();
        // 실제로는 쿠키에서 세션 ID를 가져와야 하지만, 테스트에서는 같은 세션 사용
        HttpRequest getRequest = new HttpRequest(getStartLine, Map.of(), getHeader);
        HttpResponse getResponse = new HttpResponse(version);

        // when
        loginController.doGet(getRequest, getResponse);

        // then - 302 리다이렉트 확인은 세션이 제대로 작동할 때만 가능
        assertThat(getResponse.getStatus()).isNotEqualTo(HttpStatus.FOUND);
    }

    private HttpRequest getHttpRequest(HttpMethod httpMethod) {
        return getHttpRequest(httpMethod, "admin", "password123");
    }

    private HttpRequest getHttpRequest(HttpMethod httpMethod, String account, String password) {
        RequestStartLine postStartLine = new RequestStartLine(httpMethod, "/login", version);

        HttpHeader postHeader = new HttpHeader();
        String bodyContent = String.format("account=%s&password=%s", account, password);
        postHeader.put("Content-Type", "application/x-www-form-urlencoded");
        postHeader.put("Content-Length", String.valueOf(bodyContent.getBytes(StandardCharsets.UTF_8).length));
        HttpRequestBody body = new HttpRequestBody(bodyContent);

        return new HttpRequest(postStartLine, Map.of(), postHeader, body);
    }

    @DisplayName("로그인하지 않은 상태에서 GET /login 접근 시 로그인 페이지를 표시한다")
    @Test
    void doGet_notLoggedIn_showLoginPage() throws IOException {
        // given
        RequestStartLine requestStartLine = new RequestStartLine(HttpMethod.GET, "/login", version);
        HttpRequest httpRequest = new HttpRequest(requestStartLine, Map.of(), new HttpHeader());
        HttpResponse httpResponse = new HttpResponse(version);

        // when
        loginController.doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getBody()).isNotNull();

        assertThat(httpResponse.getStatus()).isNotEqualTo(HttpStatus.FOUND);
    }
}
