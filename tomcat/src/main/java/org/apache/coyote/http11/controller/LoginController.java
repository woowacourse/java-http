package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.security.Cookie;
import org.apache.coyote.http11.security.Session;
import org.apache.coyote.http11.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        Cookie cookie = Cookie.from(request.getHeader(HttpHeaderName.COOKIE.getValue()));
        String sessionId = cookie.getCookieValue("JSESSIONID");
        if (sessionId != null) {
            if (request.getSession(false) == null) {
                throw new HttpRequestException.NotMatchSession();
            }

            return getIndexPageRedirectResponse(request);
        }

        if (request.isParamRequest()) {
            return getQueryParamResponse(request);
        }

        return getLoginPageResponse(request);
    }

    private HttpResponse getIndexPageRedirectResponse(final HttpRequest httpRequest) {
        StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.FOUND);
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/index.html");
        MessageBody messageBody = MessageBody.empty();
        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }

    private HttpResponse getQueryParamResponse(final HttpRequest httpRequest) throws IOException {
        Map<String, Object> queryParams = httpRequest.getQueryParams();
        String account = (String) queryParams.get("account");
        StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.FOUND);
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        String responseBody = FileReader.read("/login.html");
        MessageBody messageBody = MessageBody.from(responseBody);
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("user : {}", user);
        }
        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }

    private HttpResponse getLoginPageResponse(final HttpRequest httpRequest) throws IOException {
        StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.OK);

        String responseBody = FileReader.read("/login.html");
        MessageBody messageBody = MessageBody.from(responseBody);

        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        responseHeaders.addHeader(HttpHeaderName.CONTENT_LENGTH.getValue(), String.valueOf(messageBody.getBodyLength()));

        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }


    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {


        Map<String, Object> formDataMap = request.getMessageBody().getFormData();
        String account = (String) formDataMap.get("account");
        String password = (String) formDataMap.get("password");

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                log.info("user : {}", user);
                return getLoginSuccessResponse(request, user);
            } else {
                return getLoginFailResponse(request);
            }
        }
        return getLoginFailResponse(request);
    }

    private HttpResponse getLoginSuccessResponse(final HttpRequest request, final User user) {
        ResponseHeaders responseHeaders = new ResponseHeaders();
        StatusLine statusLine = new StatusLine(request.getHttpVersion(), Status.FOUND);
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/index.html");

        String cookieValue = request.getHeader(HttpHeaderName.COOKIE.getValue());
        Cookie cookie = Cookie.from(cookieValue);
        if (cookie.hasNotKey("JSESSIONID")) {
            Session session = createSession(responseHeaders, request, user);
            log.info("create sessionId : {}", session.getId());
        }

        return new HttpResponse(statusLine, responseHeaders, MessageBody.empty());
    }

    private HttpResponse getLoginFailResponse(final HttpRequest request) {
        ResponseHeaders responseHeaders = new ResponseHeaders();
        StatusLine statusLine = new StatusLine(request.getHttpVersion(), Status.FOUND);
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/401.html");
        return new HttpResponse(statusLine, responseHeaders, MessageBody.empty());
    }

    private Session createSession(final ResponseHeaders responseHeaders, final HttpRequest httpRequest, final User user) {
        Session session = httpRequest.getSession(true);
        responseHeaders.addHeader(HttpHeaderName.SET_COOKIE.getValue(), "JSESSIONID=" + session.getId());
        session.setAttribute("user", user);
        return session;
    }
}
