package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    public static final String QUERY_PARAMETER = "?";
    public static final String STATIC = "/static";
    public static final String HTML = ".html";
    public static final String CSS = ".css";
    public static final String JS = ".js";
    public static final String SVG = ".svg";

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
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final String line = bufferedReader.readLine();

            if (line == null) {
                return;
            }

            final String[] httpRequestLine = line.split(" ");
            final String method = httpRequestLine[0];
            final String requestURL = httpRequestLine[1];

            String contentType;
            byte[] responseBody;

            String httpStatus = "200 OK ";
            String resourcePath = determineResourcePath(requestURL);
            contentType = determineContentType(resourcePath);

            URL resource = getClass().getResource(resourcePath);

            if (resource == null) {
                resource = getClass().getResource("/static/404.html");
                httpStatus = "404 NOT FOUND ";
            }

            responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

            final var response = String.join("\r\n",
                    "HTTP/1.1 " + httpStatus,
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(response.getBytes());
            outputStream.write(responseBody);
            outputStream.flush();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String determineResourcePath(String requestURL) {
        if (requestURL.equals("/") || requestURL.equals("/index.html")) {
            return "/static/index.html";
        }

        if (requestURL.endsWith(SVG)) {
            return STATIC + "/assets/img/error-404-monochrome.svg";
        }

        if (requestURL.contains(QUERY_PARAMETER)) {
            String path = parseLoginQueryString(requestURL);
            return STATIC + path + HTML;
        }

        if (requestURL.endsWith(HTML) || requestURL.endsWith(CSS) || requestURL.endsWith(JS)) {
            return STATIC + requestURL;
        }

        return STATIC + requestURL + HTML;
    }

    private String determineContentType(String resourcePath) {
        if (resourcePath.endsWith(CSS)) {
            return "text/css ";
        }

        if (resourcePath.endsWith(JS)) {
            return "application/javascript ";
        }

        if (resourcePath.endsWith(SVG)) {
            return "image/svg+xml ";
        }

        return "text/html;charset=utf-8 ";
    }

    private String parseLoginQueryString(String requestURL) {
        int index = requestURL.indexOf(QUERY_PARAMETER);
        findUser(parseUserInfo(requestURL.substring(index + 1)));

        return requestURL.substring(0, index);
    }

    private Map<String, String> parseUserInfo(String queryString) {
        Map<String, String> userInfo = new HashMap<>();

        String[] info = queryString.split("&");

        for (String metadata : info) {
            String[] temp = metadata.split("=");
            String key = URLDecoder.decode(temp[0]);
            String value = URLDecoder.decode(temp[1]);

            userInfo.put(key, value);
        }

        return userInfo;
    }

    private void findUser(Map<String, String> userInfo) {
        String account = userInfo.get("account");
        String password = userInfo.get("password");

        InMemoryUserRepository.findByAccount(account).ifPresent(
                user -> {
                    boolean isValidPassword = user.checkPassword(password);
                    if (isValidPassword) {
                        log.info("user : {}", user);
                    }

                    if (!isValidPassword) {
                        log.info(user.getAccount() + "의 비밀번호가 잘못 입력되었습니다.");
                    }
                }
        );
    }
}
