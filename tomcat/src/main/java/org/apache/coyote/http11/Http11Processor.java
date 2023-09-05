package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

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
            handleRequst(inputStream, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequst(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final List<String> request = bufferedReader
                    .lines()
                    .takeWhile(line -> !line.isEmpty())
                    .collect(Collectors.toList());

            final HttpRequestHeader httpRequestHeader = new HttpRequestHeader(request);
            log.info(httpRequestHeader.getMethod().name() + " " + httpRequestHeader.getRequestUri());

            final String path = httpRequestHeader.getPath();

            if (httpRequestHeader.getMethod() == HttpMethod.GET) {
                if (path.equals("/index.html")) {
                    writeResourceResponse(httpRequestHeader, outputStream);
                    return;
                }
                if (path.equals("/login")) {
                    if (httpRequestHeader.hasCookie()) {
                        writeRedirectResponse(outputStream, "/index.html");
                        return;
                    }
                    writeRedirectResponse(outputStream, "/login.html");
                    return;
                }
                if (path.equals("/register")) {
                    writeRedirectResponse(outputStream, "/register.html");
                    return;
                }
                if (httpRequestHeader.getRequestUri().contains(".")) {
                    writeResourceResponse(httpRequestHeader, outputStream);
                    return;
                }
                if (path.equals("/")) {
                    writeDefaultResponse(outputStream);
                    return;
                }
                writeRedirectResponse(outputStream, "/404.html");
            }

            if (httpRequestHeader.getMethod() == HttpMethod.POST) {
                if (path.equals("/login")) {
                    final HttpRequestBody httpRequestBody = HttpRequestBody.parseBody(httpRequestHeader, bufferedReader);
                    writeLoginResponse(httpRequestBody, outputStream);
                    return;
                }
                if (path.equals("/register")) {
                    final HttpRequestBody httpRequestBody = HttpRequestBody.parseBody(httpRequestHeader, bufferedReader);
                    writeRegisterResponse(httpRequestBody, outputStream);
                }
            }
        } catch (AuthenticationException e) {
            writeRedirectResponse(outputStream, "/401.html");
        } catch (IllegalArgumentException e) {
            final String response = new HttpResponse(HttpStatus.BAD_REQUEST).build();
            log.error(e.getMessage(), e);
            writeResponse(outputStream, response);
        }
    }

    private void writeRedirectResponse(final OutputStream outputStream, final String location) throws IOException {
        final String response = redirectResponse(location);
        writeResponse(outputStream, response);
    }

    private String redirectResponse(final String location) {
        return new HttpResponse(HttpStatus.FOUND)
                .addLocation(location)
                .build();
    }

    private void writeRegisterResponse(final HttpRequestBody body, final OutputStream outputStream) throws IOException {
        final String account = body.get("account");
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    throw new AuthenticationException("이미 존재하는 계정입니다.");
                });
        final String password = body.get("password");
        final String email = body.get("email");
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        writeRedirectResponse(outputStream, "/login.html");
    }

    private void writeLoginResponse(final HttpRequestBody body, final OutputStream outputStream) throws IOException {
        final String account = body.get("account");
        final String password = body.get("password");
        final User loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new AuthenticationException("아이디 또는 비밀번호가 틀립니다."));
        final String response = getLoginResponse(loginUser);
        writeResponse(outputStream, response);
    }

    private String getLoginResponse(User user) {
        final SessionManager sessionManager = SessionManager.getInstance();
        final String sessionId = sessionManager.createSession(user);
        return new HttpResponse(HttpStatus.FOUND)
                .addLocation("/index.html")
                .addSetCookie(Cookie.ofJSessionId(sessionId))
                .build();
    }

    private void writeDefaultResponse(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(ContentType.HTML)
                .addCharset("utf-8")
                .addContentLength(responseBody.length())
                .build(responseBody);

        writeResponse(outputStream, response);
    }

    private void writeResourceResponse(final HttpRequestHeader request, final OutputStream outputStream) throws IOException {
        final String requestUri = request.getRequestUri();
        final String extension = requestUri.substring(requestUri.lastIndexOf(".") + 1);
        final ContentType contentType = ContentType.from(extension);
        final String content = getResourceContent(requestUri);
        final String response = new HttpResponse(HttpStatus.OK)
                .addContentType(contentType)
                .addCharset("utf-8")
                .addContentLength(content.getBytes().length)
                .build(content);
        writeResponse(outputStream, response);
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String getResourceContent(final String resourcePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + resourcePath);

        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
        return new String(Files.readAllBytes(Path.of(resource.getFile())));
    }
}
