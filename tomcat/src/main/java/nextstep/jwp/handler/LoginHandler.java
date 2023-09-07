package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidRequestMethodException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpCookie;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.common.HttpVersion;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryString;
import nextstep.jwp.http.response.FormData;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatusLine;
import nextstep.jwp.model.User;
import nextstep.jwp.security.Session;
import nextstep.jwp.security.SessionManager;
import org.apache.catalina.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final String RESOURCE_PATH = "static/login.html";
    private static final String INDEX_URI = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";
    private static final Manager SESSION_MANAGER = SessionManager.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            return handleGetMethod(request);
        }

        if (HttpMethod.POST.equals(request.getHttpMethod())) {
            return handlePostMethod(request);
        }

        throw new InvalidRequestMethodException("지원하지 않는 메서드입니다.");
    }

    private HttpResponse handleGetMethod(HttpRequest request) throws IOException {
        if (request.hasCookie()) {
            HttpCookie cookie = request.getCookie();
            String jSessionId = cookie.get(JSESSIONID);
            Session session = SESSION_MANAGER.findSession(jSessionId);
            User user = (User) session.getAttribute("user");

            return createLoginResponse(request, user);
        }

        if (request.hasQueryString()) {
            QueryString queryString = request.getQueryString();
            User user = login(queryString.get(ACCOUNT), queryString.get(PASSWORD));

            return createLoginResponse(request, user);
        }

        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpStatusLine httpStatusLine = new HttpStatusLine(httpVersion, httpStatus);
        URL url = getClass().getClassLoader().getResource(RESOURCE_PATH);
        HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

    private HttpResponse createLoginResponse(HttpRequest request, User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
        HttpResponse response = createRedirectingIndexResponse(request);

        setCookie(request, response, session);

        return response;
    }

    private HttpResponse createRedirectingIndexResponse(HttpRequest request) {
        return createRedirectResponse(request, INDEX_URI);
    }

    private HttpResponse createRedirectResponse(HttpRequest request, String location) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpStatusLine httpStatusLine = new HttpStatusLine(httpVersion, httpStatus);
        HttpBody httpBody = HttpBody.createEmptyBody();
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);
        httpHeaders.setLocation(location);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

    private void setCookie(HttpRequest request, HttpResponse response, Session session) {
        if (request.hasCookie()) {
            HttpCookie cookie = request.getCookie();
            if (cookie.containsKey(JSESSIONID)) {
                return;
            }
        }

        response.setCookie(JSESSIONID + "=" + session.getId());
    }

    private User login(String account, String password) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                LOG.info("로그인 성공 ! 아이디 : {}", user.getAccount());
                return user;
            }
        }

        throw new UnauthorizedException("로그인에 실패했습니다. account : " + account + " password : " + password);
    }

    private HttpResponse handlePostMethod(HttpRequest request) {
        FormData formData = FormData.from(request.getHttpBody());
        User user = login(formData.get(ACCOUNT), formData.get(PASSWORD));

        return createLoginResponse(request, user);
    }

}
