package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            HttpRequestFirstLine firstLine = new HttpRequestFirstLine(bufferedReader.readLine());
            String requestURL = firstLine.getUrl();

            if (Objects.equals(firstLine.getMethod(), "GET")) {
                processGetRequest(requestURL, outputStream);
                return;
            }

            if (Objects.equals(firstLine.getMethod(), "POST")) {
                processPostRequest(requestURL, bufferedReader, outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String readLine;
        while (!"".equals(readLine = bufferedReader.readLine())) {
            String[] split = readLine.split(": ");
            header.put(split[0], split[1]);
        }

        return header;
    }

    private void processGetRequest(String requestURL, OutputStream outputStream) throws URISyntaxException, IOException {
        if ("/".equals(requestURL)) {
                processRootRequest(outputStream);
                return;
            }

        final Path path = findPath(requestURL);
        byte[] fileBytes = Files.readAllBytes(path);
        String contentType = URLConnection.guessContentTypeFromName(path.toString());

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + fileBytes.length + " ",
                "",
                "");

        outputStream.write(response.getBytes());
        outputStream.write(fileBytes);

        outputStream.flush();
    }

    private Path findPath(String requestURL) throws URISyntaxException {
        if (!requestURL.contains(".")) {
            requestURL += ".html";
        }

        URI resource = getClass().getClassLoader().getResource("static" + requestURL).toURI();
        return Path.of(resource);
    }

    private void processPostRequest(String requestURL, BufferedReader bufferedReader, OutputStream outputStream)
            throws URISyntaxException, IOException {
        final Path path = findPath(requestURL);

        Map<String, String> header = getHeader(bufferedReader);

        int contentLength = Integer.parseInt(header.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        Map<String, String> bodys = new HashMap<>();
        String[] querys = requestBody.split("&");
        for (String query : querys) {
            String[] keyAndValue = query.split("=");
            bodys.put(keyAndValue[0], keyAndValue[1]);
        }

        if (requestURL.equals("/register")) {
            User user = new User(bodys.get("account"), bodys.get("password"), bodys.get("email"));
            InMemoryUserRepository.save(user);
        }

        if (requestURL.equals("/login")) {
            try {
                User user = InMemoryUserRepository.findByAccount(bodys.get("account"))
                        .orElseThrow();
                if (!user.checkPassword(bodys.get("password"))) {
                    throw new RuntimeException();
                }
                log.info("user: {}", user);
            } catch (Exception e) {
                processGetRequest("/401.html", outputStream);
            }
        }

        final var response = String.join("\r\n",
                "HTTP/1.1 302 OK ",
                "Location: /index.html ",
                "",
                "");

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processRootRequest(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
