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
import java.util.HashMap;
import java.util.Map;
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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            // 헤더
            String firstLineOfRequestHeader = br.readLine();
            String requestUri = firstLineOfRequestHeader.split(" ")[1];
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(END_OF_REQUEST_HEADER)) {
                    break;
                }
            }

            int index = requestUri.lastIndexOf(REQUEST_URI_DELIMITER);

            String requestUriPath = requestUri;
            Map<String, String> keyValues = new HashMap<>();
            if (index != -1) {
                requestUriPath = requestUri.substring(0, index);
                // keyValues 저장;
                String queryString = requestUri.substring(index + 1);
                for (String keyValue : queryString.split(QUERY_STRING_DELIMITER)) {
                    String[] split = keyValue.split("=");
                    keyValues.put(split[0], split[1]);
                }
            }

            // 바디
            var staticFileName = getStaticFileName(requestUriPath, keyValues);
            var responseBody = getResponseBody(staticFileName);
            var contentType = getContentType(staticFileName);

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

    private String getStaticFileName(String requestUriPath, Map<String, String> keyValues) throws IOException {
        if (requestUriPath.equals("/")) {
            return "";
        }
        if (requestUriPath.equals("/login")) {
            return login(keyValues);
        }

        return requestUriPath;
    }

    private String getResponseBody(String staticFileName) throws IOException {
        if (staticFileName.isBlank()) {
            return "Hello world!";
        }

        String staticFilePath = STATIC_RESOURCE_PATH + "/" + staticFileName;
        URL resource = getClass().getClassLoader().getResource(staticFilePath);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String login(Map<String, String> keyValues) {
        String account = keyValues.get("account");
        String password = keyValues.get("password");
        if (account != null && password != null) {
            Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
            boolean isValidAccount = findUser.isPresent();
            if (!isValidAccount) {
                return null;
            }
            User user = findUser.get();
            log.atInfo().log("user: {}", user);
        }
        return "login.html";
    }

    private String getContentType(String staticFileName) {
        if (staticFileName.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
