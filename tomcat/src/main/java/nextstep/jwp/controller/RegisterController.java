package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String INDEX_TEMPLATE = "index.html";
    private static final String REGISTER_TEMPLATE = "register.html";

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        final Map<String, String> requestForms = request.getRequestForms().getFormData();
        final String account = requestForms.get(ACCOUNT);
        final String email = requestForms.get(EMAIL);
        final String password = requestForms.get(PASSWORD);
        InMemoryUserRepository.save(new User(account, password, email));

        response.setLocation(INDEX_TEMPLATE);
        response.setStatus(HttpStatus.FOUND);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        final Response createdResponse = Response.createByTemplate(HttpStatus.OK, REGISTER_TEMPLATE);
//        response.setStatus(HttpStatus.OK);
//        final ResponseBody responseBody = ResponseBody.from(REGISTER_TEMPLATE);
//        response.setBody(responseBody);
        response.setBy(createdResponse);
    }
}
