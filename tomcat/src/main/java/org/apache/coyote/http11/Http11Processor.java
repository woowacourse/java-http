package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCE_PATH = "static";

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()
        ) {
            List<String> request = getRequest(inputStream);
            String requestUrl = request.getFirst()
                    .split(" ")[1];
            Map<String, String> queryParams = getQueryParams(requestUrl);

            if (requestUrl.startsWith("/login") && !queryParams.isEmpty()) {
                checkLoginUser(queryParams);
            }

            outputStream.write(getResponse(requestUrl).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getRequest(InputStream inputStream) throws IOException {
        List<String> request = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (bufferedReader.ready()) {
            request.add(bufferedReader.readLine());
        }

        return request;
    }

    private Map<String, String> getQueryParams(String requestUrl) {
        Map<String, String> queryParams = new HashMap<>();

        if (requestUrl.contains("?")) {
            String queryString = requestUrl.split("\\?")[1];
            queryParams = Arrays.stream(queryString.split("&"))
                    .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        }

        return queryParams;
    }

    private void checkLoginUser(Map<String, String> queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    if (user.checkPassword(password)) {
                        log.info("user : {}", user);
                    }
                });
    }

    private String getResponse(String requestUrl) throws IOException {
        String responseBody = getResponseBody(requestUrl);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + ContentType.findContentType(requestUrl) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getResponseBody(String requestUrl) throws IOException {
        if (requestUrl.equals("/")) {
            return "Hello world!";
        }

        String path = requestUrl.split("\\?")[0];
        if (!path.contains(".")) {
            path += ".html";
        }

        URL resourceUrl = getResourceUrl(path);

        Path resourcePath = Path.of(resourceUrl.getPath());
        return Files.readString(resourcePath);
    }

    private URL getResourceUrl(String path) {
        URL resourceUrl = getClass().getClassLoader()
                .getResource(RESOURCE_PATH + path);

        if (resourceUrl == null) {
            return getClass().getClassLoader()
                    .getResource(RESOURCE_PATH + "/404.html");
        }

        return resourceUrl;
    }
}
