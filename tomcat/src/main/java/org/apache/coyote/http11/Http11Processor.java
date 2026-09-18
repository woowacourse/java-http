package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final StaticResourceLoader resourceLoader = new StaticResourceLoader();

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            List<String> headers = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty() ) {
                headers.add(line);
            }
            String[] tokens = headers.get(0).split(" ");
            if (tokens.length < 2) {
                return;
            }


            String method = tokens[0];
            var requestUri = new RequestUri(tokens[1]);

            int contentLength = findContentLength(headers);
            String body = readBody(reader, contentLength);

            handleRequest(method, requestUri, body, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int findContentLength(List<String> headers) {
        for (String header : headers) {
            if (header.toLowerCase().startsWith("content-length:")) {
                String value = header
                        .substring("content-length:".length())
                        .trim();

                return Integer.parseInt(value);
            }
        }

        return 0;
    }

    private String readBody(
            BufferedReader reader,
            int contentLength
    ) throws IOException {
        char[] buffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int readCount = reader.read(
                    buffer,
                    totalRead,
                    contentLength - totalRead
            );

            if (readCount == -1) {
                throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
            }

            totalRead += readCount;
        }

        return new String(buffer);
    }

    private void handleRequest(String method, RequestUri requestUri, String body, OutputStream outputStream) throws IOException {
        String path = requestUri.getPath();
        switch (path) {
            case "/" -> writeResponse(outputStream, "200 OK", "Hello world!", "text/html");
            case "/register" -> handleRegister(method, body, outputStream);
            case "/login" -> handleLogin(requestUri, outputStream);
            default -> serveResource(path, outputStream);
        }
    }

    private void handleRegister(String method, String body, OutputStream outputStream) throws IOException {
        if(method.equals("GET")) {
            serveResource("/register.html", outputStream);
        }
        if(method.equals("POST")) {
            log.info("Register BODY :" + body);

            Map<String, String> map = new HashMap<>();
            String[] params = body.split("&");

            for (String param : params) {
                map.put(param.split("=")[0], param.split("=")[1]);
            }
            InMemoryUserRepository.save(new User(map.get("account"), map.get("password"), map.get("email")));

            writeRedirect(outputStream, "/index.html");
        }
    }

    private void handleLogin(RequestUri requestUri, OutputStream outputStream) throws IOException {
        if (requestUri.getQueryParameter("account") == null
                && requestUri.getQueryParameter("password") == null) {
            serveResource("/login.html", outputStream);
            return;
        }

        Optional<User> loginUser = login(requestUri);
        loginUser.ifPresent(user -> log.info("회원 조회 성공: account={}", user.getAccount()));
        String location = loginUser.isPresent() ? "/index.html" : "/401.html";
        writeRedirect(outputStream, location);
    }

    private void serveResource(String path, OutputStream outputStream) throws IOException {
        String responseBody;
        try {
            responseBody = resourceLoader.load(path);
        } catch (FileNotFoundException e) {
            writeResponse(outputStream, "404 Not Found", "Not Found", "text/plain");
            return;
        }

        String contentType = path.endsWith(".css") ? "text/css" : "text/html";
        writeResponse(outputStream, "200 OK", responseBody, contentType);
    }

    private Optional<User> login(RequestUri requestUri) {
        String account = requestUri.getQueryParameter("account");
        String password = requestUri.getQueryParameter("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void writeRedirect(OutputStream outputStream, String location) throws IOException {
        String response = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                "");
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void writeResponse(OutputStream outputStream, String status, String responseBody, String contentType) throws IOException {
        byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        var response = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBodyBytes.length + " ",
                "",
                responseBody
        );
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
