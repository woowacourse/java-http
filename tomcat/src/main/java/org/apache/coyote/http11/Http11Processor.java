package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
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
            StringBuilder requestBuilder = new StringBuilder();
            String line;
            java.io.BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                requestBuilder.append(line).append("\r\n");
            }

            String request = requestBuilder.toString();
            String header = request.split("\r\n")[0];
            String[] words = header.split(" ");
            log.info(header);

            HttpMethod httpMethod = HttpMethod.from(words[0]);
            String[] url = words[1].split("\\?");
            String path = url[0];
            String param = url.length > 1 ? url[1] : null;
            Map<String, String> params = new HashMap<>();
            if (param != null) {
                for (var p : param.split("&")) {
                    String key = p.split("=")[0];
                    String value = p.split("=")[1];
                    params.put(key, value);
                }
            }

            if (path.equals("/login")) {
                path = "/login.html";
                String account = params.get("account");
                String password = params.get("password");
                if (account == null || password == null) {
                    log.info("필수 정보가 누락되었습니다.");
                }
                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                if (user.isEmpty()) {
                    log.info("존재하지 않는 사용자입니다.");
                } else if (!user.get().checkPassword(password)) {
                    log.info("비밀번호가 틀렸습니다.");
                } else {
                    log.info(user.toString());
                }
            }

            URL resource = getClass().getClassLoader().getResource("static" + path);
            if (resource == null) {
                log.info("Resource not found!");
                return;
            }
            List<String> strings = Files.readAllLines(Paths.get(resource.toURI()));

            StringBuilder sb = new StringBuilder();
            for (var str : strings) {
                sb.append(str + "\n");
            }

            final var responseBody = sb.toString();

            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
