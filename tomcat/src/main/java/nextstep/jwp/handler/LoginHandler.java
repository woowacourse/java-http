package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidRequestMethod;
import nextstep.jwp.http.FormData;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.QueryString;
import nextstep.jwp.model.User;
import nextstep.jwp.security.Session;
import nextstep.jwp.security.SessionManager;
import org.apache.catalina.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final RequestHandler HANDLER = new LoginHandler();
    private static final Manager SESSION_MANAGER = SessionManager.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(LoginHandler.class);

    private LoginHandler() {
    }

    public static RequestHandler getInstance() {
        return HANDLER;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            return handleGetMethod(request);
        }

        if (HttpMethod.POST.equals(request.getHttpMethod())) {
            return handlePostMethod(request);
        }

        throw new InvalidRequestMethod();
    }

    private HttpResponse handleGetMethod(HttpRequest request) throws IOException {
        if (request.hasCookie()) {
            HttpCookie cookie = request.getCookie();
            String jSessionId = cookie.get("JSESSIONID");
            Session session = SESSION_MANAGER.findSession(jSessionId);
            User user = (User) session.getAttribute("user");

            return createLoginResponse(request, user);
        }

        if (request.hasQueryString()) {
            QueryString queryString = request.getQueryString();
            User user = login(queryString.get("account"), queryString.get("password"));

            if (user != null) {
                return createLoginResponse(request, user);
            }

            return createLoginFailResponse(request);
        }

        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        URL url = getClass().getClassLoader().getResource("static/login.html");
        HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
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
        return createRedirectResponse(request, "/index.html");
    }

    private HttpResponse createRedirectResponse(HttpRequest request, String location) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = HttpBody.from("");
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);
        httpHeaders.setLocation(location);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

    private void setCookie(HttpRequest request, HttpResponse response, Session session) {
        if (request.hasCookie()) {
            HttpCookie cookie = request.getCookie();
            if (cookie.containsKey("JSESSIONID")) {
                return;
            }
        }

        response.setCookie("JSESSIONID=" + session.getId());
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

        return null;
    }

    private HttpResponse createLoginFailResponse(HttpRequest request) {
        return createRedirectResponse(request, "/401.html");
    }

    private HttpResponse handlePostMethod(HttpRequest request) {
        FormData formData = FormData.from(request.getHttpBody());
        User user = login(formData.get("account"), formData.get("password"));

        if (user != null) {
            return createLoginResponse(request, user);
        }

        return createLoginFailResponse(request);
    }

}
