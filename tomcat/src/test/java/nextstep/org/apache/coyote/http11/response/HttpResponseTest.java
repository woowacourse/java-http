package nextstep.org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;
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
        HttpResponse response = new HttpResponse(
                new StatusLine(HttpStatus.OK),
                new ResponseHeaders(),
                "mallang"
        );

        // then
        assertThat(response.headers().headers().get("Content-Length"))
                .isEqualTo("7");
    }

    @Test
    void 응답_메세지를_만들_수_있다() {
        // given
        ResponseHeaders headers = new ResponseHeaders();
        headers.put("Content-Type", "text/html;charset=utf-8");
        HttpResponse response = new HttpResponse(
                new StatusLine(HttpStatus.OK),
                headers,
                "mallang"
        );

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
        ResponseHeaders headers = new ResponseHeaders();
        headers.put("hi", "yes");
        HttpResponse response = new HttpResponse(
                new StatusLine(HttpStatus.OK),
                headers,
                null
        );

        // when
        String actual = response.toString();

        // then
        String expected = "HTTP/1.1 200 OK \r\n" +
                "hi: yes \r\n" +
                "\r\n";
        assertThat(actual).isEqualTo(expected);
    }
}
