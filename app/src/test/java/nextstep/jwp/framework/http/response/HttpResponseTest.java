package nextstep.jwp.framework.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.framework.http.common.HttpHeaders;
import nextstep.jwp.framework.http.common.HttpMethod;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;
import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.request.HttpRequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("응답 바이트를 확인한다.")
    @Test
    void getByte() throws IOException {
        //given
        HttpResponse response = new HttpResponse();

        //when
        response.create(new HttpRequestLine(HttpMethod.GET, new HttpPath("/index.html"), new ProtocolVersion("HTTP/1.1")),
            new HttpHeaders("Content-Type: text/html;charset=utf-8 \r\nContent-Length: 12"),
            HttpStatus.OK);

        //then
        String content = new String(response.getBytes());
        assertThat(content).contains(
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html",
            "Content-Length: 5564"
        );
    }
}
