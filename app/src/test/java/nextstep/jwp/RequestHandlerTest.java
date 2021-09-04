package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.exception.handler.DefaultFileNotFoundException;
import nextstep.jwp.handler.HttpSessions;
import nextstep.jwp.handler.RequestHandler;
import nextstep.jwp.handler.constant.HttpMethod;
import nextstep.jwp.handler.constant.HttpStatus;
import nextstep.jwp.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestHandlerTest {

    @DisplayName("서버를 실행시켜서 브라우저로 서버에 접속하면 index.html 페이지를 보여준다.")
    @Test
    void index() throws IOException {
        // given
        List<String> headers = new ArrayList<>();
        final String httpRequest = mockRequest(HttpMethod.GET, "/index.html", headers, "");
        System.out.println(httpRequest);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/html;charset=utf-8"
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.OK);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("http://localhost:8080/login으로 접속하면 로그인 페이지(login.html)를 보여준다.")
    @Test
    void login_get() throws IOException {
        // given
        List<String> headers = new ArrayList<>();
        final String httpRequest = mockRequest(HttpMethod.GET, "/login", headers, "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/html;charset=utf-8"
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.OK);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("http://localhost:8080/login으로 접속하면 로그인 된 사용자는 메인 페이지(index.html)으로 Redirect.")
    @Test
    void login_get_has_session() throws IOException {
        // given
        String id = 로그인_된_사용자_세션_ID();

        List<String> headers = new ArrayList<>(Collections.singletonList("Cookie: JSESSIONID=" + id));
        final String httpRequest = mockRequest(HttpMethod.GET, "/login", headers, "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/html;charset=utf-8"
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.FOUND);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("로그인 성공하면 응답 헤더에 http status code를 302로 반환한다.")
    @Test
    void login_post() throws IOException {
        // given
        String id = 로그인_된_사용자_세션_ID();

        List<String> headers = new ArrayList<>(Arrays.asList("Content-Length: 30", "Content-Type: application/x-www-form-urlencoded", "Cookie: JSESSIONID=" + id));
        final String httpRequest = mockRequest(HttpMethod.POST, "/login", headers, "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/html;charset=utf-8",
                "Location: /index"
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.FOUND);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("로그인 성공 - JSESSIONID이 없는 경우 - 헤더에 Set-Cookie 추가 반환")
    @Test
    void login_post_fail() throws IOException {
        List<String> headers = new ArrayList<>(Arrays.asList("Content-Length: 30", "Content-Type: application/x-www-form-urlencoded"));
        final String httpRequest = mockRequest(HttpMethod.POST, "/login", headers, "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/html;charset=utf-8",
                "Location: /index",
                "Set-Cookie: "
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.FOUND);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("로그인 실패 - 비밂번호가 틀린 경우 - 401.html 반환")
    @Test
    void login_post_fail_when_password_failed() throws IOException {
        List<String> headers = new ArrayList<>(Arrays.asList("Content-Length: 29", "Content-Type: application/x-www-form-urlencoded"));
        final String httpRequest = mockRequest(HttpMethod.POST, "/login", headers, "account=gugu&password=passwor");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/html;charset=utf-8",
                "Location: /401.html"
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.UNAUTHORIZED);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.")
    @Test
    void register_get() throws IOException {
        // given
        List<String> headers = new ArrayList<>();
        final String httpRequest = mockRequest(HttpMethod.GET, "/register", headers, "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();

        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/html;charset=utf-8"
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.OK);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용하고 완료하면 index.html로 리다이렉트한다.")
    @Test
    void register_post() throws IOException {
        // given
        String id = 로그인_된_사용자_세션_ID();

        List<String> headers = new ArrayList<>(
                Arrays.asList(
                        "Content-Length: 56",
                        "Content-Type: application/x-www-form-urlencoded",
                        "Cookie: JSESSIONID=" + id
                )
        );
        final String httpRequest = mockRequest(HttpMethod.POST, "/register", headers, "account=jinho&email=jh8579@gmail.com&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/html;charset=utf-8",
                "Location: /index.html"
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.FOUND);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("클라이언트에서 요청하면 CSS 파일도 제공하도록 수정한다.")
    @Test
    void serve_css() throws IOException {
        // given
        List<String> headers = new ArrayList<>();
        final String httpRequest = mockRequest(HttpMethod.GET, "/css/styles.css", headers, "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();

        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length,
                "Content-Type: text/css"
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.OK);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("파일이 존재하지 않을 때 404 error page")
    @Test
    void file_not_exists() throws IOException {
        // given
        List<String> headers = new ArrayList<>();
        final String httpRequest = mockRequest(HttpMethod.GET, "/css/styles-1.css", headers, "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();

        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length + " ",
                "Location: /404.html ",
                "Content-Type: text/html;charset=utf-8 "
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.NOT_FOUND);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("Controller에 매핑되어 있지 않은 URL로 접근할 때 404 Error 반환")
    @Test
    void controller_not_mapped_url() throws IOException {
        // given
        List<String> headers = new ArrayList<>();
        final String httpRequest = mockRequest(HttpMethod.GET, "/logout", headers, "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String httpResponse = socket.output();

        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        List<String> expectedHeaders = Arrays.asList(
                "Content-Length: " + content.getBytes().length + " ",
                "Location: /404.html ",
                "Content-Type: text/html;charset=utf-8 "
        );

        HTTP_Response_start_line_형식을_준수하는지_확인(httpResponse, HttpStatus.NOT_FOUND);
        HTTP_Response_header가_원하는_header를_포함하는지_확인(httpResponse, expectedHeaders);
        HTTP_Response_body가_일치하는지_확인(httpResponse, content);
    }

    @DisplayName("HTTP Request Start Line 형식에 맞지 않는 경우 400 Error 발생하지만 400.html이 존재하지 않아 DefaultFileNotExist 에러 발")
    @Test
    void http_start_line_error() throws IOException {
        // given
        List<String> headers = new ArrayList<>();
        final String httpRequest = invalidMockRequest(HttpMethod.GET, "/login", headers, "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // then
        assertThatThrownBy(requestHandler::run).isInstanceOf(DefaultFileNotFoundException.class);
    }

    String mockRequest(HttpMethod method, String uri, List<String> headers, String body) {
        List<String> defaultHeader = new ArrayList<>(Arrays.asList("Host: localhost:8080", "Connection: keep-alive"));
        defaultHeader.addAll(headers);

        headers.addAll(defaultHeader);
        String header = String.join(" \r\n", headers);

        return String.join("\r\n",
                method.getMethodName() + " " + uri + " " + "HTTP/1.1 ",
                header + " ",
                "",
                body
        );
    }

    String invalidMockRequest(HttpMethod method, String uri, List<String> headers, String body) {
        List<String> defaultHeader = new ArrayList<>(Arrays.asList("Host: localhost:8080", "Connection: keep-alive"));
        defaultHeader.addAll(headers);

        headers.addAll(defaultHeader);
        String header = String.join(" \r\n", headers);

        return String.join("\r\n",
                method.getMethodName() + " " + "HTTP/1.1 ",
                header + " ",
                "",
                body
        );
    }

    private String 로그인_된_사용자_세션_ID() {
        String id = UUID.randomUUID().toString();
        HttpSessions.add(id);
        HttpSessions.getSession(id).setAttribute("user", new User(1, "gugu", "password", "email@email"));
        return id;
    }

    void HTTP_Response_start_line_형식을_준수하는지_확인(String response, HttpStatus status) {
        String[] startLineSplit = response.split("\\r\\n");
        String startLine = startLineSplit[0];

        String expectedStartLine = "HTTP/1.1 " + status.getValue() + " " + status.getReasonPhrase() + " ";

        assertThat(startLine).isEqualTo(expectedStartLine);
    }

    void HTTP_Response_header가_원하는_header를_포함하는지_확인(String response, List<String> headers) {
        String[] responseHeaderBody = response.split("\r\n\r\n");
        assertThat(responseHeaderBody[0]).contains(headers);

        assertThat(response).contains(headers);
    }

    void HTTP_Response_body가_일치하는지_확인(String response, String expectedBody) {
        String[] responseHeaderBody = response.split("\r\n\r\n");
        assertThat(responseHeaderBody[1]).isEqualTo(expectedBody);
    }
}
