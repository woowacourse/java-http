package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.LoginHandler;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String RESOURCE_PATH = "static";
    private static final String ILLEGAL_REQUEST = "부적절한 요청입니다.";
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String PAGE401 = "/401.html";
    public static final String HTTP_FOUND = "Found";
    public static final String INDEX_PAGE = "/index.html";
    private final Socket connection;
    private final LoginHandler loginHandler = new LoginHandler("/login");

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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader br = new BufferedReader(inputStreamReader)) {

            RequestLine requestLine = RequestLine.from(br.readLine());
            if (Objects.isNull(requestLine)) {
                throw new IllegalArgumentException(ILLEGAL_REQUEST);
            }
            RequestHeader requestHeader = readHeader(br);
            RequestBody requestBody = readBody(br, requestHeader);

            ResponseInfo responseInfo = handleRequest(requestLine, requestHeader, requestBody);

            String response = buildResponse(responseInfo);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseInfo handleRequest(RequestLine requestLine, RequestHeader header, RequestBody body) {
        String requestUri = requestLine.getRequestURI();

        if (requestUri.contains("/login")) {
            return handleLoginRequest(requestLine, header, body);
        }

        if (requestUri.contains("/register")) {
            return handleMemberRegistRequest(requestLine, body);
        }

        String resourcePath = RESOURCE_PATH + requestUri;
        return new ResponseInfo(getClass().getClassLoader().getResource(resourcePath), 200, "OK");
    }

    private ResponseInfo handleMemberRegistRequest(RequestLine requestLine, RequestBody body) {
        ClassLoader classLoader = getClass().getClassLoader();
        final String httpMethod = requestLine.getHttpMethod();

        if (httpMethod.equals("POST")) {
            String account = body.getByKey(ACCOUNT);
            String password = body.getByKey(PASSWORD);
            String email = body.getByKey(EMAIL);
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            log.info("User create - {}", user);
            String resourcePath = RESOURCE_PATH + INDEX_PAGE;
            return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
        }

        String resourcePath = RESOURCE_PATH + "/register.html";
        return new ResponseInfo(classLoader.getResource(resourcePath), 200, "OK");
    }

    private ResponseInfo handleLoginRequest(RequestLine requestLine, RequestHeader header, RequestBody body) {
        ClassLoader classLoader = getClass().getClassLoader();

        final String httpMethod = requestLine.getHttpMethod();
        if (httpMethod.equals("POST")) {
            return loginHandler.doPost(body);
        }

        if (httpMethod.equals("GET")) {
            return loginHandler.doGet(header);
        }

        String resourcePath = RESOURCE_PATH + PAGE401;
        return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
    }

    private String buildResponse(ResponseInfo responseInfo) throws IOException {
        File location = new File(responseInfo.getResource().getFile());
        String responseBody = "";

        if (location.isDirectory()) {
            responseBody = "Hello world!";
        }

        if (location.isFile()) {
            responseBody = new String(Files.readAllBytes(location.toPath()));
        }
        if (responseInfo.getCookie() != null) {
            return String.join(
                    "\r\n",
                    "HTTP/1.1 " + responseInfo.getHttpStatus() + " " + responseInfo.getStatusName(),
                    "Content-Type: " + contentType(responseInfo.getResource().getPath()) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "Set-Cookie: " + "JSESSIONID=" + responseInfo.getCookie() + " ",
                    "",
                    responseBody);
        }

        return String.join(
                "\r\n",
                "HTTP/1.1 " + responseInfo.getHttpStatus() + " " + responseInfo.getStatusName() + " \r\n" +
                        "Content-Type: " + contentType(responseInfo.getResource().getPath()) + ";charset=utf-8 \r\n" +
                        "Content-Length: " + responseBody.getBytes().length + " \r\n\r\n" + responseBody);

    }

    private String contentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    private RequestHeader readHeader(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            stringBuilder.append(line).append("\r\n");
        }
        return RequestHeader.from(stringBuilder.toString());
    }

    private RequestBody readBody(final BufferedReader bufferedReader, final RequestHeader requestHeader)
            throws IOException {
        final String contentLength = requestHeader.getByKey("Content-Length");
        if (contentLength == null) {
            return null;
        }
        final int length = Integer.parseInt(contentLength.trim());
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return RequestBody.from(new String(buffer));
    }
}
