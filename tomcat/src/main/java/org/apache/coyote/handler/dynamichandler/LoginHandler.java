package org.apache.coyote.handler.dynamichandler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.*;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.Query;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler implements Handler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (HttpMethod.GET == httpRequest.httpMethod()) {
            return handleGetMapping(httpRequest);
        }
        if (HttpMethod.POST == httpRequest.httpMethod()) {
            return handlePostMapping(httpRequest);
        }
        return new ExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR).handle(httpRequest);
    }

    private HttpResponse handleGetMapping(HttpRequest httpRequest) {
        try {
            /*
                    httpRequest.session() returns null when Request does not contain Session Information.
                                          throws IllegalArgumentException when Request contains Session Information, but session is invalid.

                    HttpResponse.createStaticResponseByPath throws IOException when File is not Found because FilePath is not invalid.
             */
            Session session = httpRequest.session();
            if (session == null) {
                return HttpResponse.createStaticResponseByPath(
                        httpRequest.httpVersion(),
                        HttpStatus.OK,
                        DEFAULT_DIRECTORY_PATH + httpRequest.path() + ContentType.TEXT_HTML.extension());
            }
            return handleAlreadyLogin(httpRequest);
        } catch (IOException e) {
            return new ExceptionHandler(HttpStatus.NOT_FOUND).handle(httpRequest);
        } catch (IllegalArgumentException e) {
            StatusLine statusLine = new StatusLine(httpRequest.httpVersion(), HttpStatus.FOUND);

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(HttpHeader.LOCATION.value(), "/login.html");
            headers.put(HttpHeader.SET_COOKIE.value(), Session.JSESSIONID + "=; " + "Max-Age=0 ");

            return new HttpResponse(statusLine, new Header(headers));
        }
    }

    private HttpResponse handleAlreadyLogin(HttpRequest httpRequest) {
        Session session = httpRequest.session();
        User user = (User) session.getAttribute("user");

        StatusLine statusLine = new StatusLine(httpRequest.httpVersion(), HttpStatus.FOUND);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeader.LOCATION.value(), "/index.html");
        if (user == null) {
            session.invalidate();
            headers.put(HttpHeader.SET_COOKIE.value(), Session.JSESSIONID + "=; " + "Max-Age=0 ");
        }
        return new HttpResponse(statusLine, new Header(headers));
    }

    private HttpResponse handlePostMapping(HttpRequest httpRequest) {
        Query queries = Query.create(httpRequest.body());

        String account = queries.get(ACCOUNT);
        String password = queries.get(PASSWORD);
        if (account == null || password == null) {
            StatusLine statusLine = new StatusLine(httpRequest.httpVersion(), HttpStatus.FOUND);

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(HttpHeader.LOCATION.value(), "/login.html");

            return new HttpResponse(statusLine, new Header(headers));
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info(ACCOUNT + ": " + account + ", " + PASSWORD + ": " + password);
            return handleRedirectPage(httpRequest, user.get());
        }
        return handleUnauthorizedPage(httpRequest);
    }

    private HttpResponse handleUnauthorizedPage(HttpRequest httpRequest) {
        return new ExceptionHandler(HttpStatus.UNAUTHORIZED).handle(httpRequest);
    }

    private HttpResponse handleRedirectPage(HttpRequest httpRequest, User user) {
        StatusLine statusLine = new StatusLine(httpRequest.httpVersion(), HttpStatus.FOUND);

        Session session = Session.create(UUID.randomUUID().toString());
        session.setAttribute("user", user);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeader.LOCATION.value(), "/index.html");
        headers.put(HttpHeader.SET_COOKIE.value(), Session.JSESSIONID + "=" + session.getId());

        return new HttpResponse(statusLine, new Header(headers));
    }
}
