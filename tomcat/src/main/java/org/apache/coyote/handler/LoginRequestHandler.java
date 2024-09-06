package org.apache.coyote.handler;

import static org.apache.ResourceReader.readFile;
import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<HttpMethod> ALLOWED_METHODS = List.of(POST, GET);
    private static final String SUCCESS_LOGIN_REDIRECT_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return httpRequest.getPath().equals("/login")
                && (ALLOWED_METHODS.contains(httpRequest.getMethod()));
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (POST.equals(httpRequest.getMethod())) {
            return post(httpRequest);
        }
        if (GET.equals(httpRequest.getMethod())) {
            return get(httpRequest);
        }
        throw new UncheckedServletException(new UnsupportedOperationException("지원하지 않는 HTTP METHOD 입니다."));
    }

    private Http11Response post(HttpRequest httpRequest) {
        Map<String, String> param = httpRequest.getParsedBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(param.get("account"));
        if (user.isPresent() && user.get().checkPassword(param.get("password"))) {
            log.info("로그인 성공 : " + user.get().getAccount());
            Session session = httpRequest.getSession();
            session.setAttribute("user", user.get());
            SessionManager.getInstance().add(session);
            return Http11Response.builder()
                    .status(HttpStatus.FOUND)
                    .appendHeader("Set-Cookie", SESSION_ID_COOKIE_NAME + "=" + session.getId())
                    .appendHeader("Location", SUCCESS_LOGIN_REDIRECT_PATH)
                    .build();
        }
        return redirect(UNAUTHORIZED_PATH);
    }

    private Http11Response get(HttpRequest httpRequest) {
        if (httpRequest.isExistsSession()) {
            Session session = httpRequest.getSession();
            User user = (User) session.getAttribute("user");
            log.info("세션 로그인 : " + user.getAccount());
            return redirect(SUCCESS_LOGIN_REDIRECT_PATH);
        }
        return Http11Response.builder()
                .status(HttpStatus.OK)
                .appendHeader("Content-Type", "text/html;charset=utf-8")
                .body(readFile(httpRequest.getRequestURI()))
                .build();
    }

    private Http11Response redirect(String path) {
        return Http11Response.builder()
                .status(HttpStatus.FOUND)
                .appendHeader("Location", path)
                .build();
    }
}
