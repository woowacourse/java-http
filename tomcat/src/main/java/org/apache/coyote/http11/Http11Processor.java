package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String URL_START_REGEX = " ";
    private static final int URL_INDEX = 1;
    private static final String DEFAULT_URL = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String url = parseUrl(bufferedReader.readLine());

            printLoginUser(url);

            final String responseBody = getResponseBody(url);
            final String response = createResponse(ContentType.from(url), responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUrl(final String request) throws IOException {
        final String url = request.split(URL_START_REGEX)[URL_INDEX];
        if (request.contains("?")) {
            return url.substring(0, url.indexOf("?")) + ".html";
        }
        return request.split(URL_START_REGEX)[URL_INDEX];
    }

    private void printLoginUser(final String url) {
        if (url.contains("login?")) {
            final String allOfQueryParam = url.substring(url.indexOf("?") + 1);
            final Map<String, String> params = new HashMap<>();
            for (String queryParameter : allOfQueryParam.split("&")) {
                final String[] param = queryParameter.split("=");
                params.put(param[0], param[1]);
            }
            final User user = InMemoryUserRepository.findByAccount(params.get("account"))
                    .orElseThrow(UserNotFoundException::new);
            if (user.checkPassword(params.get("password"))) {
                log.info(String.format("user : %s", user));
            }
        }
    }

    private String getResponseBody(final String url) throws IOException {
        if (url.equals(DEFAULT_URL)) {
            return DEFAULT_RESPONSE_BODY;
        }
        return readFile(url);
    }

    private String readFile(final String url) throws IOException {
        final String filePath = "static" + url;
        final URL resource = this.getClass().getClassLoader().getResource(filePath);
        final String path = Objects.requireNonNull(resource).getPath();
        return Files.readString(Path.of(path));
    }

    private String createResponse(final ContentType contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue(),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
