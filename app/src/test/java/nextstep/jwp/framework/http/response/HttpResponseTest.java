package nextstep.jwp.framework.http.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import nextstep.jwp.framework.http.common.HttpBody;
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
        response.create(new HttpRequestLine(HttpMethod.GET, new HttpPath("index.html"), new ProtocolVersion("HTTP/1.1")),
            new HttpHeaders("Content-Type: text/html;charset=utf-8 \r\nContent-Length: 12 \r\nCookie: io=H6Gs8jT7h07lTg94AAAA; JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1"),
            HttpStatus.OK);

        //then
        String content = new String(response.getBytes());
        assertThat(content).contains(
            "HTTP/1.1 200 OK ",
            "Cookie: io=H6Gs8jT7h07lTg94AAAA; JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1",
            "Set-Cookie: JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1; ",
            "Content-Type: text/html;charset=utf-8"
        );
    }

    @DisplayName("바디 포함 응답 바이트를 확인한다.")
    @Test
    void getByteWithBody() throws IOException {
        //given
        HttpResponse response = new HttpResponse();

        //when
        response.create(new HttpRequestLine(HttpMethod.POST, new HttpPath("index.html"), new ProtocolVersion("HTTP/1.1")),
            new HttpHeaders("Content-Type: text/html;charset=utf-8 \r\nContent-Length: 12 \r\nCookie: io=H6Gs8jT7h07lTg94AAAA; JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1 \r\n\r\n"),
            HttpStatus.OK);

        //then
        String content = new String(response.getBytes());
        assertThat(content).contains(
            "HTTP/1.1 200 OK ",
            "Cookie: io=H6Gs8jT7h07lTg94AAAA; JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1",
            "Set-Cookie: JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1; ",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: 2426"
        );
    }

}
