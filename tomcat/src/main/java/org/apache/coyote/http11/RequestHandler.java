package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestPath;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static final SessionManager sessionManager = new SessionManager();

    public static HttpResponse handleRequest(final HttpRequest httpRequest) throws IOException {
        final RequestPath requestPath = httpRequest.getRequestPath();
        final String requestResource = requestPath.getResource();

        // 기본
        if (requestResource.equals("/")) {
            final ResponseBody responseBody = ResponseBody.of(HttpExtensionType.HTML.getExtension(), "Hello world!");
            return HttpResponse.of(HttpStatusCode.OK, responseBody);
        }

        // 로그인 페이지
        if (requestResource.equals("/login") && httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
            final HttpCookie cookie = HttpCookie.from(httpRequest.getRequestHeaders().geHeaderValue("Cookie"));
            if (cookie.contains("JSESSIONID")) {
                final ResponseBody responseBody = FileExtractor.extractHtmlFile("/index");
                return HttpResponse.withCookie(HttpStatusCode.FOUND, responseBody, cookie);
            }
            final ResponseBody responseBody = FileExtractor.extractHtmlFile(requestResource);
            return HttpResponse.of(HttpStatusCode.OK, responseBody);
        }

        // 로그인 진행
        if (requestResource.equals("/login") && httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
            try {
                final String userName = httpRequest.getRequestBody().get("account");
                final String password = httpRequest.getRequestBody().get("password");

                final User user = InMemoryUserRepository.findByAccount(userName)
                        .orElseThrow(LoginException::new);

                if (user.checkPassword(password)) {
                    log.info(user.toString());
                    final HttpCookie cookie = HttpCookie.from(httpRequest.getRequestHeaders().geHeaderValue("Cookie"));
                    if (!cookie.contains("JSESSIONID")) {
                        final Session session = new Session(UUID.randomUUID().toString());
                        session.setAttribute("user", user);
                        sessionManager.add(session);
                        cookie.setCookie("JSESSIONID", UUID.randomUUID().toString());
                    }
                    final ResponseBody responseBody = FileExtractor.extractHtmlFile("/index");
                    return HttpResponse.withCookie(HttpStatusCode.FOUND, responseBody, cookie);
                }
                throw new LoginException();
            } catch (LoginException exception) {
                final ResponseBody responseBody = FileExtractor.extractHtmlFile("/401");
                return HttpResponse.of(HttpStatusCode.UNAUTHORIZED, responseBody);
            }
        }

        // 회원가입 페이지
        if (requestResource.equals("/register") && httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
            final ResponseBody responseBody = FileExtractor.extractHtmlFile("/register");
            return HttpResponse.of(HttpStatusCode.OK, responseBody);
        }

        // 회원가입 진행
        if (requestResource.equals("/register") && httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
            final Map<String, String> requestBody = httpRequest.getRequestBody();

            final String account = requestBody.get("account");
            final String password = requestBody.get("password");
            final String email = requestBody.get("email");

            if (InMemoryUserRepository.checkExistingId(account)) {
                final ResponseBody responseBody = FileExtractor.extractHtmlFile("/409");
                return HttpResponse.of(HttpStatusCode.OK, responseBody);
            }
            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            final ResponseBody responseBody = FileExtractor.extractHtmlFile("/index");
            return HttpResponse.of(HttpStatusCode.OK, responseBody);
        }

        // 나머지
        final ResponseBody responseBody = FileExtractor.extractFile(requestResource);
        return HttpResponse.of(HttpStatusCode.OK, responseBody);
    }
}
