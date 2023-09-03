package nextstep.org.apache.catalina.servlet.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.HttpStatus;
import org.apache.catalina.servlet.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("HttpResponse 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void messageBody_가_있다면_content_length를_자동으로_생성해준다() {
        // when
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine(HttpStatus.OK));
        response.setMessageBody("mallang");

        // then
        assertThat(response.headers().headers().get("Content-Length"))
                .isEqualTo("7");
    }

    @Test
    void 응답_메세지를_만들_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine(HttpStatus.OK));
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setMessageBody("mallang");

        // when
        String actual = response.toString();

        // then
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 7 \r\n" +
                "\r\n" +
                "mallang";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 응답_메세지가_없는_경우() {
        // given
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine(HttpStatus.OK));
        response.addHeader("hi", "yes");

        // when
        String actual = response.toString();

        // then
        String expected = "HTTP/1.1 200 OK \r\n" +
                "hi: yes \r\n" +
                "\r\n";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 쿠키를_세팅할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine(HttpStatus.OK));
        response.addHeader("hi", "yes");
        response.addCookie("mallang", "1234");
        response.addCookie("썬샷", "12345");

        // when
        String actual = response.toString();

        // then
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Set-Cookie: mallang=1234 \r\n" +
                "Set-Cookie: 썬샷=12345 \r\n" +
                "hi: yes \r\n" +
                "\r\n";
        assertThat(actual).isEqualTo(expected);
    }
}
