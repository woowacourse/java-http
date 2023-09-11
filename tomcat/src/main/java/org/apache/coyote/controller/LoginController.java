package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.exception.UnauthorizedException;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.QueryString;
import org.apache.coyote.httprequest.RequestMethod;
import org.apache.coyote.httpresponse.CookieResponseHeader;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String SESSION_ATTRIBUTE_OF_USER = "user";
    private static final String USER_ACCOUNT = "account";
    private static final String USER_PASSWORD = "password";
    private static final String REDIRECT_URL = "/index.html";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final RequestMethod requestMethod = request.getRequestMethod();
        try {
            if (requestMethod == RequestMethod.POST) {
                doPost(request, response);
                return;
            }
            if (requestMethod == RequestMethod.GET) {
                doGet(request, response);
                return;
            }
        } catch (UnauthorizedException e) {
            log.debug("로그인 실패 : {}", e.getMessage());
            new UnAuthorizedController().service(request, response);
            return;
        }
        new MethodNotAllowedController().service(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String resourcePath = request.getPath() + ".html";
        final User user = getUser(QueryString.from(request.getRequestBody().getContents()));
        response.setHttpStatus(HttpStatus.CREATED);
        response.setContent(resourcePath);
        response.setLocationHeader(REDIRECT_URL);
        response.setCookieHeader(createCookie(request, user));
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String resourcePath = request.getPath() + ".html";
        response.setHttpStatus(HttpStatus.OK);
        response.setContent(resourcePath);
        if (checkLoginUser(request)) {
            response.setLocationHeader(REDIRECT_URL);
            return;
        }
        if (request.hasQueryString()) {
            final User user = getUser(request.getQueryString());
            response.setLocationHeader(REDIRECT_URL);
            response.setCookieHeader(createCookie(request, user));
        }
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

    private CookieResponseHeader createCookie(final HttpRequest request, final User user) {
        final Session session = request.getSession(true);
        session.setAttribute(SESSION_ATTRIBUTE_OF_USER, user);
        return CookieResponseHeader.createByJSessionId(session.getId());
    }

    private boolean checkLoginUser(final HttpRequest request) {
        if (request.hasJSessionId()) {
            final User sessionUser = (User) request.getSession(false).getAttribute(SESSION_ATTRIBUTE_OF_USER);
            if (sessionUser == null) {
                return false;
            }
            return InMemoryUserRepository.findByAccount(sessionUser.getAccount())
                    .isPresent();
        }
        return false;
    }
}
