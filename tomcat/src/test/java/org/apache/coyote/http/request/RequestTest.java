package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import support.FixedIdGenerator;

class RequestTest {

    private static final RequestLine REQUEST_LINE = new RequestLine("GET / HTTP/1.1");
    private static final RequestBody REQUEST_BODY = RequestBody.from(null);
    private static final Manager manager = SessionManager.getInstance();

    @BeforeAll
    static void setUp() {
        manager.setIdGenerator(new FixedIdGenerator());
    }

    @Test
    void 쿠키의_JSESSION_ID에_해당하는_세션을_반환한다() {
        // given
        manager.createSession();

        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Cookie", "JSESSIONID=fixed-id"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        Request request = new Request(REQUEST_LINE, requestHeaders, REQUEST_BODY);

        // when
        Session actual = request.getSession();

        // then
        assertThat(actual.getId()).isEqualTo(FixedIdGenerator.FIXED_ID);
    }

    @Test
    void 쿠키에_JSESSION_ID가_있지만_존재하지_않은_세션일_경우_새로_생성해서_반환한다() {
        // given
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Cookie", "JSESSIONID=invalid"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        Request request = new Request(REQUEST_LINE, requestHeaders, REQUEST_BODY);

        // when
        Session actual = request.getSession();

        // then
        assertSoftly(softly -> {
            assertThat(actual.getId()).isEqualTo(FixedIdGenerator.FIXED_ID);
            try {
                softly.assertThat(manager.findSession(actual.getId())).isNotNull();
            } catch (IOException e) {
                fail();
            }
        });
    }

    @Test
    void 쿠키에_JSESSION_ID가_없을_경우_새로_생성해서_반환한다() {
        // given
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        Request request = new Request(REQUEST_LINE, requestHeaders, REQUEST_BODY);

        // when
        Session actual = request.getSession();

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual).isNotNull();
            try {
                softly.assertThat(manager.findSession(actual.getId())).isNotNull();
            } catch (IOException e) {
                fail();
            }
        });
    }
}
