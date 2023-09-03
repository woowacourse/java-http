package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpMethodNotAllowedException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.security.Cookie;
import org.apache.coyote.http11.security.Session;
import org.apache.coyote.http11.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        HttpMethod httpMethod = httpRequest.getHttpMethod();

        if (httpMethod == HttpMethod.GET) {
            Cookie cookie = Cookie.from(httpRequest.getHeader(HttpHeaderName.COOKIE.getValue()));
            String sessionId = cookie.getCookieValue("JSESSIONID");
            if (sessionId != null) {
                return getIndexPageRedirectResponse(httpRequest);
            }

            String requestUri = httpRequest.getRequestUri();
            if (requestUri.startsWith("/login?")) {
                return getQueryParamResponse(httpRequest);
            }

            return getLoginPageResponse(httpRequest);
        }

        if (httpMethod == HttpMethod.POST) {
            return getLoginRedirectResponse(httpRequest);
        }
        throw new HttpMethodNotAllowedException("허용되지 않는 HTTP Method입니다.");
    }

    private HttpResponse getLoginRedirectResponse(final HttpRequest httpRequest) {
        ResponseHeaders responseHeaders = new ResponseHeaders();
        StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.FOUND);
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());

        Map<String, Object> formDataMap = httpRequest.getMessageBody().getFormData();
        String account = (String) formDataMap.get("account");
        String password = (String) formDataMap.get("password");

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                log.info("user : {}", user);
                return getLoginSuccessResponse(httpRequest, responseHeaders, statusLine, user);
            } else {
                return getLoginFailResponse(responseHeaders, statusLine);
            }
        }
        return getLoginFailResponse(responseHeaders, statusLine);
    }

    private HttpResponse getLoginSuccessResponse(final HttpRequest httpRequest, final ResponseHeaders responseHeaders,
                                         final StatusLine statusLine, final User user) {
        responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/index.html");

        String cookieValue = httpRequest.getHeader(HttpHeaderName.COOKIE.getValue());
        Cookie cookie = Cookie.from(cookieValue);
        if (cookie.hasNotKey("JSESSIONID")) {
            Session session = createSession(responseHeaders, httpRequest, user);
            log.info("create sessionId : {}", session.getId());
        }

        return new HttpResponse(statusLine, responseHeaders, MessageBody.from(null));
    }

    private HttpResponse getLoginFailResponse(final ResponseHeaders responseHeaders, final StatusLine statusLine) {
        responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/401.html");
        return new HttpResponse(statusLine, responseHeaders, MessageBody.from(null));
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

    private HttpResponse getIndexPageRedirectResponse(final HttpRequest httpRequest) {
        StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.FOUND);
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/index.html");
        MessageBody messageBody = MessageBody.from("");
        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }

    private Session createSession(final ResponseHeaders responseHeaders, final HttpRequest httpRequest, final User user) {
        UUID sessionId = UUID.randomUUID();
        responseHeaders.addHeader(HttpHeaderName.SET_COOKIE.getValue(), "JSESSIONID=" + sessionId);
        Session session = httpRequest.getSession(true);
        session.setAttribute("user", user);
        return session;
    }
}
