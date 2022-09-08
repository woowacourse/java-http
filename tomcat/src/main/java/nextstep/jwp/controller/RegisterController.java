package nextstep.jwp.controller;

import static org.apache.coyote.http11.message.common.ContentType.HTML;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.exception.InvalidRequestException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.QueryString;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.util.StaticFileUtil;

public class RegisterController extends AbstractController {
    @Override
    protected HttpResponse handleGet(final HttpRequest request) {
        return new HttpResponse.Builder()
                .contentType(HTML)
                .body(StaticFileUtil.readFile("/register.html"))
                .build();
    }

    @Override
    protected HttpResponse handlePost(final HttpRequest request) {
        QueryString queryString = QueryString.parse(request.getRequestBody());
        String account = queryString.getValues("account").orElseThrow(InvalidRequestException::new);
        String password = queryString.getValues("password").orElseThrow(InvalidRequestException::new);
        String email = queryString.getValues("email").orElseThrow(InvalidRequestException::new);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return redirectTo("index.html");
    }
}
