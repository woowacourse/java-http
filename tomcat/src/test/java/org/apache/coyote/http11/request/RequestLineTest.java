package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

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
        assertThat(line).isEqualTo(
                new RequestLine(GET, "/index.html", "HTTP/1.1")
        );
    }

    @Test
    void Request_Line의_구성을_확인한다() {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";
        RequestLine line = RequestLine.from(requestLine);

        // when
        boolean actual = line.consistsOf(GET, "/index.html");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Request_Ling에_Query_String이_있는지_확인한다() {
        // given
        String requestLine = "GET /login?account=gugu&password=password HTTP/1.1 ";
        RequestLine line = RequestLine.from(requestLine);

        // when
        boolean actual = line.hasQueryString();

        // then
        assertThat(actual).isTrue();
    }
}
