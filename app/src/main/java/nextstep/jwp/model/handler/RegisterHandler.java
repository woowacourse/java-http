package nextstep.jwp.model.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.http_request.JwpHttpRequest;
import nextstep.jwp.model.http_response.JwpHttpResponse;
import nextstep.jwp.model.user.domain.User;

import java.io.IOException;
import java.net.URISyntaxException;

public class RegisterHandler extends DefaultHttpHandler {

    private static final String RESOURCE_PREFIX = "static/";
    private static final String REGISTER_PAGE_PATH = "register.html";
    private static final String REGISTER_SUCCESS_PATH = "index.html";

    @Override
    public String handle(JwpHttpRequest jwpHttpRequest) throws IOException, URISyntaxException {
        if (jwpHttpRequest.isEmptyParams()) {
            return renderRegisterPage();
        }

        return registerUser(jwpHttpRequest);
    }

    private String renderRegisterPage() throws URISyntaxException, IOException {
        String resourceUri = RESOURCE_PREFIX + REGISTER_PAGE_PATH;
        String resourceFile = findResourceFile(resourceUri);
        return JwpHttpResponse.ok(resourceUri, resourceFile);
    }

    private String registerUser(JwpHttpRequest jwpHttpRequest) {
        User user = assembleUser(jwpHttpRequest);
        InMemoryUserRepository.save(user);
        return JwpHttpResponse.found(REGISTER_SUCCESS_PATH);
    }

    private User assembleUser(JwpHttpRequest jwpHttpRequest) {
        String account = jwpHttpRequest.getParam("account");
        String password = jwpHttpRequest.getParam("password");
        String email = jwpHttpRequest.getParam("email");
        return new User(account, password, email);
    }
}
