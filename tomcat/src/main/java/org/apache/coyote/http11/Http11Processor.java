package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.Session;
import org.apache.coyote.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCE_PATH = "static";
    private static final int URL_INDEX = 1;
    private static final String ILLEGAL_REQUEST = "부적절한 요청입니다.";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    public static final String PAGE401 = "/401.html";
    public static final String HTTP_FOUND = "Found";
    public static final String PAGE_INDEX = "/index.html";
    private static final SessionManager sessionManager = SessionManager.getInstance();
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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader br = new BufferedReader(inputStreamReader)) {

            String requestLine = br.readLine();
            if (Objects.isNull(requestLine)) {
                throw new IllegalArgumentException(ILLEGAL_REQUEST);
            }
            log.info(requestLine);

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

    private ResponseInfo handleRequest(String requestLine, RequestHeader header, RequestBody body) {
        String requestUri = requestLine.split(" ")[URL_INDEX];

        if (requestUri.contains("/login")) {
            return handleLoginRequest(requestLine, header, body);
        }

        if (requestUri.contains("/register")) {
            return handleMemberRegistRequest(requestLine, body);
        }

        String resourcePath = RESOURCE_PATH + requestUri;
        return new ResponseInfo(getClass().getClassLoader().getResource(resourcePath), 200, "OK");
    }

    private ResponseInfo handleMemberRegistRequest(String requestLine, RequestBody body) {
        ClassLoader classLoader = getClass().getClassLoader();
        String requestMethod = requestLine.split(" ")[0];

        if (requestMethod.equals("POST")) {
            String account = body.getByKey(ACCOUNT);
            String password = body.getByKey(PASSWORD);
            String email = body.getByKey(EMAIL);
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            log.info("User create - {}", user);
            String resourcePath = RESOURCE_PATH + PAGE_INDEX;
            return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
        }

        String resourcePath = RESOURCE_PATH + "/register.html";
        return new ResponseInfo(classLoader.getResource(resourcePath), 200, "OK");
    }

    private ResponseInfo handleLoginRequest(String requestLine, RequestHeader header, RequestBody body) {
        ClassLoader classLoader = getClass().getClassLoader();
        String requestUri = requestLine.split(" ")[URL_INDEX];

        String httpMethod = requestLine.split(" ")[0];
        if (httpMethod.equals("POST")) {
            String account = body.getByKey(ACCOUNT);
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isEmpty()) {
                String resourcePath = RESOURCE_PATH + PAGE401;
                return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
            }
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            session.setAttribute("user", user.get());
            String resourcePath = RESOURCE_PATH + PAGE_INDEX;
            return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND, session.getId());
        }

        if (httpMethod.equals("GET")) {
            if (header.getByKey("Cookie") != null) {
                HttpCookie cookie = HttpCookie.from(header.getByKey("Cookie"));
                String jsessionid = cookie.getByKey("JSESSIONID");

                if (jsessionid != null) {
                    if (sessionManager.findSession(jsessionid) == null) {
                        throw new IllegalArgumentException("유효하지 않은 세션입니다.");
                    }

                    String resourcePath = RESOURCE_PATH + PAGE_INDEX;
                    return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND, jsessionid);
                }
            }

            int index = requestUri.indexOf("?");
            if (index == -1) {
                String resourcePath = RESOURCE_PATH + "/login.html";
                return new ResponseInfo(classLoader.getResource(resourcePath), 200, "OK");
            }
            String queryString = requestUri.substring(index + 1);
            String[] queryParams = queryString.split("&");
            String account = getParamValueWithKey(queryParams, ACCOUNT);
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isEmpty()) {
                String resourcePath = RESOURCE_PATH + PAGE401;
                return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
            }

            if (user.get().checkPassword(getParamValueWithKey(queryParams, PASSWORD))) {
                log.info("User: {}", user);

                String resourcePath = RESOURCE_PATH + PAGE_INDEX;
                return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
            }

        }


        String resourcePath = RESOURCE_PATH + PAGE401;
        return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
    }

    private String getParamValueWithKey(String[] queryParams, String key) {
        for (String param : queryParams) {
            String[] paramKeyValue = param.split("=");
            if (paramKeyValue[0].equals(key)) {
                return paramKeyValue[1];
            }
        }
        return "";
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
