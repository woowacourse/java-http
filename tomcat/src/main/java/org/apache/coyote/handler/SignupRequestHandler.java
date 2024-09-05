package org.apache.coyote.handler;

import static org.apache.ResourceReader.readFile;
import static org.apache.coyote.http11.Http11Method.GET;
import static org.apache.coyote.http11.Http11Method.POST;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;

public class SignupRequestHandler implements RequestHandler {

    private static final List<Http11Method> ALLOWED_METHODS = List.of(POST, GET);
    private static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return httpRequest.getPath().equals("/register")
                && (ALLOWED_METHODS.contains(Http11Method.valueOf(httpRequest.getMethod())));
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
        User newUser = new User(param.get("account"), param.get("password"), param.get("email"));
        validateExists(newUser);
        InMemoryUserRepository.save(newUser);
        Session session = httpRequest.getSession();
        session.setAttribute("user", newUser);
        SessionManager.getInstance().add(session);
        return Http11Response.builder()
                .status(HttpStatus.FOUND)
                .appendHeader("Set-Cookie", SESSION_ID_COOKIE_NAME + "=" + session.getId())
                .appendHeader("Location", "/index.html")
                .build();
    }

    private void validateExists(User user) {
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
            throw new UncheckedServletException(new IllegalStateException("이미 존재하는 아이디 입니다."));
        }
    }

    private Http11Response get(HttpRequest httpRequest) {
        try {
            return Http11Response.builder()
                    .status(HttpStatus.OK)
                    .appendHeader("Content-Type", "text/html;charset=utf-8")
                    .body(readFile(httpRequest.getRequestURI()))
                    .build();
        } catch (IOException e) {
            throw new UncheckedServletException(new NoSuchFieldException("파일을 찾을 수 없습니다."));
        }
    }
}
