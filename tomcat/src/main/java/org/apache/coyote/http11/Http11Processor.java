package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

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

            final String uri = getUri(bufferedReader);
            final String path = getPath(uri);
            final Map<String, String> queries = getQueries(uri);
            final var responseBody = getResponseBody(path, queries);
            final String contentType = getContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUri(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine()
                .split(" ")[1];
    }

    private String getPath(final String path) {
        return path.split("\\?")[0];
    }

    private Map<String, String> getQueries(final String path) {
        final Map<String, String> queries = new HashMap<>();

        final int beginIndex = path.indexOf("?");
        if (beginIndex < 0) {
            return queries;
        }
        final String queryString = path.substring(beginIndex + 1);
        Arrays.stream(queryString.split("&"))
                .forEach(query -> {
                    final String[] entry = query.split("=");
                    queries.put(entry[0], entry[1]);
                });

        return queries;
    }

    private String getResponseBody(final String path, final Map<String, String> queries) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }

        final String fileName;
        if (path.equals(LOGIN_PAGE)) {
            loggingAccount(queries);
            fileName = LOGIN_PAGE + ".html";
        } else {
            fileName = path;
        }

        final String filePath = getClass().getClassLoader().getResource("static" + fileName).getPath();
        final FileInputStream fileInputStream = new FileInputStream(filePath);

        final String responseBody = new String(fileInputStream.readAllBytes());
        fileInputStream.close();

        return responseBody;
    }

    private void loggingAccount(final Map<String, String> queries) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(queries.get(ACCOUNT));
        if (user.isPresent() && user.get().checkPassword(queries.get(PASSWORD))) {
            log.info(user.get().toString());
        }
    }

    private String getContentType(final String path) throws IOException {
        final String contentType = Files.probeContentType(Path.of(path));
        if (contentType == null) {
            return DEFAULT_CONTENT_TYPE;
        }
        return contentType;
    }
}
