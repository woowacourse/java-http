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
    private static final String QUERY_PARAM_DELIMITER = "?";
    private static final String QUERY_PARAM_AND_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";

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

            final Map<String, String> request = parseUrl(bufferedReader.readLine());

            printLoginUser(request.get("path"), request.get("params"));

            final String responseBody = getResponseBody(request.get("path"));
            final String response = createResponse(request.get("contentType"), responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseUrl(String url) throws IOException {
        url = url.split(URL_START_REGEX)[URL_INDEX];
        Map<String, String> request = new HashMap<>();
        request.put("contentType", ContentType.from(url).getValue());
        request.put("path", getPath(url));
        request.put("params", getParams(url));
        return request;
    }

    private String getPath(final String url) {
        if (url.contains(QUERY_PARAM_DELIMITER)) {
            return url.substring(0, url.indexOf(QUERY_PARAM_DELIMITER)) + ContentType.getDefaultExtension();
        }
        return url;
    }

    private String getParams(final String url) {
        if (url.contains(QUERY_PARAM_DELIMITER)) {
            return url.substring(url.indexOf(QUERY_PARAM_DELIMITER) + 1);
        }
        return "";
    }

    private void printLoginUser(final String path, final String allOfQueryParam) {
        if (path.contains("login")) {
            final Map<String, String> params = new HashMap<>();
            for (String queryParameter : allOfQueryParam.split(QUERY_PARAM_AND_DELIMITER)) {
                final String[] param = queryParameter.split(QUERY_PARAM_VALUE_DELIMITER);
                params.put(param[0], param[1]);
            }
            final User user = InMemoryUserRepository.findByAccount(params.get("account"))
                    .orElseThrow(UserNotFoundException::new);
            if (user.checkPassword(params.get("password"))) {
                log.info(String.format("user : %s", user));
            }
        }
    }

    private String getResponseBody(final String path) throws IOException {
        if (path.equals(DEFAULT_URL)) {
            return DEFAULT_RESPONSE_BODY;
        }
        return readFile(path);
    }

    private String readFile(final String path) throws IOException {
        final String filePath = String.format("static%s", path);
        final URL resource = this.getClass().getClassLoader().getResource(filePath);
        return Files.readString(Path.of(Objects.requireNonNull(resource).getPath()));
    }

    private String createResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
