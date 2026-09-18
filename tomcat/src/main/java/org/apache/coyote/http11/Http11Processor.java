package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.request.MyHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
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
             final var outputStream = connection.getOutputStream()) {

            MyHttpRequest httpRequest =
                    MyHttpRequest.of(readHttpRequest(new BufferedReader(new InputStreamReader(inputStream))));
            log.info("start request: {} {}", httpRequest.method(), httpRequest.uri());

            if (isLoginRequest(httpRequest)) {
                authenticate(httpRequest);
            }

            final var responseBody = readStaticResource(httpRequest, "Hello world!");
            final var response = buildHttpResponse(httpRequest, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
            log.info("end request: {} {}", httpRequest.method(), httpRequest.uri());
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String readHttpRequest(BufferedReader br) throws IOException {
        final StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            sb.append(line).append("\r\n");
        }
        return sb.toString();
    }

    private static boolean isLoginRequest(MyHttpRequest httpRequest) {
        return httpRequest.resourcePath().contains("login")
                && httpRequest.hasQueryParameter();
    }

    private static void authenticate(MyHttpRequest httpRequest) {
        Map<String, String> queryParams = httpRequest.queryParameters();
        InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .filter(user -> user.getAccount().equals("gugu"))
                .filter(user -> user.checkPassword(queryParams.get("password")))
                .ifPresent(user -> log.info("user matched={}", user));
    }

    private static String readStaticResource(MyHttpRequest httpRequest, String defaultContent)
            throws IOException, URISyntaxException {
        URL fileUrl = Http11Processor.class
                .getClassLoader()
                .getResource(httpRequest.resourcePath());
        File file = new File(Objects.requireNonNull(fileUrl).toURI());
        if (file.isFile()) {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        }
        return defaultContent;
    }

    private static String buildHttpResponse(MyHttpRequest httpRequest, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + httpRequest.contentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }
}
