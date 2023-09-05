package nextstep.jwp.controller.login;

import nextstep.jwp.controller.base.AbstractController;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Header;
import org.apache.coyote.http11.response.header.Status;

import java.util.Map;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws Exception {
        return super.handle(httpRequest);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        if (httpRequest.getSessionAttribute("user").isPresent()) {
            return HttpResponse.found(ContentType.HTML, Status.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
        }

        return HttpResponse.okWithResource("/login.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        Map<String, String> params = httpRequest.getBody().getParams();

        if (params.isEmpty()) {
            throw new IllegalArgumentException("계정과 비밀번호를 입력하세요.");
        }

        User user;
        try {
            if (!params.containsKey("account") || !params.containsKey("password")) {
                throw new IllegalArgumentException("queryParameter가 잘못되었습니다.");
            }
            user = userService.login(params.get("account"), params.get("password"));
        } catch (IllegalArgumentException e) {
            return HttpResponse.badRequest("/index.html");
        } catch (UnAuthorizedException e) {
            return HttpResponse.unAuthorized("/401.html");
        }

        Session session = httpRequest.createSession();
        session.setAttribute("user", user);
        HttpResponse response = HttpResponse.found(ContentType.HTML, Status.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
        response.setCookie(Cookie.fromUserJSession(session.getId()));
        return response;
    }

}
