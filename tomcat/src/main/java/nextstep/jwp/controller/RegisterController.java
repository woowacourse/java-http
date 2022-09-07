package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.apache.coyote.http11.response.header.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.util.StringUtils.HTML_FILE_EXTENSION;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.Location;

public class RegisterController extends AbstractController {

    private static final String PATH = "/register";
    private static final String REGISTER_SUCCESS_REDIRECT_URI = "/index.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathOf(PATH) &&
                (httpRequest.hasHttpMethodOf(POST) || httpRequest.hasHttpMethodOf(GET));
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        super.readFile(PATH + HTML_FILE_EXTENSION, httpResponse);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = new User(
                httpRequest.getParamValueOf(ACCOUNT_KEY),
                httpRequest.getParamValueOf(PASSWORD_KEY),
                httpRequest.getParamValueOf(EMAIL_KEY)
        );
        InMemoryUserRepository.save(user);

        httpResponse.setHttpStatusCode(FOUND);
        httpResponse.addHeader(new Location(REGISTER_SUCCESS_REDIRECT_URI));
    }
}
