package org.apache.coyote.http11.controller;

import nextstep.jwp.controller.UserController;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.session.HttpSession;

public class LoginController extends AbstractController<UserController> {

    private static final LoginController INSTANCE = new LoginController(UserController.getInstance());

    private LoginController(UserController userController) {
        super(userController);
    }

    public static LoginController getInstance() {
        return INSTANCE;
    }

    @Override
    protected ResponseEntity doGet(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        HttpSession session = httpRequest.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            return ResponseEntity.found()
                    .path("/index")
                    .build();
        }

        return ResponseEntity.ok()
                .path(requestLine.getPath())
                .build();
    }

    @Override
    protected ResponseEntity doPost(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();

        String account = requestBody.get("account");
        String password = requestBody.get("password");

        return getResponseEntity(httpRequest, account, password);
    }

    private ResponseEntity getResponseEntity(HttpRequest httpRequest, String account, String password) {
        try {
            User user = controller.login(account, password);
            HttpSession httpSession = httpRequest.getSession(true);
            httpSession.setAttribute("user", user);

            httpRequest.addSession(httpSession);

            HttpCookie httpCookie = HttpCookie.create();
            httpCookie.putJSessionId(httpSession.getId());

            return ResponseEntity.found()
                    .path("/index")
                    .httpCookie(httpCookie)
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.found()
                    .path("/401")
                    .build();
        }
    }

}
