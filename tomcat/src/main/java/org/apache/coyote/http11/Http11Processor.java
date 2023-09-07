package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String JSESSIONID = "JSESSIONID";

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
            final HttpRequest httpRequest = parseHttpRequest(bufferedReader);
            if (httpRequest == null) {
                return;
            }
            final HttpResponse response = handleRequest(httpRequest);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestStartLine startLine = extractStartLine(bufferedReader);
        if (startLine == null) {
            return null;
        }
        final HttpRequestHeader httpRequestHeaders = extractHeader(bufferedReader);
        final HttpRequestBody httpRequestBody = extractBody(httpRequestHeaders.getContentLength(), bufferedReader);
        return new HttpRequest(startLine, httpRequestHeaders, httpRequestBody);
    }

    private HttpRequestStartLine extractStartLine(final BufferedReader bufferedReader) throws IOException {
        return HttpRequestStartLine.from(bufferedReader.readLine());
    }

    private HttpRequestHeader extractHeader(final BufferedReader bufferedReader)
            throws IOException {
        Map<String, String> parsedHeaders = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            String[] headerTokens = line.split(": ");
            parsedHeaders.put(headerTokens[0], headerTokens[1]);
            line = bufferedReader.readLine();
        }
        return new HttpRequestHeader(parsedHeaders);
    }

    private HttpRequestBody extractBody(String contentLength, BufferedReader bufferedReader)
            throws IOException {
        if (contentLength == null) {
            return null;
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new HttpRequestBody(new String(buffer));
    }

    private HttpResponse handleRequest(final HttpRequest request)
            throws URISyntaxException, IOException {
        try {
            if (request.isPOST() && request.isSamePath("/register")) {
                return handlePostRegister(request);
            }
            if (request.isPOST() && request.isSamePath("/login")) {
                return handlePostLogin(request);
            }
            if (request.isGET() && request.isSamePath("/register")) {
                return handleGetRegister(request);
            }
            if (request.isGET() && request.isSamePath("/login")) {
                return handleGetLogin(request);
            }
            if (request.isGET() && request.isSamePath("/")) {
                String responseBody = "Hello world!";

                HttpResponseHeader responseHeader = new HttpResponseHeader(
                        getContentType(request.getAccept(), request.getPath()),
                        String.valueOf(responseBody.getBytes().length), null, null);
                return HttpResponse.of(HttpResponseStatus.OK, responseHeader, responseBody);
            }
            return handleDefault(request).orElse(handle404());
        } catch (IllegalArgumentException exception) {
            return handle500();
        }
    }

    private HttpResponse handlePostRegister(final HttpRequest request) {
        if (request.isNotExistBody()) {
            throw new IllegalArgumentException("로그인 정보가 입력되지 않았습니다.");
        }
        Map<String, String> parsedRequestBody = parseRequestBody(request);
        InMemoryUserRepository.save(new User(
                Long.getLong(parsedRequestBody.get("id")),
                parsedRequestBody.get("account"),
                parsedRequestBody.get("password"),
                parsedRequestBody.get("email")
        ));
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(0), "/index.html", null);
        return HttpResponse.of(HttpResponseStatus.FOUND, responseHeader, null);
    }

    private HttpResponse handlePostLogin(final HttpRequest request)
            throws URISyntaxException, IOException {
        if (request.isNotExistBody()) {
            throw new IllegalArgumentException("로그인 정보가 입력되지 않았습니다.");
        }
        final HttpCookie cookie = request.getCookie();
        Map<String, String> parsedRequestBody = parseRequestBody(request);
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(
                parsedRequestBody.get("account"));
        if (userOptional.isPresent()
                && userOptional.get().checkPassword(parsedRequestBody.get("password"))) {
            String setCookie = null;
            if (!cookie.isExist(JSESSIONID)) {
                String jSessionId = String.valueOf(UUID.randomUUID());
                setCookie = JSESSIONID + "=" + jSessionId;
                SessionManager.InstanceOf().addLoginSession(jSessionId, userOptional.get());
            }
            HttpResponseHeader responseHeader = new HttpResponseHeader(
                    getContentType(request.getAccept(), request.getPath()),
                    String.valueOf(0), "/index.html", setCookie);
            return HttpResponse.of(HttpResponseStatus.FOUND, responseHeader, null);
        }
        return handle401(request);
    }

    private HttpResponse handle401(HttpRequest request) throws URISyntaxException, IOException {
        String responseBody = getHtmlFile(getClass().getResource("/static/401.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.UNAUTHORIZATION, responseHeader, responseBody);
    }

    private Map<String, String> parseRequestBody(HttpRequest request) {
        Map<String, String> parsedRequestBody = new HashMap<>();
        String[] queryTokens = request.getBody().split("&");
        for (String queryToken : queryTokens) {
            int equalSeparatorIndex = queryToken.indexOf("=");
            if (equalSeparatorIndex != -1) {
                parsedRequestBody.put(queryToken.substring(0, equalSeparatorIndex),
                        queryToken.substring(equalSeparatorIndex + 1));

            }
        }
        return parsedRequestBody;
    }

    private HttpResponse handleGetRegister(final HttpRequest request)
            throws URISyntaxException, IOException {

        URL filePathUrl = getClass().getResource("/static/register.html");
        String responseBody = getHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.OK, responseHeader, responseBody);
    }


    private HttpResponse handleGetLogin(final HttpRequest request)
            throws URISyntaxException, IOException {
        final HttpCookie cookie = request.getCookie();
        URL filePathUrl;
        if (isLogin(cookie)) {
            filePathUrl = getClass().getResource("/static/index.html");
        } else {
            filePathUrl = getClass().getResource("/static/login.html");
        }
        String responseBody = getHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.OK, responseHeader, responseBody);
    }

    private boolean isLogin(HttpCookie cookie) {
        return cookie.isExist(JSESSIONID)
                && SessionManager.InstanceOf().findSession(cookie.findCookie(JSESSIONID)) != null;
    }

    private Optional<HttpResponse> handleDefault(final HttpRequest request)
            throws URISyntaxException, IOException {
        URL filePathUrl = getClass().getResource("/static" + request.getPath());
        if (filePathUrl == null) {
            return Optional.empty();
        }
        String responseBody = getHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return Optional.of(HttpResponse.of(HttpResponseStatus.OK, responseHeader, responseBody));

    }

    private HttpResponse handle404() throws IOException, URISyntaxException {
        URL filePathUrl = getClass().getResource("/static/404.html");

        String responseBody = getHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8,
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.NOT_FOUND, responseHeader, responseBody);
    }

    private HttpResponse handle500() throws IOException, URISyntaxException {
        String responseBody = getHtmlFile(getClass().getResource("/static/500.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8,
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.INTERNAL_SERVER_ERROR, responseHeader, responseBody);
    }

    private String getHtmlFile(URL filePathUrl) throws URISyntaxException, IOException {
        final Path filePath = Paths.get(Objects.requireNonNull(filePathUrl).toURI());
        return new String(Files.readAllBytes(filePath));
    }

    private String getContentType(final String accept, final String uri) {
        final String[] tokens = uri.split("\\.");
        if ((tokens.length >= 1 && tokens[tokens.length - 1].equals("css")) || (accept != null && accept
                .contains("text/css"))) {
            return HttpResponseHeader.TEXT_CSS_CHARSET_UTF_8;
        }
        return HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8;
    }
}
