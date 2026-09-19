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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }
            String[] requestLine = firstLine.split(" ");
            Map<String, String> header = new HashMap<>();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                String[] headerLine = line.split(":");
                header.put(headerLine[0], headerLine[1].trim());
            }
            String contentLength = header.get("Content-Length");
            if (contentLength != null) {
                char[] buffer = new char[Integer.parseInt(contentLength)];
                int count = bufferedReader.read(buffer, 0, Integer.parseInt(contentLength));
                String requestBody = new String(buffer, 0, count);
            }
            if ("/".equals(requestLine[1])) {
                writeResponse(outputStream, "200 OK", "text/html", "Hello world!");
            } else if (requestLine[1].startsWith("/login")) {
                if (Arrays.asList(requestLine[1].split("")).contains("?")) {
                    String queryString = requestLine[1].substring(requestLine[1].indexOf("?") + 1);
                    String[] queryStringWithAndSplits = queryString.split("&");
                    Map<String, String> queryStringMap = new HashMap<>();
                    for (String queryStringWithAndSplit : queryStringWithAndSplits) {
                        String[] split = queryStringWithAndSplit.split("=");
                        queryStringMap.put(split[0], split[1]);
                    }
                    Optional<User> optionalUser = InMemoryUserRepository.findByAccount(queryStringMap.get("account"));
                    if (optionalUser.isEmpty()) {
                        return;
                    }
                    User user = optionalUser.get();
                    if (!user.checkPassword(queryStringMap.get("password"))) {
                        throw new IllegalArgumentException("아이디와 비밀번호를 다시 확인하고 입력해주세요.");
                    }
                    log.info(user.toString());
                }
                writeStaticFile(outputStream, "/login.html");
            } else {
                writeStaticFile(outputStream, requestLine[1]);
            }
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeStaticFile(final OutputStream outputStream, final String target)
            throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("static" + target);
        if (resource == null) {
            URL notFound = getClass().getClassLoader().getResource("static/404.html");
            writeResponse(outputStream, "404 Not Found", "text/html", Files.readString(Path.of(notFound.toURI())));
            return;
        }
        writeResponse(outputStream, "200 OK", contentTypeOf(target), Files.readString(Path.of(resource.toURI())));
    }

    private String contentTypeOf(final String target) {
        if (target.endsWith(".css")) {
            return "text/css";
        }
        if (target.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }

    private void writeResponse(final OutputStream outputStream, final String status, final String contentType,
                               final String responseBody) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
