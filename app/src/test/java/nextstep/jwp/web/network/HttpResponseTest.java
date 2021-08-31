package nextstep.jwp.web.network;

import nextstep.jwp.web.controller.View;
import nextstep.jwp.web.network.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @DisplayName("응답을 String으로 변환한다 - 성공")
    @Test
    void asString() {
        // given
        final View view = new View("/index");
        final String viewAsString = view.render();

        // when
        final HttpResponse response = new HttpResponse();
        response.setBody(view);
        final String actual = response.asString();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + viewAsString.getBytes().length + " \r\n" +
                "\r\n" +
                viewAsString;
        assertThat(actual).isEqualTo(expected);
    }
}