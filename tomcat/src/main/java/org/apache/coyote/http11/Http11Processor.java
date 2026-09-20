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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String headerFirstLine = bufferedReader.readLine();

            int contentLength = readContentLength(bufferedReader);
            String reqBody = readReqBody(bufferedReader, contentLength);

            String method = headerFirstLine.split(" ")[0];
            String reqUri = headerFirstLine.split(" ")[1];

            int queryIndex = reqUri.indexOf("?");
            String pathUri = readPathUri(reqUri, queryIndex);
            String query = readQuery(reqUri, queryIndex);

            if (pathUri.equals("/register") && method.equals("POST")) {
                handleRegister(reqBody, outputStream);
                return;
            }

            if (pathUri.equals("/login") && method.equals("POST")) {
                if (login(reqBody)) {
                    writeAndFlush(outputStream, createRedirectResponse("/index.html"));
                    return;
                }
                pathUri = "/401.html";
            }

            pathUri = normalizePathUri(pathUri);
            Path path = getPath("static" + pathUri);

            String responseBody = findResponseBody(pathUri, path);
            String contentType = extractType(path);
            writeAndFlush(outputStream, createStaticFileResponse(responseBody, contentType));

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void handleRegister(String reqBody, OutputStream outputStream) throws IOException {
        Map<String, String> reqBodyParams = parseQueryParams(reqBody);

        InMemoryUserRepository.save(new User(
                reqBodyParams.get("account"),
                reqBodyParams.get("password"),
                reqBodyParams.get(("email"))
        ));

        writeAndFlush(outputStream, createRedirectResponse("/index.html"));
    }

    private static boolean login(String reqBody) {
        Map<String, String> queryParams = parseQueryParams(reqBody);

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

    private static String findResponseBody(String pathUri, Path path) throws IOException {
        if (pathUri.equals("/")) {
            return "Hello world!";
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private static void writeAndFlush(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String createStaticFileResponse(String responseBody, String type) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private static String createRedirectResponse(String loaction) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + loaction,
                "Content-Length: 0",
                "",
                "");
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

    private static Path getPath(String filePath) throws URISyntaxException {
        URL resource = Objects.requireNonNull(
                Http11Processor.class.getClassLoader().getResource(filePath),
                "리소스 못찾음"
        );

        return Path.of(resource.toURI());
    }

    private static String readReqBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return "";
        }

        char[] body = new char[contentLength];
        int offset = 0;

        while (offset < contentLength) {
            int read = bufferedReader.read(body, offset, contentLength - offset);

            if (read == -1) {
                throw new IOException("요청 바디가 중간에 끝났습니다.");
            }
            offset += read;
        }
        return new String(body);
    }

    private static int readContentLength(BufferedReader bufferedReader) throws IOException {
        String line;
        int contentLength = 0;

        while (!(line = bufferedReader.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
            }
        }
        return contentLength;
    }

    private String readQuery(String reqUri, int queryIndex) {
        if (queryIndex == -1) {
            return "";
        }
        return reqUri.substring(queryIndex + 1);
    }

    private String readPathUri(String reqUri, int queryIndex) {
        if (queryIndex == -1) {
            return reqUri;
        }
        return reqUri.substring(0, queryIndex);
    }

    private String normalizePathUri(String pathUri) {
        if (pathUri.equals("/")) {
            return "/";
        }
        if (!pathUri.contains(".")) {
            return pathUri + ".html";
        }
        return pathUri;
    }
}
