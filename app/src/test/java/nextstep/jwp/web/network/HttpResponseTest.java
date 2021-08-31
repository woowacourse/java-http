package nextstep.jwp.web.network;

import nextstep.jwp.web.controller.View;
import nextstep.jwp.web.network.response.HttpResponse;
import nextstep.jwp.web.network.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpResponseTest {

    @DisplayName("HttpResponse 객체를 생성한다 - 성공")
    @Test
    void create() {
        assertThatCode(HttpResponse::new)
                .doesNotThrowAnyException();
    }

    @DisplayName("HttpResponse의 값을 String으로 출력한다 - 성공")
    @Test
    void print() {
        // given
        final View view = new View("/index.html");
        final HttpResponse httpResponse = new HttpResponse();

        // when
        httpResponse.setBody(view);
        final String responseAsString = httpResponse.print();

        // then
        final byte[] bytes = view.render().getBytes();
        assertThat(responseAsString).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bytes.length + " "
        );
    }

    @DisplayName("HttpResponse의 status를 수정한다 - 성공")
    @Test
    void setStatus() {
        // given
        final HttpStatus status = HttpStatus.UNAUTHORIZED;
        final HttpResponse response = new HttpResponse();

        // when
        response.setStatus(status);
        final String responseAsString = response.print();

        // then
        assertThat(responseAsString).contains("HTTP/1.1 401 Unauthorized ");
    }
}