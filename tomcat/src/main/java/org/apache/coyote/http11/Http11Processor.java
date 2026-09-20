package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.Register;
import com.techcourse.model.User;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
                if (authenticate(httpRequest)) {
                    String response = build302FoundResponse(httpRequest, "index.html");
                    outputStream.write(response.getBytes());
                } else {
                    String response = build302FoundResponse(httpRequest, "401.html");
                    outputStream.write(response.getBytes());
                }
                outputStream.flush();
                log.info("end request: {} {}", httpRequest.method(), httpRequest.uri());
                return;
            }

            // register
            if (isRegisterRequest(httpRequest)) {
                if (register(httpRequest)) {
                    String response = build302FoundResponse(httpRequest, "index.html");
                    outputStream.write(response.getBytes());

                } else {
                    String response = build302FoundResponse(httpRequest, "login.html");
                    outputStream.write(response.getBytes());
                }
                outputStream.flush();
                log.info("end request: {} {}", httpRequest.method(), httpRequest.uri());
                return;
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
        int contentLength = 0;
        while (!(line = br.readLine()).isEmpty()) {
            sb.append(line).append("\r\n");
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).strip());
            }
        }
        sb.append("\r\n");

        char[] cbuf = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int count = br.read(cbuf, read, contentLength - read);
            if (count == -1) {
                break;
            }
            read += count;
        }
        sb.append(cbuf, 0, read);
        return sb.toString();
    }

    private static boolean isLoginRequest(MyHttpRequest httpRequest) {
        return httpRequest.resourcePath().contains("static/login.html")
                && httpRequest.method().equalsIgnoreCase("POST")
                && httpRequest.hasRequestBody();
    }

    private static boolean isRegisterRequest(MyHttpRequest httpRequest) {
        return httpRequest.resourcePath().contains("static/register.html")
                && httpRequest.method().equalsIgnoreCase("POST")
                && httpRequest.hasRequestBody();
    }

    // TODO json도 처리 가능하도록
    private static boolean authenticate(MyHttpRequest httpRequest) {
        Map<String, String> params = new HashMap<>();
        for (String parameter : httpRequest.body().split("&")) {
            String[] keyValue = parameter.split("=", 2);
            params.put(keyValue[0], keyValue[1]);
        }
        Optional<User> foundUser = findUserByAccount(params.get("account"));
        if (foundUser.isEmpty()) {
            return false;
        }

        if (foundUser.get().checkPassword(params.get("password"))) {
            log.info("user matched={}", foundUser.get());
            return true;
        }
        log.info("authenticate failed!");
        return false;
    }

    private static boolean register(MyHttpRequest httpRequest) {
        Map<String, String> params = new HashMap<>();
        for (String parameter : httpRequest.body().split("&")) {
            String[] keyValue = parameter.split("=", 3);
            params.put(keyValue[0], keyValue[1]);
        }

        try {
            User registeredUser = Register.register(params.get("account"), params.get("email"), params.get("password"));
            log.info("registration succeed: {}", registeredUser);
            return true;
        } catch (IllegalArgumentException e) {
            log.error("registration failed: ", e);
            return false;
        }
    }

    private static Optional<User> findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account);
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

    private static String build302FoundResponse(MyHttpRequest httpRequest, String redirectLocation) {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 302 Found \r\n");
        if (!httpRequest.hasCookie("JSESSIONID")) {
            sb.append("Set-Cookie: JSESSIONID=")
                    .append(UUID.randomUUID())
                    .append(" \r\n");
        }
        String other =
                "Location: http://localhost:8080/" + redirectLocation + " \r\n" +
                        "Content-Type: text/html;charset=utf-8 \r\n" +
                        "Content-Length: 0 \r\n";
        return sb.append(other).toString();
    }

    private static String buildHttpResponse(MyHttpRequest httpRequest, String responseBody) {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK \r\n");
        if (!httpRequest.hasCookie("JSESSIONID")) {
            sb.append("Set-Cookie: JSESSIONID=")
                    .append(UUID.randomUUID())
                    .append(" \r\n");
        }
        String other = String.join("\r\n",
                "Content-Type: " + httpRequest.contentType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
        return sb.append(other).toString();
    }
}
