package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String REQUEST_HEADER_SUFFIX = "";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String SUCCESS_STATUS_CODE = "200 OK";
    private static final String HTML_CONTENT_TYPE = "text/html";
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
            // requestHeader 읽기
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<>();
            String line = br.readLine();
            if (line.isEmpty()) {
                return;
            }
            while (!REQUEST_HEADER_SUFFIX.equals(line)) {
                lines.add(line);
                line = br.readLine();
            }

            // requestHeader의 startLine 파싱
            final String[] startLine = lines.get(0).split(" ");
            if (startLine.length != 3) {
                return;
            }
            final String httpMethodName = startLine[0];
            final String requestURI = startLine[1];
            final String httpVersion = startLine[2];
            if (!HTTP_VERSION.equals(httpVersion)) {
                return;
            }

            // startLine의 httpMethod, requestURI 값에 따라 처리
            HttpMethod httpMethod = HttpMethod.findByName(httpMethodName);
            if (HttpMethod.GET.equals(httpMethod)) {
                Map<String, String> queryString = parseQueryString(requestURI);
                if (requestURI.contains("/login")) {
                    login(queryString.get("account"), queryString.get("password"));
                    createResponse(outputStream, SUCCESS_STATUS_CODE, HTML_CONTENT_TYPE, "static/login.html");
                    return;
                }
                if (requestURI.contains(".")) {
                    String type = "text/" + requestURI.split("\\.")[1];
                    String path = "static" + requestURI;
                    createResponse(outputStream, SUCCESS_STATUS_CODE, type, path);
                    return;
                }
                createResponse(outputStream, SUCCESS_STATUS_CODE, HTML_CONTENT_TYPE, requestURI);
                return;
            }
            if (HttpMethod.POST.equals(httpMethod)) {
                createResponse(outputStream, SUCCESS_STATUS_CODE, HTML_CONTENT_TYPE, requestURI);
            }
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void createResponse(OutputStream outputStream, String statusCode, String contentType, String path)
            throws IOException, URISyntaxException {
        final String responseBody = createResponseBody(path);
        final var response = String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String createResponseBody(String resourcePath) throws IOException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource != null) {
            final Path path = Paths.get(resource.toURI());
            return Files.readString(path);
        }
        return "Hello world!";
    }

    private Map<String, String> parseQueryString(String requestURI) {
        Map<String, String> queries = new HashMap<>();
        if (requestURI.contains("?")) {
            int index = requestURI.indexOf("?");
            String queryString = requestURI.substring(index + 1);
            for (String eachQueryString : queryString.split("&")) {
                String[] parsedEachQueryString = eachQueryString.split("=");
                queries.put(parsedEachQueryString[0], parsedEachQueryString[1]);
            }
        }
        return queries;
    }

    private void login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정 정보가 틀렸습니다."));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("계정 정보가 틀렸습니다.");
        }
        log.info("user: {}", user);
    }
}
