package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javassist.NotFoundException;
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
        try (connection;
             final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequestHeader httpRequestHeader = makeHttpRequestHeader(reader);
            String requestUrl = httpRequestHeader.getRequestUrlWithOutQuery();
            handleLogin(requestUrl, httpRequestHeader);

            String requestBody = getRequestBody(httpRequestHeader, reader);

            sendHttpResponse(requestUrl, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendHttpResponse(String requestUrl, OutputStream outputStream) throws IOException, NotFoundException {
        byte[] responseBody = getResponseBody(requestUrl);
        HttpResponseHeader responseHeader = HttpResponseHeader.createDefault(requestUrl, responseBody.length);
        outputStream.write(responseHeader.getResponseHeaderString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private String getRequestBody(HttpRequestHeader httpRequestHeader, BufferedReader reader) throws IOException {
        String body = "";
        if (httpRequestHeader.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequestHeader.getValue("Content-Length"));
            char[] bodyChars = new char[contentLength];
            int read = reader.read(bodyChars, 0, contentLength);
            body = new String(bodyChars, 0, read);
        }
        return body;
    }

    private byte[] getResponseBody(String requestUrl) throws IOException, NotFoundException {
        URL resourceUrl = getClass().getClassLoader().getResource("static" + requestUrl);
        if (resourceUrl == null) {
            throw new NotFoundException(requestUrl);
        }
        Path path = new File((resourceUrl).getPath()).toPath();
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
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            int colonIndex = line.indexOf(":");
            String key = line.substring(0, colonIndex).trim();
            String value = line.substring(colonIndex + 1).trim();
            headers.put(key, value);
        }
        return headers;
    }
}
