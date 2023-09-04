package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.common.FileContent;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private static final String NEW_LINE = "\r\n";
    private static final String STATIC = "static";

    private final ResponseLine responseLine;
    private final HttpHeaders headers;
    private final String responseBody;

    private HttpResponse(final ResponseLine responseLine, final HttpHeaders headers, final String responseBody) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse parse(final HttpRequest request) throws IOException {
        final RequestLine requestLine = request.getRequestLine();
        if (requestLine.getUri().equals("/register") && requestLine.getMethod().equals("POST")) {
            final URL url = HttpResponse.class.getClassLoader()
                    .getResource(STATIC + "/index" + ".html");
            final Path path = new File(url.getPath()).toPath();

            final String[] splitUserInfo = request.getRequestBody().split("&");
            if (splitUserInfo.length != 3) {
                throw new IllegalArgumentException("아이디, 이메일, 비밀번호가 전부 들어와야 합니다.");
            }

            final String account = splitUserInfo[0].split("=")[1];
            final String password = splitUserInfo[1].split("=")[1];
            final String email = splitUserInfo[2].split("=")[1];
            if (InMemoryUserRepository.findByAccount(account).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
            }
            final User user = new User(account, email, password);
            InMemoryUserRepository.save(user);

            final byte[] content = Files.readAllBytes(path);

            final HttpHeaders headers = HttpHeaders.createResponse(path);
            headers.setHeader("Location", "/index.html");
            final String responseBody = new String(content);

            return new HttpResponse(ResponseLine.create(HttpStatus.FOUND), headers, responseBody);
        }

        String uri = request.getUri();
        if (uri.equals("/")) {
            return new HttpResponse(ResponseLine.create(HttpStatus.OK),
                    HttpHeaders.createSimpleText(),
                    "Hello world!");
        }

        if (requestLine.getMethod().equals("GET")) {
            final String[] splitUri = uri.split("\\.");
            URL url;
            if (splitUri.length == 1) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + FileContent.findPage(uri) + ".html");

                final Path path = new File(url.getPath()).toPath();

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                final String responseBody = new String(content);


                final Session session = SessionManager.findSession(request.getHeaders().getCookie("JSESSIONID"));
                if (uri.equals("/login") && Objects.nonNull(session)) {
                    url = HttpResponse.class.getClassLoader()
                            .getResource(STATIC + "/index" + ".html");
                    final Path loginPath = new File(url.getPath()).toPath();

                    final byte[] loginContent = Files.readAllBytes(loginPath);
                    final String loginResponseBody = new String(loginContent);

                    headers.setHeader("Location", "/index.html");

                    return new HttpResponse(ResponseLine.create(HttpStatus.FOUND), headers, loginResponseBody);
                }
                if (FileContent.findPage(uri).equals("/404")) {
                    return new HttpResponse(ResponseLine.create(HttpStatus.NOT_FOUND), headers, responseBody);
                }
                return new HttpResponse(ResponseLine.create(HttpStatus.OK), headers, responseBody);
            } else {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + uri);

                final Path path = new File(url.getPath()).toPath();

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                final String responseBody = new String(content);

                return new HttpResponse(ResponseLine.create(HttpStatus.OK), headers, responseBody);
            }
        } else if (requestLine.getMethod().equals("POST")) {
            final String body = request.getRequestBody();
            final String[] parseQuery = body.split("&");
            final String username = parseQuery[0].split("=")[1];
            final String password = parseQuery[1].split("=")[1];

            URL url;

            if (InMemoryUserRepository.findByAccountAndPassword(username, password).isEmpty()) {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + "/401" + ".html");
                final Path path = new File(url.getPath()).toPath();

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                final String responseBody = new String(content);

                return new HttpResponse(ResponseLine.create(HttpStatus.UNAUTHORIZED), headers, responseBody);
            } else {
                url = HttpResponse.class.getClassLoader()
                        .getResource(STATIC + "/index" + ".html");
                final Path path = new File(url.getPath()).toPath();

                final User user = InMemoryUserRepository.findByAccountAndPassword(username, password)
                        .orElseThrow(() -> new IllegalArgumentException("알 수 없는 에러입니다."));
                log.info(user.toString());

                final byte[] content = Files.readAllBytes(path);

                final HttpHeaders headers = HttpHeaders.createResponse(path);
                final String uuid = UUID.randomUUID().toString();
                headers.setCookie("JSESSIONID", uuid);
                final Session session = new Session(uuid);
                session.setAttribute("user", user);
                SessionManager.add(session);

                headers.setHeader("Location", "/index.html");
                final String responseBody = new String(content);

                return new HttpResponse(ResponseLine.create(HttpStatus.FOUND), headers, responseBody);
            }
        }
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + "/404" + ".html");

        final Path path = new File(url.getPath()).toPath();

        final HttpHeaders headers = HttpHeaders.createResponse(path);

        return new HttpResponse(ResponseLine.create(HttpStatus.NOT_FOUND), headers, "");
    }

    @Override
    public String toString() {
        return responseLine.toString() +
                NEW_LINE +
                headers.toString() +
                NEW_LINE +
                responseBody;
    }
}
