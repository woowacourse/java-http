package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
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
        try (var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            String uri = getUri(inputStream);

            if ("/favicon.ico".equals(uri)) {
                handleFaviconRequest(outputStream);
                return;
            }
            
            if (uri.startsWith("/login")) {
                uri = getLoginFileUri(uri);
            }
            var responseBody = getStaticFileContent(uri);
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + getFileExtension(uri) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUri(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.readLine().split(" ")[1];
    }

    private void handleFaviconRequest(OutputStream outputStream) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 204 No Content",
                "Content-Length: 0",
                "",
                "");
        writeResponse(outputStream, response);
    }

    private String getLoginFileUri(String uri) {
        int questionMarkIndex = uri.indexOf("?");
        validateContainsQueryString(questionMarkIndex);
        String fileName = uri.substring(0, questionMarkIndex) + ".html";
        checkLoginQueryString(uri.substring(questionMarkIndex + 1));

        return fileName;
    }

    private void validateContainsQueryString(int questionMarkIndex) {
        if (questionMarkIndex == -1) {
            throw new IllegalArgumentException("로그인 요청에 쿼리파라미터를 포함시켜주세요.");
        }
    }

    private void checkLoginQueryString(String queryString) {
        Map<String, String> queryStringPairs = getQueryStringPairs(queryString);

        String account = queryStringPairs.get("account");
        String password = queryStringPairs.get("password");
        if (account != null & password != null) {
            InMemoryUserRepository.findByAccount(account).ifPresent(user -> {
                if ("password".equals(password)) {
                    log.info("user : {}", user);
                }
            });
        }
    }

    private Map<String, String> getQueryStringPairs(String queryString) {
        Map<String, String> queryStringPairs = new HashMap<>();
        for (String pairs : queryString.split("&")) {
            String[] pair = pairs.split("=");
            if (pair.length == 2) {
                queryStringPairs.put(pair[0], pair[1]);
            }
        }

        return queryStringPairs;
    }

    private String getStaticFileContent(String path) throws IOException {
        if (Objects.equals(path, "/")) {
            return "Hello world!";
        }
        String staticPath = "static/" + path;

        File file = new File(getClass().getClassLoader().getResource(staticPath).getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getFileExtension(String path) {
        if (Objects.equals(path, "/")) {
            return "html";
        }
        String[] splitPath = path.split("\\.");
        return splitPath[splitPath.length - 1];
    }

    private void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
