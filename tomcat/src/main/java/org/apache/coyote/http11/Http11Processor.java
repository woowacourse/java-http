package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int MAX_REQUEST_SIZE = 104_857_600; // 10MB

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
            String request = parseRequest(inputStream);
            String header = request.split("\r\n")[0];
            validateHeader(header);
            String[] words = header.split(" ");
            String requestPath = words[1].split("\\?")[0];
            HttpMethod httpMethod = HttpMethod.from(words[0]);
            QueryParameters params = parseParameters(words[1]);

            if (requestPath.equals("/login")) {
                requestPath = "/login.html";
                login(params);
            }

            final var responseBody = getStaticPage(requestPath);
            final var response = buildResponse(responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void validateHeader(String header) {
        if (header.split(" ").length < 3) {
            throw new IllegalArgumentException();
        }
    }

    private String buildResponse(String responseBody) {
        int bodyLength = getBodyLength(responseBody);
        final var response = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + bodyLength + " ",
            "",
            responseBody);
        return response;
    }

    private int getBodyLength(String responseBody) {
        if (responseBody == null) {
            return 0;
        }
        return responseBody.getBytes().length;
    }

    private String getStaticPage(String requestPath) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("static" + requestPath);
        if (resource == null) {
            log.info("Resource not found!");
            throw new IllegalArgumentException();
        }
        List<String> strings = Files.readAllLines(Paths.get(resource.toURI()));

        StringBuilder sb = new StringBuilder();
        for (var str : strings) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }

    private void login(QueryParameters queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = findUser(account, password);
        log.info(user.toString());
    }

    private User findUser(String account, String password) {
        if (account == null || password == null) {
            throw new IllegalArgumentException("필수 정보가 누락되었습니다.");
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (!user.get().checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        return user.get();
    }

    private QueryParameters parseParameters(String url) {
        QueryParameters queryParameters = new QueryParameters();
        String[] words = url.split("\\?");
        if (words.length > 1) {
            for (var p : words[1].split("&")) {
                queryParameters.put(p);
            }
        }
        return queryParameters;
    }

    private String parseRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        int byteSum = 0;
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            sb.append(line).append("\r\n");
            byteSum += line.length() + 2;
            if (byteSum > MAX_REQUEST_SIZE) {
                throw new IllegalArgumentException();
            }
        }

        return sb.toString();
    }
}
