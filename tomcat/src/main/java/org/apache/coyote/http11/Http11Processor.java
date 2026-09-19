package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String[] requestLineParts = bufferedReader.readLine().split(" ");
            final var response = getResponse(requestLineParts[1]);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }


    private String getResponse(String requestUri) throws IOException {
        if (requestUri.contains("/login?")) {
            Map<String, String> queryMap = getQuerySeparate(requestUri);
            String account = queryMap.get("account");
            String password = queryMap.get("password");
            Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
            if (foundUser.isEmpty()) {
                return getRedirectResponse("/401.html", getContentType(requestUri));
            }

            User user = foundUser.get();
            log.info(user.toString());

            if (user.checkPassword(password)) {
                return getRedirectResponse("/index.html", getContentType(requestUri));
            }
            return getRedirectResponse("/401.html", getContentType(requestUri));
        }

        if (!requestUri.equals("/")) {
            String paths = getStaticResource(requestUri);
            if (paths != null) {
                return getOkResponse(getContentType(requestUri), paths);
            }
        }
        return getOkResponse(getContentType(requestUri), "Hello world!");
    }

    private String getRedirectResponse(String location, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                contentType,
                "Content-Length: " + 0,
                "",
                "");
    }

    private String getOkResponse(String contentType, String body) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK",
                contentType,
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private Map<String, String> getQuerySeparate(String requestUri) {
        Map<String, String> queryMap = new HashMap<>();
        int index = requestUri.indexOf("?");
        String queryString = requestUri.substring(index + 1);
        String[] queryParameters = queryString.split("&");
        for (String parameter : queryParameters) {
            String[] queryParameter = parameter.split("=", -1);
            queryMap.put(queryParameter[0], queryParameter[1]);
        }
        return queryMap;
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "Content-Type: text/javascript;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    @Nullable
    private String getStaticResource(String requestUri) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUri);
        if (url == null) {
            requestUri = requestUri + ".html";
            url = getClass().getClassLoader().getResource("static" + requestUri);
        }
        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
