package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.HttpStatus;
import org.apache.catalina.servlet.response.StatusLine;
import org.apache.coyote.http11.HttpResponseMessageMaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("HttpResponseMessageMaker 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpResponseMessageMakerTest {

    @Test
    void 응답_메세지를_만들_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine(HttpStatus.OK));
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setMessageBody("mallang");

        // when
        String actual = HttpResponseMessageMaker.make(response);

        // then
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 7 \r\n" +
                "\r\n" +
                "mallang";
        assertThat(actual).isEqualTo(expected);
    }
}
