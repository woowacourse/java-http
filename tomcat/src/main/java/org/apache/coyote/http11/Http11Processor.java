package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
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
            final var requestLine = RequestLine.from(bufferedReader.readLine());

            final var httpHeaders = HttpHeaders.parse(bufferedReader);

            log.info("요청 = {}", requestLine);

            final var result = getFile(requestLine.getPath());
            final var response = new Response();

            if (HttpMethod.GET.equals(requestLine.getHttpMethod())) {
                if (requestLine.getPath().isEqualPath("/login")) {
                    final var cookie = httpHeaders.get("Cookie");
                    final var httpCookie = HttpCookie.parse(cookie);
                    if (httpCookie.containsKey("JSESSIONID")) {
                        final var jSessionId = httpCookie.get("JSESSIONID");
                        final var session = sessionManager.findSession(jSessionId);
                        if (session == null) {
                            log.warn("유효하지 않은 세션입니다.");
                            redirectLocation(response, requestLine.getPath(), result, "401.html");
                        } else {
                            final var sessionUser = (User) session.getAttribute("user");
                            log.info("이미 로그인 유저 = {}", sessionUser);
                            redirectLocation(response, requestLine.getPath(), result, "index.html");
                        }
                    } else {
                        generateOKResponse(response, requestLine.getPath(), result);
                    }
                } else {
                    generateOKResponse(response, requestLine.getPath(), result);
                }
            }

            if (HttpMethod.POST.equals(requestLine.getHttpMethod())) {
                final var body = parseRequestBody(httpHeaders, bufferedReader);

                if (requestLine.getPath().isEqualPath("/login")) {
                    final var user = createResponse(body, requestLine.getPath(), response, result);

                    log.info("user login = {}", user);
                } else if (requestLine.getPath().isEqualPath("/register")) {
                    final var user = new User(body.get("account"), body.get("password"), body.get("email"));
                    InMemoryUserRepository.save(user);
                    redirectLocation(response, requestLine.getPath(), result, "index.html");
                }

                outputStream.write(response.toHttpResponse().getBytes());
                outputStream.flush();
                return;
            }

            outputStream.write(response.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HashMap<String, String> parseRequestBody(final HttpHeaders httpHeaders,
                                                     final BufferedReader bufferedReader) throws IOException {
        final int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final var requestBody = new String(buffer);
        return parsingBody(requestBody);
    }

    private String getFile(final Path path) throws IOException {
        final var resource = path.getUrl();
        if (resource == null) {
            return "";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private HashMap<String, String> parsingBody(final String requestBody) {
        final var body = new HashMap<String, String>();
        final var params = requestBody.split("&");
        for (final var param : params) {
            body.put(param.split("=")[0], param.split("=")[1]);
        }
        return body;
    }

    private void generateOKResponse(final Response response, final Path path, final String result) {
        response.setSc("OK");
        response.setStatusCode(200);
        response.setContentType(path.getContentType());
        response.setContentLength(result.getBytes().length);
        response.setSourceCode(result);
    }

    private void redirectLocation(final Response response, final Path path, final String result,
                                  final String location) {
        response.setStatusCode(302);
        response.setSc("FOUND");
        response.setContentType(path.getContentType());
        response.setContentLength(result.getBytes().length);
        response.setLocation(location);
        response.setSourceCode(result);
    }

    private User createResponse(final Map<String, String> queryString, final Path request, final Response response,
                                final String result) {
        final var account = queryString.get("account");
        log.info("account = {}", account);
        try {
            final var user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            final var password = queryString.get("password");
            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            redirectLocation(response, request, result, "index.html");
            final var uuid = UUID.randomUUID();
            response.setCookie("JSESSIONID=" + uuid);
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
