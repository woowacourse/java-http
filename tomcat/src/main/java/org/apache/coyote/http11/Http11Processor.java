package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String STATIC_PATH = "/static";

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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            Path path = Path.of("");
            StringBuilder responseBody = new StringBuilder();
            String contentType = "";
            String statusCode = "";
            String location = "";

            String rawLine;
            Map<String, String> header = new HashMap<>();
            header.put("requestLine", bufferedReader.readLine());
            while ((rawLine = bufferedReader.readLine()) != null && !rawLine.isEmpty()) {
                if (!rawLine.isEmpty()) {
                    String[] line = rawLine.split(": ", 2);
                    header.put(line[0], line[1]);
                }
            }

            if (header.get("requestLine").startsWith("GET")) {  // TODO: 404 추가하기
                String uri = header.get("requestLine").split(" ")[1];
                if (uri.equals("/")) {
                    contentType = "text/html; charset=utf-8 ";
                    statusCode = "200 OK";
                    path = Path.of(getClass().getResource(STATIC_PATH + "/index.html").getPath());
                }
                if (uri.startsWith("/login")) {
                    contentType = "text/html; charset=utf-8 ";

                    int index = uri.indexOf("?");
                    if (index == -1) { // 쿼리 파라미터가 없는 경우
                        statusCode = "200 OK";
                        path = Path.of(getClass().getResource(STATIC_PATH + "/login.html").getPath());
                    }
                    if (index != -1) { // 쿼리 파라미터가 있는 경우
                        String queryString = uri.substring(index + 1);
                        String[] userInfo = queryString.split("&");
                        String account = userInfo[0].split("=")[1];
                        String password = userInfo[1].split("=")[1];
                        Optional<User> user = InMemoryUserRepository.findByAccount(account);
                        if (!user.isPresent()
                            || (user.isPresent() && !user.get().checkPassword(password))) {
                            statusCode = "401 UNAUTHORIZED ";
                            path = Path.of(getClass().getResource(STATIC_PATH + "/401.html").getPath());
                        }
                        if (user.isPresent() && user.get().checkPassword(password)) {
                            log.info(user.get().toString());
                            statusCode = "302 FOUND ";
                            location = "/index.html";
                        }
                    }
                    if (uri.endsWith(".html")) {
                        contentType = "text/html; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());
                    }
                    if (uri.endsWith(".css")) {
                        contentType = "text/css; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());

                    }
                    if (uri.endsWith(".js")) {
                        contentType = "application/javascript ";
                        path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());

                    }
                }
            }

            var response = "";
            if (statusCode.startsWith("200") || statusCode.startsWith("401")) {
                Files.readAllLines(path)
                        .stream()
                        .forEach(line -> responseBody.append(line).append("\n"));

                response = String.join("\r\n",
                        "HTTP/1.1 " + statusCode,
                        "Content-Type: " + contentType,
                        "Content-Length: " + responseBody.toString().getBytes().length + " ",
                        "",
                        responseBody);
            }
            if (statusCode.startsWith("302")) {
                response = String.join("\r\n",
                        "HTTP/1.1 " + statusCode,
                        "Location: " + location);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
