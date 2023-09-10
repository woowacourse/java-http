package nextstep.jwp.controller;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.HttpSession;

public class LoginController extends AbstractController<UserService> {

    public LoginController(UserService userService) {
        super(userService);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();
        HttpSession session = httpRequest.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            httpResponse.setHttpStatus(HttpStatus.FOUND)
                    .setRedirect("/index");
            return;
        }

        httpResponse.setHttpStatus(HttpStatus.OK)
                .setRedirect(requestLine.getPath());
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestBody requestBody = httpRequest.getRequestBody();

        String account = requestBody.get("account");
        String password = requestBody.get("password");

        try {
            User user = service.login(account, password);

            HttpSession httpSession = httpRequest.getSession(true);
            httpSession.setAttribute("user", user);

            httpRequest.addSession(httpSession);

            HttpCookie httpCookie = HttpCookie.create();
            httpCookie.putJSessionId(httpSession.getId());

            httpResponse.setHttpStatus(HttpStatus.FOUND)
                    .setRedirect("/index")
                    .setCookie(httpCookie);
        } catch (IllegalArgumentException e) {
            httpResponse.setHttpStatus(HttpStatus.FOUND)
                    .setRedirect("/401");
        }
    }

}
