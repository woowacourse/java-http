package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotEnoughConditionException;
import nextstep.jwp.http.QueryStringConverter;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.support.View;
import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;

import java.util.Map;

public class RegisterController implements Controller {

    @Override
    public void service(final Request request, final Response response) {
        try {
            final User user = convert(request.getContent());
            InMemoryUserRepository.save(user);
        } catch (NotEnoughConditionException e) {
            makeErrorResponse(response);
            return;
        }
        makeRedirectResponse(response);
    }

    private User convert(final String parameters) {
        final Map<String, String> mapping = QueryStringConverter.convert(parameters);
        return new User(mapping.get("account"), mapping.get("password"), mapping.get("email"));
    }

    private void makeErrorResponse(final Response response) {
        response.header(HttpHeader.LOCATION, "/register")
                .httpStatus(HttpStatus.FOUND);
    }

    private void makeRedirectResponse(final Response response) {
        response.header(HttpHeader.LOCATION, View.INDEX.getValue())
                .httpStatus(HttpStatus.FOUND);
    }
}
