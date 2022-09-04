package nextstep.jwp.view;

import java.util.Objects;
import org.apache.http.HttpRequest;
import spring.Controller;

public class WelcomeController implements Controller {

    private static final String WELCOME_MESSAGE = "Hello world!";

    @Override
    public boolean support(final String requestUri) {
        return Objects.equals("/", requestUri);
    }

    @Override
    public String service(final HttpRequest httpRequest) {
        return WELCOME_MESSAGE;
    }
}
