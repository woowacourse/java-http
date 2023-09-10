package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.request.line.RequestLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestLineTest {

    @Test
    void requestLine을_입력해_생성한다() {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";

        // when
        RequestLine line = RequestLine.from(requestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(line.requestUri()).isEqualTo("/index.html");
            softly.assertThat(line.httpMethod()).isEqualTo(GET);
            softly.assertThat(line.httpVersion()).isEqualTo("HTTP/1.1");
        });
    }

    @Test
    void Request_Line의_구성을_Http_Method와_URI로_확인한다() {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";
        RequestLine line = RequestLine.from(requestLine);

        // when
        boolean actual = line.consistsOf(GET, "/index.html");

        // then
        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"GET, true", "POST, false"})
    void Request_Line의_구성을_Http_Method로_확인한다(HttpMethod httpMethod, boolean expected) {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";
        RequestLine line = RequestLine.from(requestLine);

        // when
        boolean actual = line.consistsOf(httpMethod);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void Request_Line에_Query_String이_있는지_확인한다() {
        // given
        String requestLine = "GET /login?account=gugu&password=password HTTP/1.1 ";
        RequestLine line = RequestLine.from(requestLine);

        // when
        boolean actual = line.hasQueryString();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Request_Line의_Query_String의_값을_꺼낸다() {
        // given
        String requestLine = "GET /login?account=gugu&password=password HTTP/1.1 ";
        RequestLine line = RequestLine.from(requestLine);

        // when
        String account = line.getQueryStringValue("account");

        // then
        assertThat(account).isEqualTo("gugu");
    }
}
