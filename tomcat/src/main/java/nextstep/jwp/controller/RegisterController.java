package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotEnoughConditionException;
import nextstep.jwp.http.Headers;
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
    public Response execute(final Request request) {
        try {
            final User user = convert(request.getContent());
            InMemoryUserRepository.save(user);

        } catch (NotEnoughConditionException e) {
            return errorResponse();
        }
        return redirectResponse();
    }

    private User convert(final String parameters) {
        final Map<String, String> mapping = QueryStringConverter.convert(parameters);
        return new User(mapping.get("account"), mapping.get("password"), mapping.get("email"));
    }

    private Response errorResponse() {
        final Headers headers = new Headers();
        headers.put(HttpHeader.LOCATION, "/register");
        return new Response(headers).httpStatus(HttpStatus.FOUND);
    }

    private Response redirectResponse() {
        final Headers headers = new Headers();
        headers.put(HttpHeader.LOCATION, View.INDEX.getValue());
        return new Response(headers).httpStatus(HttpStatus.FOUND);
    }
}
