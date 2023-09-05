package nextstep.jwp.controller;

import nextstep.jwp.application.MemberService;
import nextstep.jwp.model.User;
import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.cookie.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getBody("account");
        String password = request.getBody("password");
        redirectLogin(request, response, account, password);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.existsSession()) {
            response.location("index.html");
            response.setStatusCode(HttpStatusCode.FOUND);
            return;
        }
        StaticResource staticResource = StaticResource.of("/login.html");
        ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
        response.setResponseBody(responseBody);
    }

    private void redirectLogin(HttpRequest httpRequest, HttpResponse httpResponse, String account, String password) throws IOException {
        Optional<User> findUser = MemberService.login(account, password);
        if (findUser.isEmpty()) {
            StaticResource staticResource = StaticResource.of("/401.html");
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setStatusCode(HttpStatusCode.UNAUTHORIZED);
            httpResponse.setResponseBody(responseBody);
            return;
        }
        Session session = httpRequest.getSession(true);
        session.setAttribute("user", findUser.get());
        httpResponse.location("index.html");
        httpResponse.setStatusCode(HttpStatusCode.FOUND);
        httpResponse.addCookie(HttpCookie.jSessionId(session.getId()));
    }
}
