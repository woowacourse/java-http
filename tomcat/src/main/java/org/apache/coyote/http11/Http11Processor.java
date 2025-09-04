package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            List<String> requestHeader = extractHttpRequest(inputStream);
            String startLine = requestHeader.getFirst();
            String requestURI = startLine.split(" ")[1];

            String responseBody = loadResource(requestURI);
            String response = createHttpResponse(requestURI, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> extractHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> requestLines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            requestLines.add(line);
        }
        return requestLines;
    }

    private String loadResource(String requestURI) throws IOException {
        if (requestURI.equals("/")) {
            return "Hello world!";
        }

        int queryIndex = requestURI.indexOf("?");

        if (queryIndex == -1) {
            URL resource = getClass().getClassLoader()
                    .getResource("static" + requestURI);

            Path path = new File(resource.getPath()).toPath();
            return Files.readString(path);
        }

        String filePath = requestURI.substring(0, queryIndex);
        Map<String, String> queryStrings = parseQueryStrings(requestURI, queryIndex);
        checkUser(queryStrings);

        URL resource = getClass().getClassLoader()
                .getResource("static" + filePath + ".html");

        Path path = new File(resource.getPath()).toPath();
        return Files.readString(path);
    }

    private void checkUser(Map<String, String> queryStrings) {
        User user = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(queryStrings.get("password"))) {
            throw new IllegalArgumentException();
        }

        log.info(user.toString());
    }

    private Map<String, String> parseQueryStrings(String requestURI, int queryIndex) {
        String query = requestURI.substring(queryIndex + 1);;
        Map<String, String> result = new HashMap<>();

        for (String param : query.split("&")) {
            String[] keyValue = param.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    private String createHttpResponse(String requestURI, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + extractContentType(requestURI) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String extractContentType(String requestURI) {
        if (requestURI.contains(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
