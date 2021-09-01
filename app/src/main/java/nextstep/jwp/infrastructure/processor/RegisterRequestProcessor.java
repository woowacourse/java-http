package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import nextstep.jwp.model.User;

import java.io.OutputStream;
import java.util.Map;

public class RegisterRequestProcessor implements RequestProcessor {

    private static final String REGISTER_SUCCESS_URI = "login.html";

    @Override
    public String processResponse(CustomHttpRequest request, OutputStream outputStream) {
        Map<String, String> params = request.getParams();
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return CustomHttpResponse.found(REGISTER_SUCCESS_URI);
    }
}
