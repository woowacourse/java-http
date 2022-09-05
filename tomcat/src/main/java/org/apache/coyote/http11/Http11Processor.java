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
import org.apache.coyote.Processor;
import org.apache.coyote.QueryParams;
import org.apache.coyote.exception.InvalidLoginFomratException;
import org.apache.coyote.exception.InvalidPasswordException;
import org.apache.coyote.exception.MemberNotFoundException;
import org.apache.coyote.support.ResourcesUtil;
import org.apache.coyote.support.UriParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LANDING_PAGE_URL = "/";
    private static final String STATIC_PATH = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String LOGIN_PATH = "/login.html";

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

            String uri = UriParser.parseUri(bufferedReader);

            String response = accessUri(uri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String accessUri(final String uri) throws IOException {
        if (uri.equals(LANDING_PAGE_URL)) {
            return toOkResponse(ContentType.TEXT_HTML, "Hello world!");
        }
        String url = UriParser.parseUrl(uri);
        String queryString = UriParser.parseQueryString(uri);
        url = addExtension(url);
        return accessUrl(url, QueryParams.parseQueryParams(queryString));
    }

    private String addExtension(String url) {
        String extension = UriParser.parseExtension(url);
        if (extension.isBlank()) {
            url = url + DEFAULT_EXTENSION;
        }
        return url;
    }

    private String accessUrl(final String url, final QueryParams queryParams) throws IOException {
        ContentType contentType = UriParser.parseContentType(url);
        if (url.equals(LANDING_PAGE_URL)) {
            return toOkResponse(contentType, "Hello world!");
        }
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
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        if (account == null || password == null) {
            throw new InvalidLoginFomratException();
        }
        User user = findUser(account);
        checkPassword(password, user);
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html "
        );
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
}
