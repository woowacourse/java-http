package nextstep.jwp.controller;

import nextstep.jwp.application.MemberService;
import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String email = httpRequest.getBody("email");
        String password = httpRequest.getBody("password");
        String account = httpRequest.getBody("account");
        MemberService.register(account, email, password);
        httpResponse.location("/index.html");
        httpResponse.setStatusCode(HttpStatusCode.FOUND);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        StaticResource staticResource = StaticResource.of("/register.html");
        ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
        response.setResponseBody(responseBody);
    }
}
