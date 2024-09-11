package servlet;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeaders;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.junit.jupiter.api.Test;

class HttpServletTest {

    private final HttpServlet httpServlet = HttpServlet.getInstance();

    @Test
    void GET_login을_호출한다() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        httpServlet.service(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3797 \r\n" +
                "\r\n" +
                Files.readString(Paths.get(resource.getFile()), StandardCharsets.UTF_8);

        assertThat(new String(response.getBytes())).isEqualTo(expected);
    }

    @Test
    void query_parameter를_포함한_GET_login을_호출한다() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("GET /login?account=gugu&password=password HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        httpServlet.service(request, response);

        // then
        // todo session generator
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Set-Cookie: JSESSIONID=1234 \r\n" +
                "Location: /index \r\n" +
                "\r\n";

        assertThat(new String(response.getBytes())).isEqualTo(expected);
    }

    @Test
    void 이미_로그인한_후_GET_login을_호출한다() throws IOException {
        // given
        // todo
        Session session = new Session("1234");
        session.setAttribute("user", new User("gugu", "password", "email"));
        SessionManager.getInstance().add(session);

        RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Cookie", "JSESSIONID=1234"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        httpServlet.service(request, response);

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index \r\n" +
                "\r\n";

        assertThat(new String(response.getBytes())).isEqualTo(expected);
    }

    @Test
    void POST_login을_호출한다() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("POST /login HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", "29"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from("account=gugu&password=password");
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        httpServlet.service(request, response);

        // then
        // todo session generator
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Set-Cookie: JSESSIONID=1234 \r\n" +
                "Location: /index \r\n" +
                "\r\n";

        assertThat(new String(response.getBytes())).isEqualTo(expected);
    }

    @Test
    void POST_register를_호출한다() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("POST /register HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", "53"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from("account=prin&password=password&email=prin%40email.com");
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        httpServlet.service(request, response);

        // then
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index \r\n" +
                "\r\n";

        assertThat(new String(response.getBytes())).isEqualTo(expected);
    }

    @Test
    void GET_welcome을_호출한다() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        httpServlet.service(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/welcome.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 13 \r\n" +
                "\r\n" +
                Files.readString(Paths.get(resource.getFile()), StandardCharsets.UTF_8);

        assertThat(new String(response.getBytes())).isEqualTo(expected);
    }

    @Test
    void GET_정적_리소스를_호출한다() throws IOException {
        // given
        RequestLine requestLine = new RequestLine("GET /css/styles.css HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        httpServlet.service(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: 211991 \r\n" +
                "\r\n" +
                Files.readString(Paths.get(resource.getFile()), StandardCharsets.UTF_8);

        assertThat(new String(response.getBytes())).isEqualTo(expected);
    }
}
