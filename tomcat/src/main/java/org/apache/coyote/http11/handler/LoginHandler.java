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
import org.apache.coyote.http11.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getRequestLine().getHttpMethod() == HttpMethod.GET) {
            StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.OK);

            String responseBody = FileReader.read("/login.html");
            MessageBody messageBody = MessageBody.from(responseBody);

            ResponseHeaders responseHeaders = new ResponseHeaders();
            responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
            responseHeaders.addHeader(HttpHeaderName.CONTENT_LENGTH.getValue(), messageBody.getBodyLength());

            String requestUri = httpRequest.getRequestUri();
            if (isLoginTryUri(requestUri)) {
                Map<String, Object> queryParams = httpRequest.getQueryParams();
                String account = (String) queryParams.get("account");
                statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.FOUND);
                messageBody = MessageBody.from("");
                Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();

                    responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/index.html");

                    log.info("user : {}", user);
                }
            }

            return new HttpResponse(statusLine, responseHeaders, messageBody);
        }

        if (httpRequest.getRequestLine().getHttpMethod() == HttpMethod.POST) {
            ResponseHeaders responseHeaders = new ResponseHeaders();
            StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.FOUND);
            responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
            MessageBody messageBody = MessageBody.from(null);

            Map<String, Object> formDataMap = httpRequest.getMessageBody().getFormData();
            String account = (String) formDataMap.get("account");
            String password = (String) formDataMap.get("password");

            checkLoginWithUserInfo(responseHeaders, account, password);


            String cookieValue = (String) httpRequest.getRequestHeaders().getHeaderValue(HttpHeaderName.COOKIE.getValue());
            Cookie cookie = Cookie.from(cookieValue);
            if (cookie.hasNotKey("JSESSIONID")) {
                UUID sessionId = UUID.randomUUID();
                responseHeaders.addHeader(HttpHeaderName.SET_COOKIE.getValue(), "JSESSIONID=" + sessionId);
            }


            return new HttpResponse(statusLine, responseHeaders, messageBody);
        }
        throw new HttpMethodNotAllowedException("허용되지 않는 HTTP Method입니다.");
    }

    private void checkLoginWithUserInfo(final ResponseHeaders responseHeaders, final String account, final String password) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/index.html");
                log.info("user : {}", user);
            } else {
                responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/401.html");
            }
        } else {
            responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/401.html");
        }
    }

    private boolean isLoginTryUri(final String requestUri) {
        return requestUri.startsWith("/login?");
    }
}
