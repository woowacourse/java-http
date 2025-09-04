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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

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
            String request = parseRequest(inputStream);
            String header = request.split("\r\n")[0];
            String[] words = header.split(" ");
            String requestPath = words[1].split("\\?")[0];
            HttpMethod httpMethod = HttpMethod.from(words[0]);
            Map<String, String> params = parseParameters(words[1]);

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

    private String buildResponse(String responseBody) {
        int bodyLength = responseBody == null ? 0 : responseBody.getBytes().length;
        final var response = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + bodyLength + " ",
            "",
            responseBody);
        return response;
    }

    private String getStaticPage(String requestPath) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("static" + requestPath);
        if (resource == null) {
            log.info("Resource not found!");
            return null;
        }
        List<String> strings = Files.readAllLines(Paths.get(resource.toURI()));

        StringBuilder sb = new StringBuilder();
        for (var str : strings) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }

    private void login(Map<String, String> params) {
        Optional<User> user = Optional.empty();
        String account = params.get("account");
        String password = params.get("password");
        if (account == null || password == null) {
            log.info("필수 정보가 누락되었습니다.");
        } else {
            user = InMemoryUserRepository.findByAccount(account);
        }
        if (user.isEmpty()) {
            log.info("존재하지 않는 사용자입니다.");
        } else if (!user.get().checkPassword(password)) {
            log.info("비밀번호가 틀렸습니다.");
        } else {
            log.info(user.toString());
        }
    }

    private Map<String, String> parseParameters(String url) {
        HashMap<String, String> parameters = new HashMap<>();
        String[] words = url.split("\\?");
        if (words.length > 1) {
            for (var p : words[1].split("&")) {
                String key = p.split("=")[0];
                String value = p.split("=")[1];
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    private String parseRequest(InputStream inputStream) throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            requestBuilder.append(line).append("\r\n");
        }

        return requestBuilder.toString();
    }
}
