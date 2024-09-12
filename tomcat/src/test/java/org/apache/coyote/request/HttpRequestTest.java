package org.apache.coyote.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.coyote.util.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private static final String COOKIE_HEADER = "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

    @Test
    @DisplayName("body로부터 특정 키값을 찾아 반환한다.")
    void getValueFromBody() {
        HttpRequest httpRequest = HttpRequestFixture.POST_LOGIN_PATH_REQUEST;

        assertEquals(httpRequest.getValueFromBody("account"), "polla");
    }


    @Test
    @DisplayName("컨텐츠 타입을 찾아 정적 경로를 반환한다.")
    void getResourcePath() {
        HttpRequest httpRequest = HttpRequestFixture.GET_REGISTER_PATH_REQUEST;

        assertEquals(httpRequest.getResourcePath(), "/register.html");
    }

    @Test
    @DisplayName("true를 받는 경우 랜덤한 UUID를 가진 새로운 세션을 만든다.")
    void getSession_WhenGivenTrue() {
        HttpRequest httpRequest = HttpRequestFixture.GET_REGISTER_PATH_REQUEST;

        Session session = httpRequest.getSession(true);
        SessionManager sessionManager = SessionManager.getInstance();

        assertNull(sessionManager.findSession(session.getId()));
    }

    @Test
    @DisplayName("/ 로 보내는 요청인지 확인한다.")
    void isDefaultRequestPath() {
        HttpRequest httpRequest = HttpRequestFixture.GET_DEFAULT_PATH_REQUEST;

        assertTrue(httpRequest.isDefaultRequestPath());
    }

    @Test
    @DisplayName("경로에 확장자가 없는 경우 Accept로부터 찾아온다.")
    void findContentType_WhenNoExtension() {
        HttpRequest httpRequest = HttpRequestFixture.GET_REGISTER_PATH_REQUEST;

        assertEquals(ContentType.HTML, httpRequest.findContentType());
    }

    @Test
    @DisplayName("경로에 확장자가 있는 경우 확장자로부터 찾아온다.")
    void findContentType_WhenHasExtension() {
        HttpRequest httpRequest = HttpRequestFixture.GET_STATIC_MAIN_PATH_REQUEST;

        assertEquals(ContentType.HTML, httpRequest.findContentType());
    }

    @Test
    @DisplayName("쿠키가 있는지 확인한다.")
    void hasCookie() {
        HttpRequest httpRequest = HttpRequestFixture.POST_LOGGED_IN_USER_WITH_COOKIE(COOKIE_HEADER);

        assertTrue(httpRequest.hasCookie());
    }
}
