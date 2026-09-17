package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
            String requestLine = reader.readLine();

            if (requestLine == null) { // 클라이언트가 아무 요청도 보내지 않고 연결만 끊은 경우 처리. 파싱할 것 X
                return;
            }

            RequestTarget requestTarget = parseRequestTarget(requestLine);

            readHeaders(reader);
            logUserIfExists(requestTarget.queryString());

            URL resource = findResource(requestTarget.path());

            if (resource == null) { // 요청한 정적 파일을 못 찾은 경우 처리.
                return;
            }

            byte[] body = readBody(resource);
            String response = createResponse(requestTarget.path(), body);

            writeResponse(outputStream, response, body);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestTarget parseRequestTarget(String line) {
        String[] requestLine = line.split(" ");
        String uri = requestLine[1];

        String path = uri;
        String queryString = null;

        int queryIndex = uri.indexOf("?");

        if (queryIndex != -1) {
            path = uri.substring(0, queryIndex);
            queryString = uri.substring(queryIndex + 1);
        }

        return new RequestTarget(normalizePath(path), queryString);
    }

    private void readHeaders(BufferedReader reader) throws IOException {
        String header;
        while ((header = reader.readLine()) != null && !header.isEmpty()) {
            // Header는 현재 사용하지 않으므로 읽고 버린다.
        }
    }

    private void logUserIfExists(String queryString) {
        if (queryString == null) {
            return;
        }

        Map<String, String> queryParams = parseQueryString(queryString);
        String account = queryParams.get("account");

        InMemoryUserRepository.findByAccount(account) // 추후 UserService 생성
                .ifPresent(user ->
                        log.info("조회된 사용자: id={}, account={}",
                                user.getId(),
                                user.getAccount()
                        )
                );
    }

    private URL findResource(String path) {
        return getClass()
                .getClassLoader()
                .getResource("static" + path);
    }

    private byte[] readBody(URL resource) throws IOException {
        try (InputStream resourceStream = resource.openStream()) {
            return resourceStream.readAllBytes();
        }
    }

    private String createResponse(String path, byte[] body) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + getContentType(path) + " ",
                "Content-Length: " + body.length + " ",
                "",
                ""
        );
    }

    private void writeResponse(OutputStream outputStream, String response, byte[] body) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private String normalizePath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }

        if (!path.contains(".")) {
            return path + ".html";
        }

        return path;
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            String key = keyValue[0];
            String value = keyValue[1];

            queryParams.put(key, value);
        }

        return queryParams;
    }

    private record RequestTarget(String path, String queryString) {
    }

}
