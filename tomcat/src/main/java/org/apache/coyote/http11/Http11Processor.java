package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String DEFAULT_DIRECTORY_NAME = "static";

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = bufferedReader.readLine();

            if (requestLine == null) {
                return;
            }

            if (isLogin(requestLine)) {
                checkLogin(requestLine);
            }

            Map<String, String> requestHeaders = makeRequestHeaders(bufferedReader);
            String[] s = requestLine.split(" ");
            String body = makeResponseBody(s[1]);

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + findResponseContentType(s[1]) + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> makeRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }
            String[] split = line.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            requestHeaders.put(key, value);
        }
        return requestHeaders;
    }

    private String makeResponseBody(String requestUrl) throws IOException {
        final Path path = findPath(requestUrl);
        return Files.readString(path);
    }

    private Path findPath(String url) {
        try {
            if (url.equals("/")) {
                URL url1 = getClass().getClassLoader().getResource(DEFAULT_DIRECTORY_NAME + "/index.html");
                return Path.of(Objects.requireNonNull(url1).toURI());
            }

            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?")) + ".html";
            }

            URL foundUrl = getClass().getClassLoader().getResource(DEFAULT_DIRECTORY_NAME + url);
            return Path.of(Objects.requireNonNull(foundUrl).toURI());
        } catch (URISyntaxException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String findResponseContentType(String url) {
        Path path = findPath(url);
        return "text/" + path.toString().split("\\.")[1];
    }

    private String findResponseContentType2(Map<String, String> requestHeaders) {
        return requestHeaders.get("Accept").split(",")[0];
    }

    private boolean isLogin(String uri) {
        return uri.contains("login");
    }

    private void checkLogin(String uri) {
        int index = uri.indexOf("?");
        String queryString = uri.substring(index + 1);

        String[] elements = queryString.split("&");
        Map<String, String> map = new HashMap<>();

        for (String element : elements) {
            String[] k = element.split("=");
            map.put(k[0], k[1]);
        }
        String account = map.get("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        System.out.println("user.getAccount() = " + user.getAccount());
        log.info("user : {}", user);
    }
}
