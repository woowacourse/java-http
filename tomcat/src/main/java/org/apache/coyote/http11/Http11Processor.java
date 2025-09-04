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
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String OK_RESPONSE_LINE = "HTTP/1.1 200 OK ";
    private static final String CONTENT_TYPE_RESPONSE_HEADER_KEY = "Content-Type: ";
    private static final String CONTENT_LENGTH_RESPONSE_HEADER_KEY = "Content-Length: ";
    private static final String CONTENT_TYPE_RESPONSE_LINE_CHARSET_UTF_8 = ";charset=utf-8 ";
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

            String uri = parseUrl(bufferedReader);
            String responseBody = null;

            if (StaticResourceExtension.anyMatch(uri)) {
                responseBody = handleForStaticResource(uri);
            }

            if (uri.contains("/login")) {
                responseBody = handleForStaticResource("login.html");
                handleForLogin(uri);
            }

            if (responseBody == null) {
                responseBody = DEFAULT_RESPONSE_BODY;
            }

            final var response = String.join("\r\n",
                    OK_RESPONSE_LINE,
                    CONTENT_TYPE_RESPONSE_HEADER_KEY + StaticResourceExtension.findMimeTypeByUrl(uri)
                            + CONTENT_TYPE_RESPONSE_LINE_CHARSET_UTF_8,
                    CONTENT_LENGTH_RESPONSE_HEADER_KEY + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUrl(BufferedReader bufferedReader) throws IOException {
        String firstLine = bufferedReader.readLine();
        if (firstLine.isBlank()) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }
        return firstLine.split(" ")[1];
    }

    private String handleForStaticResource(String url) throws IOException {
        URL resource = getPathOfResource(url);
        return readFile(resource);
    }

    private URL getPathOfResource(String url) {
        URL resource = getClass().getClassLoader().getResource("static/" + url);
        if (resource != null) {
            return resource;
        }

        throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
    }

    private static String readFile(URL resource) throws IOException {
        File file = new File(resource.getFile());
        return Files.readString(file.toPath());
    }

    private void handleForLogin(String uri) {
        Map<String, String> queryStrings = getQueryStrings(uri);

        User foundUser = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        log.info("로그인한 사용자의 이름: {}", foundUser.getAccount());
    }

    private Map<String, String> getQueryStrings(String uri) {
        int index = uri.indexOf("?");

        String[] rawQueryStrings = uri.substring(index + 1).split("&");

        Map<String, String> queryString = new HashMap<>();
        for (String rawQueryString : rawQueryStrings) {
            String[] splitQueryString = rawQueryString.split("=");
            queryString.put(splitQueryString[0], splitQueryString[1]);
        }

        return queryString;
    }
}
