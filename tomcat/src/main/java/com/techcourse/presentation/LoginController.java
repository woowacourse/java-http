package com.techcourse.presentation;

import com.techcourse.application.LoginService;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String BASE_URL = "/login";

    private final LoginService loginService;
    private final StaticResourceController staticResourceController;

    public LoginController(
            final LoginService loginService,
            final StaticResourceController staticResourceController
    ) {
        this.loginService = loginService;
        this.staticResourceController = staticResourceController;
    }

    public HttpResponse renderLoginPage(final HttpRequest request) {
        final Session session = request.getSession(false);
        if (session != null) {
            return HttpResponse.builder()
                    .protocol(request.protocol())
                    .found()
                    .header("Location", "/index.html")
                    .build();
        }

        return staticResourceController.getResource(createStaticRequest("/login.html", request.protocol()));
    }

    public HttpResponse login(final HttpRequest request) {
        final Map<String, String> params = request.params();
        final String account = params.get("account");
        final String password = params.get("password");

        try {
            if (account == null || password == null) {
                log.debug("요청 파라미터: {}", params);
                throw new IllegalArgumentException("적절하지 않은 로그인 요청입니다.");
            }

            final User user = loginService.login(account, password);
            
            if (user != null) {
                final Session session = request.getSession(true);
                session.setAttribute(user.getAccount(), user);
                SessionManager.getInstance().add(session);
                
                return HttpResponse.builder()
                        .protocol(request.protocol())
                        .found()
                        .addCookie("JSESSIONID" + session.getId())
                        .header("Location", "/index.html")
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return staticResourceController.getResource(createStaticRequest("/401.html", request.protocol()));
        }

        return staticResourceController.getResource(createStaticRequest("/401.html", request.protocol()));
    }

    @Override
    public boolean isResponsible(final String path) {
        return path.startsWith(BASE_URL);
    }

    @Override
    public HttpResponse getResource(final HttpRequest request) {
        if (!BASE_URL.equals(request.path())) {
            log.debug("요청 경로: {}", request.path());
            throw new IllegalArgumentException("요청 경로와 일치하는 API가 존재하지 않습니다.");
        }

        if ("GET".equals(request.method())) {
            return renderLoginPage(request);
        }
        if ("POST".equals(request.method())) {
            return login(request);
        }
        throw new IllegalArgumentException("요청 경로에 일치하는 메서드가 없습니다.");
    }

    private HttpRequest createStaticRequest(final String path, final String protocol) {
        return new HttpRequest("GET", path, protocol, new HashMap<>(), new HashMap<>());
    }

}
