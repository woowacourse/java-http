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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            handleRequst(bufferedReader, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequst(final BufferedReader bufferedReader, final OutputStream outputStream) throws IOException {
        try {
            HttpRequest request = HttpRequest.parse(bufferedReader);

            final HttpMethod method = request.requestLine().method();
            final URI uri = request.requestLine().uri();
            final String path = uri.getPath();
            log.info(method.name() + " " + uri);


            if (method == HttpMethod.GET) {
                if (path.equals("/index.html")) {
                    writeResourceResponse(outputStream, path);
                    return;
                }
                if (path.equals("/login")) {
                    if (request.headers().getCookies().containsKey("JSESSIONID")) {
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
                if (path.lastIndexOf(".") != -1) {
                    writeResourceResponse(outputStream, path);
                    return;
                }
                if (path.equals("/")) {
                    writeDefaultResponse(outputStream);
                    return;
                }
                writeRedirectResponse(outputStream, "/404.html");
            }

            if (method == HttpMethod.POST) {
                if (path.equals("/login")) {
                    writeLoginResponse(outputStream, request.body());
                    return;
                }
                if (path.equals("/register")) {
                    writeRegisterResponse(outputStream, request.body());
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

    private void writeRegisterResponse(final OutputStream outputStream, final Body body) throws IOException {
        final String account = body.getValue("account");
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    throw new AuthenticationException("이미 존재하는 계정입니다.");
                });
        final String password = body.getValue("password");
        final String email = body.getValue("email");
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        writeResponse(outputStream, getLoginResponse(user));
    }

    private void writeLoginResponse(final OutputStream outputStream, final Body body) throws IOException {
        System.out.println("body = " + body);
        final String account = body.getValue("account");
        final String password = body.getValue("password");
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
                .addSetCookie(new Cookie("JSESSIONID", sessionId))
                .build();
    }

    private void writeDefaultResponse(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(ContentType.HTML)
                .addContentLength(responseBody.length())
                .build(responseBody);

        writeResponse(outputStream, response);
    }

    private void writeResourceResponse(final OutputStream outputStream, final String requestUri) throws IOException {
        final String extension = requestUri.substring(requestUri.lastIndexOf(".") + 1);
        final ContentType contentType = ContentType.from(extension);
        final String content = getResourceContent(requestUri);
        final String response = new HttpResponse(HttpStatus.OK)
                .addContentType(contentType)
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
