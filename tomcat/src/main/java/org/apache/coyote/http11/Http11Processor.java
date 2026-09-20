package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String headerFirstLine = bufferedReader.readLine();

            String reqUri = headerFirstLine.split(" ")[1];
            int queryIndex = reqUri.indexOf("?");

            String pathUri = reqUri;
            String query = "";

            if (queryIndex != -1) {
                pathUri = reqUri.substring(0, queryIndex);
                query = reqUri.substring(queryIndex + 1);
            }

            String responseBody;

            if (pathUri.equals("/")) {
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

            if (pathUri.equals("/login") && !query.isEmpty()) {
                if (!login(query)) {
                    pathUri = "/401.html";
                } else {
                    writeAndFlush(outputStream, createRedirectResponse("/index.html"));
                    return;
                }
            }

            if (!pathUri.contains(".")) {
                pathUri = pathUri + ".html";
            }

            String staticUrl = "static" + pathUri;
            Path path = getPath(staticUrl);
            log.debug("staticPath: {}", path);
            final var response = createStaticFileResponse(path);
            writeAndFlush(outputStream, response);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static boolean login(String query) {
        Map<String, String> queryParams = parseQueryParams(query);

        if (queryParams.get("account") == null || queryParams.get("password") == null) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .map(user -> {
                    if (user.checkPassword(queryParams.get("password"))) {
                        log.info("user : {}", user.toString());
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    private static void writeAndFlush(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String createStaticFileResponse(Path path) throws IOException {
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

    private static String createRedirectResponse(String loaction) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + loaction,
                "Content-Length: 0",
                "",
                "");
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
