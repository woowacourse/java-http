package nextstep.jwp.model;

import nextstep.jwp.HttpServlet;
import nextstep.jwp.MockSocket;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.request.HttpMethod.GET;
import static nextstep.jwp.model.httpmessage.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @DisplayName("메인 페이지를 조회한다.")
    @Test
    void request_GET_INDEXT() throws IOException {
        String value = String.join("\r\n",
                GET + " / HTTP/1.1 ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        MockSocket socket = new MockSocket(value);
        HttpServlet httpServlet = new HttpServlet(socket);
        httpServlet.run();

        HttpRequest request = new HttpRequest(socket.getInputStream());
        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/");
        assertThat(request.getHeader("Content-Type")).isEqualTo("text/html;charset=utf-8");
        assertThat(request.getContentLength()).isEqualTo(12);
        assertThat(request.getRequestBody()).isEqualTo("Hello world!");
    }

    @DisplayName("index.html 페이지를 조회한다.")
    @Test
    void request_GET_INDEXT_HTML() throws IOException {
        String value = String.join("\r\n",
                GET + " /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        MockSocket socket = new MockSocket(value);
        HttpServlet httpServlet = new HttpServlet(socket);
        httpServlet.run();

        HttpRequest request = new HttpRequest(socket.getInputStream());
        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
    }

    @DisplayName("login 페이지를 조회한다.")
    @Test
    void request_GET_LOGIN() throws IOException {
        String value = String.join("\r\n",
                GET + " /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        httpServlet.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());
        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getRequestURI()).isEqualTo("/login");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
    }

    @DisplayName("form 형식의 파라미터로 로그인을 실행한다.")
    @Test
    void request_GET_LOGIN_with_params() throws IOException {
        String value = String.join("\r\n",
                POST + " /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                "account=gugu&password=password");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        httpServlet.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());
        assertThat(request.getMethod()).isEqualTo(POST);
        assertThat(request.getRequestURI()).isEqualTo("/login");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @DisplayName("쿼리 파라미터로 로그인을 요청한다.")
    @Test
    void request_GET_LOGIN_with_query_params() throws IOException {
        String value = String.join("\r\n",
                GET + " /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        httpServlet.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());
        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getRequestURI()).isEqualTo("/login");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getQueryParam("account")).isEqualTo("gugu");
        assertThat(request.getQueryParam("password")).isEqualTo("password");
    }

    @DisplayName("css 파일을 요청한다.")
    @Test
    void request_CSS() throws IOException {
        String value = String.join("\r\n",
                GET + " /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css ",
                "");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        httpServlet.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());

        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/css/styles.css");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Accept")).isEqualTo("text/css");
    }

    @DisplayName("js 파일을 요청한다.")
    @Test
    void request_JS() throws IOException {
        String value = String.join("\r\n",
                GET + " /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: application/javascript ",
                "");
        final MockSocket socket = new MockSocket(value);
        final HttpServlet httpServlet = new HttpServlet(socket);

        httpServlet.run();
        HttpRequest request = new HttpRequest(socket.getInputStream());

        assertThat(request.getMethod()).isEqualTo(GET);
        assertThat(request.getPath()).isEqualTo("/js/scripts.js");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Accept")).isEqualTo("application/javascript");
    }
}