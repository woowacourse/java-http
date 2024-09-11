package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final var httpRequest = HttpRequest.from(bufferedReader);

            log.info("요청 = {}", httpRequest.getRequestLine());

            //TODO refactor
            final var result = getFile(httpRequest.getRequestLine().getPath());
            final var httpResponse = new HttpResponse();

            if (httpRequest.hasMethod(HttpMethod.GET)) {
                if (httpRequest.getRequestLine().getPath().isEqualPath("/login")) {
                    final var cookie = httpRequest.getHeaders().get("Cookie");
                    final var httpCookie = HttpCookie.parse(cookie);
                    if (httpCookie.containsKey("JSESSIONID")) {
                        final var jSessionId = httpCookie.get("JSESSIONID");
                        final var session = sessionManager.findSession(jSessionId);
                        if (session == null) {
                            log.warn("유효하지 않은 세션입니다.");
                            redirectLocation(httpResponse, httpRequest, result, "401.html");
                        } else {
                            final var sessionUser = (User) session.getAttribute("user");
                            log.info("이미 로그인 유저 = {}", sessionUser);
                            redirectLocation(httpResponse, httpRequest, result, "index.html");
                        }
                    } else {
                        generateOKResponse(httpResponse, httpRequest, result);
                    }
                } else {
                    generateOKResponse(httpResponse, httpRequest, result);
                }
            }

            if (httpRequest.hasMethod(HttpMethod.POST)) {

                final var body = httpRequest.getBody();

                if (httpRequest.getRequestLine().getPath().isEqualPath("/login")) {
                    final var user = createResponse(body, httpRequest, httpResponse, result);

                    log.info("user login = {}", user);
                } else if (httpRequest.getRequestLine().getPath().isEqualPath("/register")) {
                    final var user = new User(body.get("account"), body.get("password"), body.get("email"));
                    InMemoryUserRepository.save(user);
                    redirectLocation(httpResponse, httpRequest, result, "index.html");
                }

                outputStream.write(httpResponse.toHttpResponse().getBytes());
                outputStream.flush();
                return;
            }

            outputStream.write(httpResponse.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFile(final Path path) throws IOException {
        final var resource = path.getAbsolutePath();
        if (resource == null) {
            return "";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private void generateOKResponse(final HttpResponse response, final HttpRequest request, final String result) {
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setSourceCode(result);
        response.putHeader("Content-Length", result.getBytes().length);
        response.putHeader("Content-Type", request.getContentTypeToResponseText());
    }

    private void redirectLocation(final HttpResponse response, final HttpRequest request, final String result,
                                  final String location) {
        response.setHttpStatusCode(HttpStatusCode.FOUND);
        response.setSourceCode(result);
        response.putHeader("Content-Length", result.getBytes().length);
        response.putHeader("Content-Type", request.getContentTypeToResponseText());
        response.putHeader("Location", location);
    }

    private User createResponse(final Map<String, String> body, final HttpRequest request,
                                final HttpResponse response,
                                final String result) {
        final var account = body.get("account");
        log.info("account = {}", account);
        try {
            final var user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            final var password = body.get("password");
            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            redirectLocation(response, request, result, "index.html");
            final var uuid = UUID.randomUUID();
            response.putHeader("Set-Cookie", "JSESSIONID=" + uuid);

            final var session = new Session(uuid.toString());
            session.setAttribute("user", user);
            sessionManager.add(session);
            return user;
        } catch (final IllegalArgumentException e) {
            log.warn(e.getMessage());
            redirectLocation(response, request, result, "401.html");
            return null;
        }
    }
}
