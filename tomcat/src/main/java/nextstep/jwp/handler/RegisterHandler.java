package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.body.FormData;
import org.apache.coyote.http11.handler.FileHandler;
import org.apache.coyote.http11.handler.GetAndPostHandler;

public class RegisterHandler extends GetAndPostHandler {

    private static final String MAIN_LOCATION = "/index";
    private final FileHandler fileHandler = new FileHandler();

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return fileHandler.handle(httpRequest);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        FormData formData = FormData.of(httpRequest.getBody());

        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");

        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponse.redirectTo(MAIN_LOCATION);
    }
}
