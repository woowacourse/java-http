package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.cookie.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;

public class RequestReader {

    private static final Logger log = LoggerFactory.getLogger(RequestReader.class);
    private static final Pattern FILE_EXTENSION_REGEX = Pattern.compile(".css|.js|.ico|.html");
    private static final String INDEX_HTML = "/index.html";
    private static final String LOGIN_HTML = "/login.html";
    private static final String REGISTER_HTML = "/register.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    public HttpResponse parse(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = HttpResponse.create();
        if (FILE_EXTENSION_REGEX.matcher(httpRequest.getRequestUri()).find() && httpRequest.isMatchMethod(HttpMethod.GET)) {
            StaticResource staticResource = StaticResource.of(httpRequest.getRequestUri());
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setResponseBody(responseBody);
            return httpResponse;
        }

        if (httpRequest.getRequestUri().startsWith("/login") && httpRequest.isMatchMethod(HttpMethod.GET)) {
            if (httpRequest.existsSession()) {
                Session session = httpRequest.getSession(false);
                User user = (User) session.getAttribute("user");
                log.info("user: {}", user);
                httpResponse.location(INDEX_HTML);
                httpResponse.setStatusCode(HttpStatusCode.FOUND);
                return httpResponse;

            }
            StaticResource staticResource = StaticResource.of(LOGIN_HTML);
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setResponseBody(responseBody);
            return httpResponse;
        }

        if (httpRequest.isStartsWith("/login") && httpRequest.isMatchMethod(HttpMethod.POST)) {
            String account = httpRequest.getBody("account");
            String password = httpRequest.getBody("password");
            return redirectLogin(httpRequest, httpResponse, account, password);
        }

        if (httpRequest.isStartsWith("/register") && httpRequest.isMatchMethod(HttpMethod.GET)) {
            StaticResource staticResource = StaticResource.of(REGISTER_HTML);
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setResponseBody(responseBody);
            return httpResponse;
        }

        if (httpRequest.isStartsWith("/register") && httpRequest.isMatchMethod(HttpMethod.POST)) {
            InMemoryUserRepository.save(new User(httpRequest.getBody("account"), httpRequest.getBody("password"), httpRequest.getBody("email")));
            httpResponse.location(INDEX_HTML);
            httpResponse.setStatusCode(HttpStatusCode.FOUND);
            return httpResponse;
        }
        httpResponse.setResponseBody(ResponseBody.of("Hello world!", "html"));
        return httpResponse;
    }

    private HttpResponse redirectLogin(HttpRequest httpRequest, HttpResponse httpResponse, String account, String password) throws IOException {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        if (!user.checkPassword(password)) {
            StaticResource staticResource = StaticResource.of(UNAUTHORIZED_HTML);
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setStatusCode(HttpStatusCode.UNAUTHORIZED);
            httpResponse.setResponseBody(responseBody);
            return httpResponse;

        }
        log.info("user: {}", user);
        Session session = httpRequest.getSession(true);
        session.setAttribute("user", user);
        httpResponse.location(INDEX_HTML);
        httpResponse.setStatusCode(HttpStatusCode.FOUND);
        httpResponse.addCookie(HttpCookie.jSessionId(session.getId()));
        return httpResponse;
    }
}
