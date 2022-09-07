package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @DisplayName("header와 body 값으로 http 메시지를 잘 만드는지 확인한다.")
    @Test
    void makeResponse() {
        // given
        final HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setBody("Hello world!");

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        // when
        final String actual = httpResponse.makeResponse();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("리다이렉트를 할 때 header와 body 값으로 http 메시지를 잘 만드는지 확인한다.")
    @Test
    void makeResponse_redirect() {
        // given
        final HttpResponse httpResponse = new HttpResponse();
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "",
                "");

        // when
        httpResponse.sendRedirect("/index.html");
        final String actual = httpResponse.makeResponse();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
