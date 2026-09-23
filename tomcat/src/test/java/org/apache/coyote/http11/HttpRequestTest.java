package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void HTTP_요청을_method_path_version으로_파싱한다() throws IOException {
        // given & when
        HttpRequest request = request("GET /index.html HTTP/1.1\r\n\r\n");

        // then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void Content_Length만큼_본문을_읽어_한글_폼_값을_해석하고_뒤의_데이터는_포함하지_않는다() throws IOException {
        // given
        String body = "memo=한글";
        String rawRequest = "POST /register HTTP/1.1\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                + "\r\n"
                + body
                + "extra";

        // when
        HttpRequest request = request(rawRequest);

        // then
        assertThat(request.getParameter("memo")).isEqualTo("한글");
    }

    @Test
    void form_body를_요청_parameter로_변환한다() throws IOException {
        // given
        String body = "account=usher&password=password";
        String rawRequest = formRequest(body);

        // when
        HttpRequest request = request(rawRequest);

        // then
        assertThat(request.getParameter("account")).isEqualTo("usher");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void form_body의_인코딩된_문자를_디코딩한다() throws IOException {
        // given
        String body = "email=usher%40woowahan.com&name=u+sher";
        String rawRequest = formRequest(body);

        // when
        HttpRequest request = request(rawRequest);

        // then
        assertThat(request.getParameter("email")).isEqualTo("usher@woowahan.com");
        assertThat(request.getParameter("name")).isEqualTo("u sher");
    }

    @Test
    void form_값에_등호가_있어도_첫_등호만_구분자로_사용한다() throws IOException {
        // given & when
        HttpRequest request = request(formRequest("token=abc=def"));

        // then
        assertThat(request.getParameter("token")).isEqualTo("abc=def");
    }

    @Test
    void 헤더_이름은_대소문자를_구분하지_않는다() throws IOException {
        // given
        String body = "account=usher";
        String rawRequest = "POST /login HTTP/1.1\r\n"
                + "cOnTeNt-TyPe: application/x-www-form-urlencoded\r\n"
                + "cOnTeNt-LeNgTh: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                + "\r\n"
                + body;

        // when
        HttpRequest request = request(rawRequest);

        // then
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getParameter("account")).isEqualTo("usher");
    }

    @Test
    void 본문이_Content_Length보다_짧으면_요청을_거부한다() {
        // given
        String rawRequest = "POST /login HTTP/1.1\r\nContent-Length: 100\r\n\r\naccount=usher";

        // when & then
        assertThatThrownBy(() -> request(rawRequest)).isInstanceOf(IllegalArgumentException.class);
    }

    private String formRequest(String body) {
        return "POST /login HTTP/1.1\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                + "\r\n"
                + body;
    }

    private HttpRequest request(String rawRequest) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));
        return new HttpRequestParser(inputStream).parse();
    }
}
