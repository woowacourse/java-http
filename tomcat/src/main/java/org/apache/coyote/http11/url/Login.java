package org.apache.coyote.http11.url;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpDataRequest;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends Url {
    private static final Logger log = LoggerFactory.getLogger(Login.class);
    private static final String SESSION_KEY = "user";

    public Login(HttpRequest request) {
        super(request);
    }

    @Override
    public HttpResponse handle(HttpHeaders httpHeaders, String requestBody) {
        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            try {
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute(SESSION_KEY);
                if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
                    String resource = IOUtils.readResourceFile("/index.html");
                    return new HttpResponse(new StatusLine(HttpStatus.FOUND),
                            ResponseHeaders.create(getPath(), resource), resource);
                }
            } catch (NullPointerException e) {
                log.info(e.getMessage(), e);
                String resource = IOUtils.readResourceFile(getPath());
                return new HttpResponse(new StatusLine(HttpStatus.OK), ResponseHeaders.create(getPath(), resource),
                        resource);
            }
        }

        if (HttpMethod.POST.equals(request.getHttpMethod())) {
            var data = HttpDataRequest.extractRequest(requestBody);
            var user = InMemoryUserRepository.findByAccount(data.get("account"))
                    .orElse(null);
            String resource = IOUtils.readResourceFile("/401.html");
            if (Objects.isNull(user)) {
                return new HttpResponse(new StatusLine(HttpStatus.UNAUTHORIZED),
                        ResponseHeaders.create(getPath(), resource), resource);
            }
            if (user.checkPassword(data.get("password"))) {
                final var session = validateSession(user);
                var requestCookie = request.addCookie(HttpCookie.ofJSessionId(session.getId()));
                String indexResource = IOUtils.readResourceFile("/index.html");
                return new HttpResponse(new StatusLine(HttpStatus.FOUND), ResponseHeaders.create(getPath(), indexResource),
                        indexResource).addCookie(requestCookie);
            }
            log.info("user : {}", user);

            return new HttpResponse(new StatusLine(HttpStatus.UNAUTHORIZED), ResponseHeaders.create(getPath(), resource), resource);
        }
        throw new IllegalArgumentException("Login에 해당하는 HTTP Method가 아닙니다. : " + request.getHttpMethod());
    }

    private HttpSession validateSession(User user) {
        SessionManager sessionManager = new SessionManager();
        final var session = request.getSession();
        session.setAttribute("user", user);
        sessionManager.add(session);
        log.info("sessionManager에 저장 : {}", session);
        return session;
    }
}
