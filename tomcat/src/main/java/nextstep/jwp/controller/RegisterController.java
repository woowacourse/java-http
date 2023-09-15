package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.common.HttpStatus.FOUND;
import static org.apache.coyote.http11.common.HttpStatus.OK;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.httpStatus(OK)
                .redirectUri("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.getContentValue("account");
        String email = requestBody.getContentValue("email");
        String password = requestBody.getContentValue("password");
        InMemoryUserRepository.save(new User(account, password, email));
        response.httpStatus(FOUND)
                .header("Location", "/index.html");
    }
}
