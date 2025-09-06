package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final Socket connection;

    public Http11Processor(Socket connection) {
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

            String firstLine = getFirstLine(bufferedReader);

            HttpRequest httpRequest = HttpRequest.parseByFirstLine(firstLine);
            String uri = httpRequest.uri();
            String path = uri;

            Map<String, String> queryStrings = new HashMap<>();

            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                path = uri.substring(0, index);
                queryStrings = parseQueryStrings(uri, index);
            }

            HttpResponse response = null;

            if (StaticResourceExtension.anyMatch(path)) {
                response = handleForStaticResource(path);
            }

            if (uri.contains("/login") && queryStrings.isEmpty()) {
                response = handleForStaticResource("login.html");
            }

            if (uri.contains("/login") && !queryStrings.isEmpty()) {
                response = handleForLogin(queryStrings);
            }

            if (response == null) {
                response = HttpResponse.createOKResponse(DEFAULT_RESPONSE_BODY, uri);
            }

            outputStream.write(response.parseToString().getBytes());
            outputStream.flush();
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFirstLine(BufferedReader bufferedReader) throws IOException {
        String firstLine = bufferedReader.readLine();
        if (firstLine == null || firstLine.isBlank()) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }
        return firstLine;
    }

    private Map<String, String> parseQueryStrings(String uri, int index) {
        if (index == -1) {
            return null;
        }

        String rawQueryString = uri.substring(index + 1);
        String[] pairs = rawQueryString.split("&");

        Map<String, String> queryParams = new HashMap<>();
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            queryParams.put(key, value);
        }
        return queryParams;
    }

    private HttpResponse handleForStaticResource(String uri) throws IOException {
        log.info("uri = {}", uri);
        URL resource = getPathOfResource(uri);
        String responseBody = readFile(resource);

        return HttpResponse.createOKResponse(responseBody, uri);
    }

    private URL getPathOfResource(String uri) {
        URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (resource != null) {
            return resource;
        }

        throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
    }

    private static String readFile(URL resource) throws IOException {
        File file = new File(resource.getFile());
        return Files.readString(file.toPath());
    }

    private HttpResponse handleForLogin(Map<String, String> queryStrings) {
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(queryStrings.get("account"));

        if (foundUser.isPresent() && foundUser.get().checkPassword(queryStrings.get("password"))) {
            return HttpResponse.createRedirectionResponse("index.html");
        }

        return HttpResponse.createRedirectionResponse("401.html");
    }
}
