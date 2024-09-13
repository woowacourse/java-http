package org.apache.coyote.request.header;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.fixture.RequestHeaderFixture;
import org.apache.coyote.session.SessionPrefix;
import org.apache.coyote.util.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestHeaderTest {

    @Test
    @DisplayName("Content 가 존재하지 않을 경우 Content-Length 값을 0으로 반환한다.")
    void getBodyLength_WhenContentIsNull() {
        RequestHeader requestHeader = RequestHeaderFixture.DEFAULT;

        assertEquals(requestHeader.getBodyLength(), 0);
    }

    @Test
    @DisplayName("헤더의 Content-Length의 값을 반환환다.")
    void getBodyLength_WhenContentExist() {
        RequestHeader requestHeader = RequestHeaderFixture.REGISTER_POST_DEFAULT;

        assertEquals(requestHeader.getBodyLength(), 80);
    }

    @Test
    @DisplayName("Accept가 여러개 들어오는 경우 제일 우선시 되는 컨텐츠 타입을 찾는다.")
    void findAcceptType() {
        RequestHeader requestHeader = RequestHeaderFixture.RESOURCE_DEFAULT;

        assertEquals(requestHeader.findAcceptType(), ContentType.HTML);
    }

    @Test
    @DisplayName("헤더에 쿠키가 존재하는지 확인한다.")
    void hasCookie() {
        RequestHeader requestHeader = RequestHeaderFixture.COOKIE_RESOURCE_DEFAULT;

        assertTrue(requestHeader.hasCookie());
    }

    @Test
    @DisplayName("쿠키에 있는 세션 id를 통해 세션을 찾아온다.")
    void findSession() {
        SessionManager sessionManager = SessionManager.getInstance();
        Session expectedSession = SessionPrefix.SESSION;
        sessionManager.add(expectedSession);

        RequestHeader requestHeader = RequestHeaderFixture.COOKIE_RESOURCE_DEFAULT;
        Session session = requestHeader.findSession();

        assertEquals(expectedSession, session);
    }
}
