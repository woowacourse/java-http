package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpVersion;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("리다이렉트 http 응답을 반환한다")
    @Test
    void found() {
        final HttpResponse response = HttpResponse.found(HttpVersion.HTTP_1_1, ContentType.HTML, "/index.html");
        String expected = HttpVersion.HTTP_1_1.getValue() + " " + HttpStatus.FOUND.getValue() + " \r\n"
                + "Content-Type: " + ContentType.HTML.getValue() + " \r\n"
                + "Location: /index.html " + "\r\n"
                + "\r\n";

        final String actual = response.createResponse();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Ok 응답을 반환한다")
    @Test
    void ok() {
        final HttpResponse response = HttpResponse.ok(HttpVersion.HTTP_1_1, ContentType.HTML, "");
        String expected = HttpVersion.HTTP_1_1.getValue() + " " + HttpStatus.OK.getValue() + " \r\n"
                + "Content-Type: " + ContentType.HTML.getValue() + " \r\n"
                + "Content-Length: " + "".getBytes().length + " \r\n"
                + "\r\n";

        final String actual = response.createResponse();

        assertThat(actual).isEqualTo(expected);
    }
}
