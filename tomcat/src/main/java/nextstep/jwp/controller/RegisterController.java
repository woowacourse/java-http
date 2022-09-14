package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.ok()
                .addResponseBody(View.REGISTER.getContents(), ContentType.TEXT_HTML_CHARSET_UTF_8)
                .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getRequestBody().getValue("account");
        String password = request.getRequestBody().getValue("password");
        String email = request.getRequestBody().getValue("email");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            InMemoryUserRepository.save(new User(account, password, email));
            return HttpResponse.redirect()
                    .addLocation(View.INDEX.getViewFileName())
                    .build();
        }

        return HttpResponse.redirect()
                .addLocation(View.UNAUTHORIZED.getViewFileName())
                .build();
    }
}
