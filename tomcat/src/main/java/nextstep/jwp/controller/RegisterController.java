package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotEnoughConditionException;
import nextstep.jwp.http.QueryStringConverter;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.support.Resource;
import nextstep.jwp.support.View;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpStatus;

import java.util.Map;

public class RegisterController extends AbstractController {

    @Override
    public void doGet(final Request request, final Response response) {
        final Resource resource = new Resource(View.REGISTER.getValue());
        response.header(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue())
                .content(resource.read());
    }

    @Override
    public void doPost(final Request request, final Response response) {
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
