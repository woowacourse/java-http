package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    public static final String STATIC_RESOURCE_PATH = "static";
    public static final String HTTP_REQUEST_LINE = "Request-Line";
    public static final String HTTP_HEADER_ACCEPT = "Accept";
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
        try (
                final var inputStream = connection.getInputStream();
                final var inputStreamReader = new InputStreamReader(inputStream);
                final var bufferedReader = new BufferedReader(inputStreamReader);
                final var outputStream = connection.getOutputStream()
        ) {
            final Map<String, String> request = parseRequest(bufferedReader);
            final var requestLine = request.get(HTTP_REQUEST_LINE);
            final var uri = requestLine.split(" ")[1];
            if (uri.contains("?")) {
                final var queryStartIndex = uri.indexOf("?");
                final var queryString = uri.substring(queryStartIndex + 1);
                final var parameters = parseParameters(queryString);
                logUser(parameters);
            }

            final var responseBody = readFile(uri);
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    request.get(HTTP_HEADER_ACCEPT),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseRequest(BufferedReader bufferedReader)
            throws IOException {
        final Map<String, String> request = new HashMap<>();
        final var requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            throw new RuntimeException();
        }
        request.put(HTTP_REQUEST_LINE, requestLine);

        var requestHeader = bufferedReader.readLine();
        while (!requestHeader.isEmpty()) {
            String[] keyValuePair = requestHeader.split(": ");
            request.put(keyValuePair[0], keyValuePair[1]);
            requestHeader = bufferedReader.readLine();
        }
        return request;
    }

    private Map<String, String> parseParameters(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        String[] parametersArray = queryString.split("&");
        for (String parameter : parametersArray) {
            String[] split = parameter.split("=");
            parameters.put(split[0], split[1]);
        }
        return parameters;
    }

    private void logUser(Map<String, String> parameters) {
        if (parameters.containsKey("account") && parameters.containsKey("password")) {
            User user = InMemoryUserRepository.findByAccount(parameters.get("account"))
                    .orElseThrow();
            if (user.checkPassword(parameters.get("password"))) {
                log.info(user.toString());
            }
        }
    }

    private String readFile(String uri) throws IOException {
        if (uri.equals("/")) {
            return "Hello world!";
        }
        final String filePath = getFilePath(uri);
        URL resource = findResource(filePath);
        Path path = Paths.get(resource.getFile());
        return new String(Files.readAllBytes(path));
    }

    private String getFilePath(String uri) {
        String filePath = uri.split("\\?")[0];
        if (!filePath.matches(".+\\.[a-zA-Z]+$")) {
            return filePath + ".html";
        }
        return filePath;
    }

    private URL findResource(String fileName) {
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + fileName);
        if (resource == null) {
            return getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + "/404.html");
        }
        return resource;
    }
}
