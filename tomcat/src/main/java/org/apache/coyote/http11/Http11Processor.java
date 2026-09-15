package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequestHeader httpRequestHeader = makeHttpRequestHeader(reader);
            String requestUrl = httpRequestHeader.getRequestUrlWithOutQuery();

            handleLogin(requestUrl, httpRequestHeader);

            byte[] responseBody = getResponseBody(requestUrl);
            HttpResponseHeader responseHeader = HttpResponseHeader.createDefault(requestUrl, responseBody.length);

            outputStream.write(responseHeader.getResponseHeaderString().getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] getResponseBody(String requestUrl) throws IOException {
        URL resourceUrl = getClass().getClassLoader().getResource("static" + requestUrl);

        Path path = new File(Objects.requireNonNull(resourceUrl).getPath()).toPath();
        return Files.readAllBytes(path);
    }

    private void handleLogin(String requestUrl, HttpRequestHeader httpRequestHeader) {
        if (requestUrl.contains("login")) {
            QueryParams params = QueryParams.from(httpRequestHeader.getQuery());
            String account = params.getValue("account");
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            user.ifPresent(u -> {
                log.info(u.toString());
            });
        }
    }

    private HttpRequestHeader makeHttpRequestHeader(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        Map<String, String> headers = makeHeaders(reader);
        return new HttpRequestHeader(startLine, headers);
    }

    private Map<String, String> makeHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }
            String[] header = line.split(" ");
            headers.put(header[0], header[1]);
        }
        return headers;
    }
}
