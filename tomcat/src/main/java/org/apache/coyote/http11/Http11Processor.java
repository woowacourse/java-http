package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCE_PATH = "static";
    private static final int URL_INDEX = 1;
    public static final String ILLEGAL_REQUEST = "부적절한 요청입니다.";
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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader br = new BufferedReader(inputStreamReader)) {

            String requestLine = br.readLine();
            if (Objects.isNull(requestLine)) {
                throw new IllegalArgumentException(ILLEGAL_REQUEST);
            }

            URL resource = getResource(requestLine);
            if (Objects.isNull(resource)) {
                throw new IllegalArgumentException(ILLEGAL_REQUEST);
            }

            String response = buildResponse(resource);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private URL getResource(String requestLine) {
        String requestUri = requestLine.split(" ")[URL_INDEX];

        if (requestUri.contains("/login")) {
            int index = requestUri.indexOf("?");
            if (index == -1) {
                String resourcePath = RESOURCE_PATH + "/login.html";
                return getClass().getClassLoader().getResource(resourcePath);
            }

            String path = requestUri.substring(0, index);
            String queryString = requestUri.substring(index + 1);

            String[] queryParams = queryString.split("&");
            String account = getParamValueWithKey(queryParams, "account");
            User user = InMemoryUserRepository.findByAccount(account).get();
            if (user.checkPassword(getParamValueWithKey(queryParams, "password"))) {
                log.info("User: " + user.toString());
            }

            String resourcePath = RESOURCE_PATH + path + ".html";
            return getClass().getClassLoader().getResource(resourcePath);
        }

        String resourcePath = RESOURCE_PATH + requestUri;
        return getClass().getClassLoader().getResource(resourcePath);
    }

    private String getParamValueWithKey(String[] queryParams, String key) {
        for (String param : queryParams) {
            String[] paramKeyValue = param.split("=");
            if (paramKeyValue[0].equals(key)) {
                return paramKeyValue[1];
            }
        }
        return "";
    }

    private String buildResponse(URL resource) throws IOException {
        File location = new File(resource.getFile());
        String responseBody = "";

        if (location.isDirectory()) {
            responseBody = "Hello world!";
        }

        if (location.isFile()) {
            responseBody = new String(Files.readAllBytes(location.toPath()));
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType(resource.getPath()) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String contentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
