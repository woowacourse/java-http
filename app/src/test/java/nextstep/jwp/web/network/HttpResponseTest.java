package nextstep.jwp.web.network;

import nextstep.jwp.web.controller.View;
import nextstep.jwp.web.network.response.ContentType;
import nextstep.jwp.web.network.response.HttpResponse;
import nextstep.jwp.web.network.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpResponseTest {

//    @Test
//    void createWithByte() {
//        // given
//        final byte[] bytes = "Hello World!".getBytes();
//
//        // when // then
//        assertThatCode(() -> HttpResponse.ofByteArray(HttpStatus.OK, bytes))
//                .doesNotThrowAnyException();
//    }
//
//    @Test
//    void createWithString() {
//        // given
//        final String string = "Hello World!";
//
//        // when // then
//        assertThatCode(() -> HttpResponse.ofString(HttpStatus.OK, ContentType.HTML, string))
//                .doesNotThrowAnyException();
//    }

    @DisplayName("View를 이용해서 HttpResponse를 생성한다 - 성공")
    @Test
    void createWithString() {
        // given
        final View view = new View("/index");

        // when // then
        assertThatCode(() -> HttpResponse.ofView(HttpStatus.OK, view))
                .doesNotThrowAnyException();
    }

    @DisplayName("응답을 String으로 변환한다 - 성공")
    @Test
    void asString() {
        // given
        final View view = new View("/index");
        final String viewAsString = view.render();

        // when
        final HttpResponse httpResponse = HttpResponse.ofView(HttpStatus.OK, view);
        final String actual = httpResponse.asString();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + viewAsString.getBytes().length + " \r\n" +
                "\r\n" +
                viewAsString;
        assertThat(actual).isEqualTo(expected);
    }
}