package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.exception.MemberAlreadyExistException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private static final String INDEX_HTML = "/index.html";
    private static final String LOGIN_HTML = "/login.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final RequestLine requestLine = httpRequest.getRequestLine();

        if (requestLine.getHttpMethod() == HttpMethod.GET && requestLine.getPath().startsWith("/login")) {
            if (isLoggedIn(httpRequest)) {
                final Session session = httpRequest.getSession(false);
                final User user = (User) session.getAttribute("user");
                log.info("user = {}", user);
                return redirect(INDEX_HTML);
            }
            return render(LOGIN_HTML);
        }

        if (requestLine.getHttpMethod() == HttpMethod.POST && requestLine.getPath().startsWith("/login")) {
            if (isLoggedIn(httpRequest)) {
                return redirect(INDEX_HTML);
            }
            final RequestBody requestBody = httpRequest.getRequestBody();
            final String account = requestBody.getParamValue("account");
            final String password = requestBody.getParamValue("password");
            final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

            if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
                final User user = userOptional.get();
                log.info("user = {}", user);
                final Session session = httpRequest.getSession(true);
                session.setAttribute("user", user);
                return redirectWithSession(INDEX_HTML, session.getId());
            }
            return redirect(UNAUTHORIZED_HTML);
        }

        if (requestLine.getHttpMethod() == HttpMethod.POST && requestLine.getPath().startsWith("/register")) {
            final RequestBody requestBody = httpRequest.getRequestBody();
            final String accountValue = requestBody.getParamValue("account");
            final Optional<User> userOptional = InMemoryUserRepository.findByAccount(accountValue);
            if (userOptional.isPresent()) {
                log.error("중복 사용자 등록 : ", new MemberAlreadyExistException(accountValue));
                return redirect(INDEX_HTML);
            }
            InMemoryUserRepository.save(
                    new User(
                            requestBody.getParamValue("account"),
                            requestBody.getParamValue("password"),
                            requestBody.getParamValue("email")
                    )
            );

            return redirect(INDEX_HTML);
        }

        if (requestLine.getHttpMethod() == HttpMethod.GET && !requestLine.getPath().equals("/")) {
            return render(requestLine.getPath());
        }

        return HttpResponse.of(HttpStatus.OK, ResponseBody.noContent(ContentType.HTML));
    }

    private boolean isLoggedIn(final HttpRequest httpRequest) {
        final Optional<Cookie> cookieOptional = httpRequest.getCookieValue("JSESSIONID");
        if (cookieOptional.isPresent() && SessionManager.has(cookieOptional.get().getValue())) {
            final String cookieJsessionId = cookieOptional.get().getValue();
            final Session session = SessionManager.findSession(cookieJsessionId);
            return session.getId().equals(cookieJsessionId);
        }
        return false;
    }

    private HttpResponse render(final String path) throws IOException {
        final StaticResource staticResource = StaticResource.from(path);
        final ResponseBody responseBody = ResponseBody.from(staticResource);
        return HttpResponse.of(HttpStatus.OK, responseBody);
    }

    private HttpResponse redirect(final String path) throws IOException {
        final StaticResource staticResource = StaticResource.from(path);
        final ResponseBody responseBody = ResponseBody.from(staticResource);
        return HttpResponse.redirect(HttpStatus.FOUND, path, responseBody);
    }

    private HttpResponse redirectWithSession(final String path, final String sessionId) throws IOException {
        final HttpResponse httpResponse = redirect(path);
        httpResponse.addSession(sessionId);
        return httpResponse;
    }
}
