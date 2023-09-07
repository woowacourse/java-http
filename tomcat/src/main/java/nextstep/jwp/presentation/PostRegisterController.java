package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponseBuilder;

import java.io.IOException;

public class PostRegisterController implements Controller {

    private static final String KEY_VALUE_SEPARATOR = "=";

    @Override
    public String process(HttpRequest httpRequest, HttpResponseBuilder httpResponseBuilder) throws IOException {
        String[] splitRequestBody = httpRequest.getMessageBody().split("&");
        String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
        String email = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
        email = email.replace("%40", "@");
        String password = splitRequestBody[2].split(KEY_VALUE_SEPARATOR)[1];

        InMemoryUserRepository.save(new User(account, password, email));
        return httpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, "/index.html");
    }
}
