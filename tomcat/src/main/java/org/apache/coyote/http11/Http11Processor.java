package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.ContentType;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.Processor;
import org.apache.coyote.QueryParams;
import org.apache.coyote.exception.InvalidLoginFormatException;
import org.apache.coyote.exception.InvalidPasswordException;
import org.apache.coyote.exception.MemberNotFoundException;
import org.apache.coyote.exception.ResourceNotFoundException;
import org.apache.coyote.support.HttpRequestParser;
import org.apache.coyote.support.ResourcesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LANDING_PAGE_URL = "/";
    private static final String STATIC_PATH = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String LOGIN_PATH = "/login.html";
    public static final String UNAUTHORIZED_PATH = "/401.html";
    public static final String NOT_FOUND_PATH = "/404.html";
    public static final String INTERNAL_SERVER_ERROR_PATH = "/500.html";
    public static final String DEFAULT_RESPONSE_BODY = "Hello world!";

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String startLine = bufferedReader.readLine();
            HttpMethod httpMethod = HttpRequestParser.parseHttpMethod(startLine);
            String uri = HttpRequestParser.parseUri(startLine);

            String response = respond(uri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String respond(final String uri) {
        try {
            return accessUri(uri);
        } catch (InvalidLoginFormatException | InvalidPasswordException | MemberNotFoundException e) {
            log.error(e.getMessage(), e);
            return toFoundResponse(UNAUTHORIZED_PATH);
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage(), e);
            return toFoundResponse(NOT_FOUND_PATH);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return toFoundResponse(INTERNAL_SERVER_ERROR_PATH);
        }
    }

    private String accessUri(final String uri) throws IOException {
        if (uri.equals(LANDING_PAGE_URL)) {
            return toOkResponse(ContentType.TEXT_HTML, DEFAULT_RESPONSE_BODY);
        }
        String url = HttpRequestParser.parseUrl(uri);
        String queryString = HttpRequestParser.parseQueryString(uri);
        url = addExtension(url);
        return accessUrl(url, QueryParams.parseQueryParams(queryString));
    }

    private String addExtension(String url) {
        String extension = HttpRequestParser.parseExtension(url);
        if (extension.isBlank()) {
            url = url + DEFAULT_EXTENSION;
        }
        return url;
    }

    private String accessUrl(final String url, final QueryParams queryParams) throws IOException {
        ContentType contentType = HttpRequestParser.parseContentType(url);
        if (url.equals(LOGIN_PATH)) {
            return renderLogin(url, queryParams);
        }
        String responseBody = ResourcesUtil.readResource(STATIC_PATH + url);
        return toOkResponse(contentType, responseBody);
    }

    private String renderLogin(final String url, final QueryParams queryParams) throws IOException {
        if (queryParams.exists()) {
            return login(queryParams);
        }
        String responseBody = ResourcesUtil.readResource(STATIC_PATH + url);
        return toOkResponse(ContentType.TEXT_HTML, responseBody);
    }

    private String login(final QueryParams queryParams) {
        validateLogin(queryParams);
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html "
        );
    }

    private void validateLogin(final QueryParams queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        validateLoginFormat(account, password);
        User user = findUser(account);
        checkPassword(password, user);
    }

    private void validateLoginFormat(final String account, final String password) {
        if (account == null || password == null) {
            throw new InvalidLoginFormatException();
        }
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void checkPassword(final String password, final User user) {
        if (!user.checkPassword(password)) {
            throw new InvalidPasswordException();
        }
    }

    private String toOkResponse(final ContentType contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String toFoundResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                ""
        );
    }
}
