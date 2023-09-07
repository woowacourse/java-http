package nextstep.jwp.controller;

import nextstep.jwp.application.MemberService;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.cookie.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBody("account");
        String password = request.getBody("password");
        try {
            User findUser = MemberService.login(account, password);

            Session session = request.getSession(true);
            session.setAttribute("user", findUser);
            response.location("index.html");
            response.setStatusCode(HttpStatusCode.FOUND);
            response.addCookie(HttpCookie.jSessionId(session.getId()));
        } catch (UserNotFoundException e) {
            StaticResource staticResource = StaticResource.of("/401.html");
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            response.setStatusCode(HttpStatusCode.UNAUTHORIZED);
            response.setResponseBody(responseBody);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.existsSession()) {
            response.location("index.html");
            response.setStatusCode(HttpStatusCode.FOUND);
            return;
        }
        StaticResource staticResource = StaticResource.of("/login.html");
        ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
        response.setResponseBody(responseBody);
    }
}
