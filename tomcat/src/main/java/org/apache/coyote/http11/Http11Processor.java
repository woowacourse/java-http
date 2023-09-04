package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
                    if (httpRequestHeader.hasCookie()) {
                        final String content = getResourceContent(path);
                        final String response = new HttpResponse(HttpStatus.OK)
                                .addContentType(ContentType.HTML)
                                .addContentLength(content.length())
                                .build(content);
                        writeResponse(outputStream, response);
                        return;
                    }
                    final String response = redirectResponse("/login.html");
                    writeResponse(outputStream, response);
                    return;
                }
                if (path.equals("/login")) {
                    if (httpRequestHeader.hasCookie()) {
                        final String response = redirectResponse("/index.html");
                        writeResponse(outputStream, response);
                        return;
                    }
                    final String response = redirectResponse("/login.html");
                    writeResponse(outputStream, response);
                    return;
                }
                if (path.equals("/register")) {
                    final String response = redirectResponse("/register.html");
                    writeResponse(outputStream, response);
                    return;
                }
                if (httpRequestHeader.getRequestUri().contains(".")) {
                    generateResourceResponse(httpRequestHeader, outputStream);
                    return;
                }
                final String response = redirectResponse("/404.html");
                writeResponse(outputStream, response);
            }

            if (httpRequestHeader.getMethod() == HttpMethod.POST && path.equals("/login")) {
                final HttpRequestBody httpRequestBody = parseBody(httpRequestHeader, bufferedReader);
                generateLoginResponse(httpRequestBody, outputStream);
                return;
            }

            if (httpRequestHeader.getMethod() == HttpMethod.POST && path.equals("/register")) {
                final HttpRequestBody httpRequestBody = parseBody(httpRequestHeader, bufferedReader);
                generateRegisterResponse(httpRequestBody, outputStream);
                return;
            }
            generateDefaultResponse(outputStream);
        } catch (IllegalArgumentException e) {
            final String response = new HttpResponse(HttpStatus.BAD_REQUEST).build();
            log.error(e.getMessage(), e);
            writeResponse(outputStream, response);
        }
    }

    private static HttpRequestBody parseBody(final HttpRequestHeader httpRequestHeader, final BufferedReader bufferedReader) throws IOException {
        if (httpRequestHeader.getContentLength() != 0) {
            final int contentLength = httpRequestHeader.getContentLength();
            final char[] buffers = new char[contentLength];
            bufferedReader.read(buffers, 0, contentLength);
            return HttpRequestBody.of(httpRequestHeader.getContentType(), new String(buffers));
        }
        return HttpRequestBody.empty();
    }

    private void generateRegisterResponse(final HttpRequestBody body, final OutputStream outputStream) throws IOException {
        final String account = body.get("account");
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 존재하는 계정입니다.");
                });
        final String password = body.get("password");
        final String email = body.get("email");
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        final String response = redirectResponse("/login.html");
        writeResponse(outputStream, response);
    }

    private void generateLoginResponse(final HttpRequestBody body, final OutputStream outputStream) throws IOException {
        final String account = body.get("account");
        final String password = body.get("password");
        final String response = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> loginResponse())
                .orElseGet(() -> redirectResponse("/401.html"));
        writeResponse(outputStream, response);
    }

    private String loginResponse() {
        return new HttpResponse(HttpStatus.FOUND)
                .addLocation("/index.html")
                .addSetCookie(Cookie.ofJSessionId("1234567890"))
                .build();
    }

    private String redirectResponse(final String location) {
        return new HttpResponse(HttpStatus.FOUND)
                .addLocation(location)
                .build();
    }

    private void generateDefaultResponse(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(ContentType.HTML)
                .addContentLength(responseBody.length())
                .build(responseBody);

        writeResponse(outputStream, response);
    }

    private void generateResourceResponse(final HttpRequestHeader request, final OutputStream outputStream) throws IOException {
        final String requestUri = request.getRequestUri();
        final String extension = requestUri.substring(requestUri.lastIndexOf(".") + 1);
        final ContentType contentType = ContentType.from(extension);
        final String content = getResourceContent(requestUri);
        final String response = new HttpResponse(HttpStatus.OK)
                .addContentType(contentType)
                .addContentLength(content.length())
                .build(content);
        writeResponse(outputStream, response);
    }

    private String getResourceContent(final String resourcePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + resourcePath);

        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
        return Files.readString(Path.of(resource.getPath()));
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
