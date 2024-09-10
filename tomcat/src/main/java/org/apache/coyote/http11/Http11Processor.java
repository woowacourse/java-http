package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.HttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String[] firstLine = reader.readLine().split(" ");
            String method = firstLine[0];
            String url = firstLine[1];
            String version = firstLine[2];

            Map<String, String> requestHeaders = readRequestHeaders(reader);

            if (method.equals("GET")) {
                doGet(url, requestHeaders, outputStream);
            }
            if (method.equals("POST")) {
                doPost(url, requestHeaders, reader, outputStream);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            requestHeaders.put(line.split(": ")[0], line.split(": ")[1]);
        }
        return requestHeaders;
    }

    private void doGet(String url, Map<String, String> requestHeaders, OutputStream outputStream) throws IOException {
        if (url.equals("/")) {
            createHttpResponse(
                    "Hello world!",
                    createHttpResponseHeader("200 OK", "", "text/html", "Hello world!"),
                    outputStream);
            return;
        }
        String[] result = determineGetResponse(url, requestHeaders);
        Path path = getPath(result);
        var responseBody = Files.readString(path);
        String contentType = Files.probeContentType(path);

        String responseHeader = createHttpResponseHeader(result[0], result[2], contentType, responseBody);
        createHttpResponse(responseBody, responseHeader, outputStream);
    }

    private String[] determineGetResponse(String url, Map<String, String> requestHeaders) {
        String[] result = new String[3];
        result[0] = "200 OK";
        result[2] = "";
        if (url.endsWith("html") || url.endsWith("js") || url.endsWith("css")) {
            result[1] = "static" + url;
            return result;
        }
        result[1] = "static" + url + ".html";
        return result;
    }

    private Path getPath(String[] pathAndStatus) {
        URL resource = getClass().getClassLoader().getResource(pathAndStatus[1]);
        return new File(resource.getPath()).toPath();
    }

    private void doPost(String url, Map<String, String> requestHeaders, BufferedReader reader,
            OutputStream outputStream
    ) throws IOException {
        String[] requestBody = readRequestBody(requestHeaders, reader);
        String[] pathAndStatus = determinePostResponse(url, requestHeaders, requestBody);

        final Path path = getPath(pathAndStatus);
        var responseBody = Files.readString(path);
        String contentType = Files.probeContentType(path);

        String responseHeader = createHttpResponseHeader(pathAndStatus[0], pathAndStatus[2], contentType, responseBody);
        createHttpResponse(responseBody, responseHeader, outputStream);
    }

    private String[] determinePostResponse(String url, Map<String, String> requestHeaders, String[] requestBody) {
        String[] pathAndStatus = new String[3];
        if (url.startsWith("/login")) {
            pathAndStatus = doLogin(requestHeaders, requestBody);
        }
        if (url.equals("/register")) {
            pathAndStatus = doRegister(requestBody);
        }
        return pathAndStatus;
    }

    private String[] doLogin(Map<String, String> requestHeaders, String[] requestBody) {
        String[] result = new String[3];
        result[0] = "302 FOUND";
        result[1] = "static/index.html";

        String account = requestBody[0].split("=")[1];
        String password = requestBody[1].split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (!user.checkPassword(password)) {
            result[0] = "302 FOUND";
            result[1] = "static/401.html";
            result[2] = "Location: /401.html";
            return result;
        }

        log.info(user.toString());
        result[2] = checkCookie(requestHeaders);
        return result;
    }

    private String checkCookie(Map<String, String> requestHeaders) {
        String result = "Location: /index.html ";
        HttpCookie httpCookie = new HttpCookie(requestHeaders.get("Cookie"));
        if (!httpCookie.containsJSessionId()) {
            result = String.join("\r\n",
                    result,
                    "Set-Cookie: JSESSIONID=" + UUID.randomUUID().toString() + " ");
        }

        return result;
    }

    private String[] readRequestBody(Map<String, String> requestHeaders, BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer).split("&");
    }

    private String[] doRegister(String[] requestBody) {
        String[] result = new String[3];
        result[0] = "302 FOUND";
        result[1] = "static/index.html";
        result[2] = "Location: /index.html ";
        User user = createUser(requestBody);
        InMemoryUserRepository.save(user);

        return result;
    }

    private User createUser(String[] requestBody) {
        Map<String, String> userInfo = new HashMap<>();
        for (String param : requestBody) {
            userInfo.put(param.split("=")[0], param.split("=")[1]);
        }
        return new User(userInfo.get("account"), userInfo.get("password"), userInfo.get("email"));
    }

    private static String createHttpResponseHeader(String status, String additionalHeader, String contentType,
            String responseBody
    ) {
        String responseHeader = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ");
        if (!additionalHeader.isBlank()) {
            responseHeader = String.join("\r\n",
                    responseHeader,
                    additionalHeader);
        }
        return responseHeader;
    }

    private void createHttpResponse(String responseBody, String responseHeader, OutputStream outputStream)
            throws IOException {
        final var response = String.join("\r\n",
                responseHeader,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}