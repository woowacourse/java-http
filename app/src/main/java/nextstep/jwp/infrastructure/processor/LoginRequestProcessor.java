package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import nextstep.jwp.model.User;

import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

public class LoginRequestProcessor implements RequestProcessor {

    private static final String LOGIN_SUCCESS_URI = "index.html";
    private static final String LOGIN_FAILURE_URI = "401.html";

    @Override
    public String processResponse(CustomHttpRequest request, OutputStream outputStream) {
        Map<String, String> params = request.getParams();
        String account = params.get("account");
        String password = params.get("password");
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);

        if (byAccount.isPresent() && byAccount.get().checkPassword(password)) {
            return CustomHttpResponse.found(LOGIN_SUCCESS_URI);
        }
        return CustomHttpResponse.found(LOGIN_FAILURE_URI);
    }
}
