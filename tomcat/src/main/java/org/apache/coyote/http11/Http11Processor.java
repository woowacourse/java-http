package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

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
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String REQUEST_URI_DELIMITER = "?";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String END_OF_REQUEST_HEADER = "";

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

            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String firstLineOfRequestHeader = br.readLine();
            String requestUri = firstLineOfRequestHeader.split(" ")[1];
            // 헤더
            while ((line = br.readLine()) != null) {
                if (line.equals(END_OF_REQUEST_HEADER)) {
                    break;
                }
            }

            String requestUriPath = requestUri;
            int index = requestUri.lastIndexOf(REQUEST_URI_DELIMITER);
            if (index != -1) {
                requestUriPath = requestUri.substring(0, index);
                String queryString = requestUri.substring(index + 1);
                String[] keyValues = queryString.split(QUERY_STRING_DELIMITER);
                String account = null;
                String password = null;
                for (String keyValue : keyValues) {
                    String[] split = keyValue.split("=");
                    String key = split[0];
                    String value = split[1];
                    if (key.equals("account")) {
                        account = value;
                    }
                    if (key.equals("password")) {
                        password = value;
                    }
                }

                if (account != null && password != null) {
                    Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
                    boolean isValidAccount = findUser.isPresent();
                    if (!isValidAccount) {
                        return;
                    }
                    User user = findUser.get();
                    log.atInfo().log("user: {}", user);
                }
            }

            if (requestUriPath.equals("/login")) {
                requestUriPath+=".html";
            }

            // 바디
            var responseBody = getResponseBody(requestUriPath);
            var contentType = getContentType(requestUriPath);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: %s;charset=utf-8 ".formatted(contentType),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String requestUriPath) throws IOException {
        if (requestUriPath.equals("/")) {
            return "Hello world!";
        }
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + requestUriPath);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getContentType(String requestUriPath) {
        if (requestUriPath.endsWith(".css")) {
            return "text/css";
        }
        if (requestUriPath.endsWith(".html")) {
            return "text/html";
        }
        return "text/html";
    }
}
