package org.apache.coyote.httpresponse.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.CookieHeader;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.QueryString;
import org.apache.coyote.httprequest.RequestMethod;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;
import org.apache.coyote.httpresponse.handler.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private static final String SESSION_ATTRIBUTE_OF_USER = "user";
    private static final String USER_ACCOUNT = "account";
    private static final String USER_PASSWORD = "password";
    private static final String REDIRECT_URL = "/index.html";

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final RequestMethod requestMethod = request.getRequestMethod();
        try {
            if (requestMethod == RequestMethod.POST) {
                return handlePost(request);
            }
            if (requestMethod == RequestMethod.GET) {
                return handleGet(request);
            }
        } catch (UnauthorizedException e) {
            log.debug("로그인 실패 : {}", e.getMessage());
            return new UnAuthorizedHandler().handle(request);
        }
        return new MethodNotAllowedHandler().handle(request);
    }

    private HttpResponse handlePost(final HttpRequest request) {
        final String resourcePath = request.getPath() + ".html";
        final User user = getUser(QueryString.from(request.getRequestBody().getContents()));
        return HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.CREATED)
                .setContent(resourcePath)
                .setLocationHeader(REDIRECT_URL)
                .setCookieHeader(createCookie(request, user));
    }

    private User getUser(final QueryString queryString) {
        final String account = queryString.getValue(USER_ACCOUNT);
        final String password = queryString.getValue(USER_PASSWORD);
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 유저입니다."));
        if (user.checkPassword(password)) {
            log.info("user : {}", user);
            return user;
        }
        throw new UnauthorizedException("비밀번호가 잘못되었습니다.");
    }

    private CookieHeader createCookie(final HttpRequest request, final User user) {
        final Session session = request.getSession(true);
        session.setAttribute(SESSION_ATTRIBUTE_OF_USER, user);
        return CookieHeader.createByJSessionId(session.getId());
    }

    private HttpResponse handleGet(final HttpRequest request) {
        final String resourcePath = request.getPath() + ".html";
        final HttpResponse response = HttpResponse
                .init(request.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .setContent(resourcePath);
        if (checkLoginUser(request)) {
            return response
                    .setLocationHeader(REDIRECT_URL);
        }
        if (request.hasQueryString()) {
            final User user = getUser(request.getQueryString());
            return response
                    .setLocationHeader(REDIRECT_URL)
                    .setCookieHeader(createCookie(request, user));
        }
        return response;
    }

    private boolean checkLoginUser(final HttpRequest request) {
        if (request.hasJSessionId()) {
            final User sessionUser = (User) request.getSession(true).getAttribute(SESSION_ATTRIBUTE_OF_USER);
            if (sessionUser == null) {
                return false;
            }
            return InMemoryUserRepository.findByAccount(sessionUser.getAccount())
                    .isPresent();
        }
        return false;
    }
}
