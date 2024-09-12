package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class RequestTest {

    private static final RequestLine REQUEST_LINE = new RequestLine("GET / HTTP/1.1");
    private static final RequestBody REQUEST_BODY = RequestBody.from(null);

    private final Manager manager = SessionManager.getInstance();

    @Test
    void 쿠키의_JSESSION_ID에_해당하는_세션을_반환한다() {
        // given
        String id = "1234";
        manager.createSession(id);

        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Cookie", "JSESSIONID=1234"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        Request request = new Request(REQUEST_LINE, requestHeaders, REQUEST_BODY);

        // when
        Session actual = request.getSession(false);

        // then
        assertThat(actual.getId()).isEqualTo(id);
    }

    @Test
    void 쿠키에_JSESSION_ID가_있지만_세션이_유효하지_않을경우_null을_반환한다() {
        // given
        Session session = manager.createSession("5678");
        session.setCreateTime(System.currentTimeMillis() - 1800000);

        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Cookie", "JSESSIONID=5678"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        Request request = new Request(REQUEST_LINE, requestHeaders, REQUEST_BODY);

        // when
        Session actual = request.getSession(false);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void 쿠키에_JSESSION_ID가_있지만_존재하지_않은_세션일_경우_null을_반환한다() {
        // given
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Cookie", "JSESSIONID=invalid"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        Request request = new Request(REQUEST_LINE, requestHeaders, REQUEST_BODY);

        // when
        Session actual = request.getSession(false);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void create_옵션을_사용할_경우_새로운_세션을_생성한다() {
        // given
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        Request request = new Request(REQUEST_LINE, requestHeaders, REQUEST_BODY);

        boolean create = true;

        // when
        Session actual = request.getSession(create);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual).isNotNull();
            try {
                softly.assertThat(manager.findSession(actual.getId())).isNotNull();
            } catch (IOException e) {
                fail();
            }
        });
    }
}
