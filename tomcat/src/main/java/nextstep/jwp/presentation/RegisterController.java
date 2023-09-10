package nextstep.jwp.presentation;

import static nextstep.jwp.exception.AuthExceptionType.DUPLICATED_ID;
import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

import nextstep.jwp.common.FormData;
import nextstep.jwp.common.ResourceLoader;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String INDEX_HTML = "/index.html";

    private final ResourceLoader resourceLoader;

    public RegisterController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        return register(request);
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.status(OK)
                .body(resourceLoader.load("static/register.html"))
                .contentType(TEXT_HTML)
                .build();
    }

    private HttpResponse register(HttpRequest request) {
        FormData formData = FormData.from(request.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new AuthException(DUPLICATED_ID);
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.status(FOUND)
                .redirectUri(INDEX_HTML)
                .build();
    }
}
