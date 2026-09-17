package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String reqFirstLine = bufferedReader.readLine();

            String reqUrl = reqFirstLine.split(" ")[1];
            int queryIndex = reqUrl.indexOf("?");

            String pathUrl = reqUrl;
            String query = "";

            if (queryIndex != -1) {
                pathUrl = reqUrl.substring(0, queryIndex);
                query = reqUrl.substring(queryIndex + 1);
            }

            String responseBody;

            if (pathUrl.equals("/")) {
                responseBody = "Hello world!";

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                        "",
                        responseBody);

                writeAndFlush(outputStream, response);
                return;
            }

            if (pathUrl.equals("/login") && !query.isEmpty()) {
                login(query);
            }

            if (!pathUrl.contains(".")) {
                pathUrl = pathUrl + ".html";
            }

            String staticUrl = "static" + pathUrl;
            Path path = getPath(staticUrl);
            final var response = getResponse(path);
            writeAndFlush(outputStream, response);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void login(String query) {
        Map<String, String> queryParams = parseQueryParams(query);

        if (queryParams.get("account") == null || queryParams.get("password") == null) {
            return;
        }

        User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        if (user.checkPassword(queryParams.get("password"))) {
            log.info("user : {}", user.toString());
        }
    }

    private static void writeAndFlush(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String getResponse(Path path) throws IOException {
        String responseBody = Files.readString(path);
        String type = extractType(path);

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
        return response;
    }

    private static String extractType(Path path) {
        if (path.toString().endsWith(".css")) {
            return "css";
        }
        if (path.toString().endsWith(".js")) {
            return "javascript";
        }
        return "html";
    }

    private static Map<String, String> parseQueryParams(String queryParams) {
        Map<String, String> queries = new HashMap<>();

        for (String s : queryParams.split("&")) {
            queries.put(s.split("=")[0], s.split("=")[1]);
        }

        return queries;
    }

    private Path getPath(String filePath) throws URISyntaxException {
        return Path.of(getClass().getClassLoader()
                .getResource(filePath)
                .toURI());
    }
}
