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
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        fileHandler.handle(httpRequest, httpResponse);
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        FormData formData = FormData.of(httpRequest.getBody());

        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");

        InMemoryUserRepository.save(new User(account, password, email));
        httpResponse.redirectTo(MAIN_LOCATION);
    }
}
