package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            // 요청 헤더 읽기
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder builder = new StringBuilder();

            String line;
            while (!(line = reader.readLine()).isBlank()) {
                builder.append(line);
                builder.append("\r\n");
            }

            String request = builder.toString();
            if (request.isBlank()) {
                return;
            }

            // 경로, 타입, 바디 가져옴
            String uri = request.split(" ")[1];
            log.info("uri: {}", uri);

            String type = "html";
            if (uri.contains(".")) {
                type = List.of(uri.split("\\.")).getLast();
            }
            log.info("type: {}", type);

            String queryString = "";
            if (uri.contains("?")) {
                queryString = List.of(uri.split("\\?")).getLast();
            }

            log.info("queryString: {}", queryString);

            Map<String, String> pairs = new LinkedHashMap<>();
            if (!queryString.isBlank()) {
                uri = List.of(uri.split("\\?")).getFirst() + ".html";
                List<String> queries = List.of(queryString.split("&"));

                for (String query : queries) {
                    String[] pair = query.split("=");
                    log.info("pair[0]: {}", pair[0]);
                    log.info("pair[1]: {}", pair[1]);
                    pairs.put(pair[0], pair[1]);
                }
                Optional<User> user = InMemoryUserRepository.findByAccount(pairs.get("account"));
                if (user.isPresent() && user.get().checkPassword(pairs.get("password"))) {
                    log.info("user : {}", user.get());
                }
            }

            var responseBody = "Hello world!";
            if (!uri.equals("/") && !uri.isBlank()) {
                URL resource = getClass().getClassLoader().getResource("static" + uri);
//                log.info("resource = {}", resource);

                final String filePath = resource.getFile();
                final Path path = Paths.get(filePath);
//                log.info("path = {}", path);

                responseBody = Files.readString(path);
            }

            // 응답 생성
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + type + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
