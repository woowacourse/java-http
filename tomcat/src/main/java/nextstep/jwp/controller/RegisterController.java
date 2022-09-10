package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.reqeust.QueryParams;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        super.doPost(request, response);
        registerUser(request);
    }

    private void registerUser(final HttpRequest request) {
        QueryParams queryParams = new QueryParams(request.getBody());
        Map<String, String> queries = queryParams.getValues();
        String account = queries.get("account");
        String password = queries.get("password");
        String email = queries.get("email");

        validateUserInformation(account, password, email);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private void validateUserInformation(final String account, final String password, final String email) {
        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException();
        }
    }
}
