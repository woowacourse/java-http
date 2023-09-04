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

            final HttpRequest httpRequest = new HttpRequest(request);
            log.info("request: {}", httpRequest);


            if (httpRequest.getMethod() == HttpMethod.GET && httpRequest.getRequestUri().contains(".")) {
                generateResourceResponse(httpRequest, outputStream);
                return;
            }

            final String path = httpRequest.getPath();
            if (httpRequest.getMethod() == HttpMethod.POST && path.equals("/login")) {
                final HttpRequestBody httpRequestBody = parseBody(httpRequest, bufferedReader);
                generateLoginResponse(httpRequestBody, outputStream);
                return;
            }

            if (httpRequest.getMethod() == HttpMethod.POST && path.equals("/register")) {
                final HttpRequestBody httpRequestBody = parseBody(httpRequest, bufferedReader);
                generateRegisterResponse(httpRequestBody, outputStream);
                return;
            }
            generateDefaultResponse(outputStream);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            outputStream.write(new HttpResponse(HttpStatus.BAD_REQUEST).build().getBytes());
            outputStream.flush();
        }
    }

    private static HttpRequestBody parseBody(final HttpRequest httpRequest, final BufferedReader bufferedReader) throws IOException {
        if (httpRequest.getContentLength() != 0) {
            final int contentLength = httpRequest.getContentLength();
            final char[] buffers = new char[contentLength];
            bufferedReader.read(buffers, 0, contentLength);
            return HttpRequestBody.of(httpRequest.getContentType(), new String(buffers));
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
        outputStream.write(redirectResponse("/index.html").getBytes());
        outputStream.flush();
    }

    private void generateLoginResponse(final HttpRequestBody body, final OutputStream outputStream) throws IOException {
        final String account = body.get("account");
        final String password = body.get("password");
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        final String response = redirectResponse("/index.html");
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String redirectResponse(final String location) {
        return new HttpResponse(HttpStatus.FOUND)
                .addLocation(location)
                .build();
    }

    private static void generateDefaultResponse(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(ContentType.HTML)
                .addContentLength(responseBody.length())
                .build(responseBody);

        log.info("response: {}", response);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void generateResourceResponse(final HttpRequest request, final OutputStream outputStream) throws IOException {
        final String requestUri = request.getRequestUri();
        final String extension = requestUri.substring(requestUri.lastIndexOf(".") + 1);
        final ContentType contentType = ContentType.from(extension);
        final var resource = getClass().getClassLoader().getResource("static/" + requestUri);

        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
        final String responseBody = Files.readString(Path.of(resource.getPath()));

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(contentType)
                .addContentLength(responseBody.length())
                .build(responseBody);

        log.info("response: {}", response);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
