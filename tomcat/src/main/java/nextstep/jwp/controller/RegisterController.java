package nextstep.jwp.controller;

import static org.apache.coyote.http11.StaticPages.INDEX_PAGE;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponseStatusLine;
import org.apache.coyote.http11.ResponseEntityFactory;

public class RegisterController extends RestController {

    private static final String SUPPORTED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    public RegisterController() {
        super("/register");
    }

    @Override
    public ResponseEntity service(final HttpRequest httpRequest) {
        final Map<String, String> body = httpRequest.getBody();
        final User user = new User(body.get("account"), body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);

        return ResponseEntityFactory.createRedirectHttpResponse(HttpResponseStatusLine.FOUND(), INDEX_PAGE);
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final boolean isPostMethod = httpRequest.getMethod() == HttpMethod.POST;
        final boolean isSupportedContentType = httpRequest.containsContentType(SUPPORTED_CONTENT_TYPE);

        return super.canHandle(httpRequest) && isPostMethod && isSupportedContentType;
    }
}
