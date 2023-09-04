package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE_URI = "/login.html";
    private static final String UNAUTHORIZED_PAGE_URI = "/401.html";
    private static final String INDEX_PAGE_URI = "/index.html";

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String requestStartLine = bufferedReader.readLine();

            if (requestStartLine == null) {
                return;
            }

            HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(requestStartLine);
            HttpRequestHeader httpRequestHeader = parseRequestHeader(bufferedReader);
            HttpRequestBody httpRequestBody = parseRequestBody(httpRequestHeader.contentLength(), bufferedReader);

            ResponseEntity responseEntity = createResponse(httpRequestStartLine, httpRequestHeader, httpRequestBody);
            String response = HttpResponse.from(responseEntity).getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestBody parseRequestBody(String contentLength, BufferedReader bufferedReader) throws IOException {
        if (contentLength == null) {
            return HttpRequestBody.none();
        }
        int length = Integer.parseInt(contentLength);
        char[] httpRequestBody = new char[length];
        bufferedReader.read(httpRequestBody, 0, length);
        return HttpRequestBody.from(new String(httpRequestBody));
    }

    private HttpRequestHeader parseRequestHeader(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            stringBuilder.append(line).append("\r\n");
            line = bufferedReader.readLine();
        }
        return HttpRequestHeader.from(stringBuilder.toString());
    }

    private ResponseEntity createResponse(
            HttpRequestStartLine httpRequestStartLine,
            HttpRequestHeader httpRequestHeader,
            HttpRequestBody httpRequestBody
    ) throws IOException {
        String requestURI = httpRequestStartLine.getRequestURI();
        log.info("REQUEST URI: {}", requestURI);

        if (requestURI.equals("/")) {
            final var responseBody = "Hello world!";
            return ResponseEntity.of(HttpStatus.OK, requestURI, responseBody);
        }

        if (requestURI.equals("/login")) {
            return login(httpRequestStartLine, httpRequestHeader, httpRequestBody);
        }

        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + requestURI);
        File file = new File(resource.getFile());
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        return ResponseEntity.of(HttpStatus.OK, requestURI, responseBody);
    }

    private ResponseEntity login(HttpRequestStartLine httpRequestStartLine, HttpRequestHeader httpRequestHeader, HttpRequestBody httpRequestBody) throws IOException {
        HttpMethod httpMethod = httpRequestStartLine.getHttpMethod();
        String requestURI = httpRequestStartLine.getRequestURI();
        if (httpMethod == HttpMethod.GET) {
            return ResponseEntity.builder()
                    .httpStatus(HttpStatus.OK)
                    .requestURI(requestURI)
                    .location(LOGIN_PAGE_URI)
                    .build();
        }
        Map<String, String> queryStrings = parseQueryString(requestURI);
        User account = findAccount(queryStrings);

        String password = queryStrings.get("password");
        boolean isCorrectPassword = account.checkPassword(password);
        if (!isCorrectPassword) {
            log.info("account {} 비밀번호 불일치로 로그인 실패", account.getAccount());
            return ResponseEntity
                    .builder()
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .requestURI(requestURI)
                    .location(UNAUTHORIZED_PAGE_URI)
                    .build();
        }

        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.FOUND)
                .requestURI(requestURI)
                .location(INDEX_PAGE_URI)
                .build();
    }

    private User findAccount(Map<String, String> queryStrings) {
        String account = queryStrings.get("account");
        Optional<User> findAccount = InMemoryUserRepository.findByAccount(account);
        if (findAccount.isEmpty()) {
            log.info("해당하는 Account를 찾을 수 없음! account={}", account);
            throw new NoSuchElementException();
        }

        return findAccount.get();
    }

    private Map<String, String> parseQueryString(String requestURI) {
        int questionIndex = requestURI.indexOf("?");
        if (questionIndex == -1) {
            return Map.of();
        }

        String nonParsedQueryString = requestURI.substring(questionIndex + 1);
        return Arrays.stream(nonParsedQueryString.split("&"))
                .map(it -> Arrays.stream(it.split("=")).collect(Collectors.toList()))
                .collect(Collectors.toMap(it -> it.get(0), it -> it.get(1)));
    }
}
