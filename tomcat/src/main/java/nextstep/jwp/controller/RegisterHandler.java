package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.request.RequestBody;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseHeader;
import org.apache.coyote.model.response.StatusCode;

import static org.apache.coyote.model.response.HttpResponse.createResponse;


public class RegisterHandler extends AbstractHandler {

    private static final String REGISTER_HTML = "/register.html";
    private static final String INDEX_HTML = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final RegisterHandler INSTANCE = new RegisterHandler();
    private static final String CLIENT_ERROR_404 = "/404.html";

    public static RegisterHandler getINSTANCE() {
        return INSTANCE;
    }

    @Override
    protected String getMethodResponse(final HttpRequest httpRequest) {
        return createResponse(StatusCode.OK, HttpResponse.getResponseBody(REGISTER_HTML, getClass()))
                .getResponse();
    }

    @Override
    protected String postMethodResponse(final HttpRequest httpRequest) {
        saveUser(httpRequest);
        HttpResponse httpResponse = createResponse(StatusCode.FOUND, HttpResponse.getResponseBody(INDEX_HTML, getClass()));
        httpResponse.setHeader(ResponseHeader.LOCATION, INDEX_HTML);
        return httpResponse.getResponse();
    }

    @Override
    protected String otherMethodResponse(final HttpRequest httpRequest) {
        return createResponse(StatusCode.NOT_FOUND, HttpResponse.getResponseBody(CLIENT_ERROR_404, getClass()))
                .getResponse();
    }

    private void saveUser(final HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.getByKey(ACCOUNT);
        String password = requestBody.getByKey(PASSWORD);
        String email = requestBody.getByKey(EMAIL);
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
