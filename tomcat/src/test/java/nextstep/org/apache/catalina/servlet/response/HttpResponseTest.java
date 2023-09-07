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
    void 쿠키를_세팅할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        response.setStatusLine(new StatusLine(HttpStatus.OK));
        response.addHeader("hi", "yes");

        // when
        response.addCookie("mallang", "1234");
        response.addCookie("썬샷", "12345");

        // then
        HttpResponse expected = new HttpResponse();
        expected.setStatusLine(new StatusLine(HttpStatus.OK));
        expected.addHeader("hi", "yes");
        expected.addCookie("mallang", "1234");
        expected.addCookie("썬샷", "12345");
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
