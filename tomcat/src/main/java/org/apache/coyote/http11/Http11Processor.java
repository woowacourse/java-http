package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            HttpRequest request = HttpRequest.from(extractRequestHeader(inputStream));
            String requestURI = request.getURI();
            HttpResponse response = createHttpResponse(requestURI);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> extractRequestHeader(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> headers = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headers.add(line);
        }
        return headers;
    }

    private HttpResponse createHttpResponse(String requestURI) throws IOException {
        if (requestURI.equals("/")) {
            return HttpResponse.from(requestURI, "Hello world!");
        }

        int queryIndex = requestURI.indexOf("?");

        if (queryIndex == -1) {
            return HttpResponse.from(requestURI, readResourceFile("static" + requestURI));
        }
        String path = requestURI.substring(0, queryIndex);
        Map<String, String> queryStrings = parseQueryStrings(requestURI, queryIndex);
        checkUser(queryStrings);

        return HttpResponse.from(requestURI, readResourceFile("static" + path + ".html"));
    }

    private String readResourceFile(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource(path);
        Path filePath = new File(resource.getPath()).toPath();
        return Files.readString(filePath);
    }

    private Map<String, String> parseQueryStrings(String requestURI, int queryIndex) {
        String query = requestURI.substring(queryIndex + 1);
        Map<String, String> result = new HashMap<>();

        for (String param : query.split("&")) {
            String[] keyValue = param.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    private void checkUser(Map<String, String> queryStrings) {
        User user = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(queryStrings.get("password"))) {
            throw new IllegalArgumentException();
        }
        log.info(user.toString());
    }
}
