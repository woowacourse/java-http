package nextstep.jwp.presentation;

import static nextstep.jwp.presentation.StaticResource.INDEX_PAGE;
import static org.apache.catalina.Session.JSESSIONID;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.catalina.Session;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController(UserService.getInstance());

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    private final UserService userService;

    private RegisterController(final UserService userService) {
        this.userService = userService;
    }

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setBody(StaticResource.ofRequest(request));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final User user = userService.register(request);
            setLoginSession(request, response, user);
            redirectIndex(response);
        } catch (final RuntimeException runtimeException) {
            LOG.error(runtimeException.getMessage());
        }

        response.setBody(StaticResource.ofRequest(request));
    }

    private void setLoginSession(final HttpRequest request, final HttpResponse response, final User user) {
        final Session session = request.getSession(true);
        session.setAttribute("user", user);
        response.addSetCookie(JSESSIONID, session.getId());
    }

    private void redirectIndex(final HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(INDEX_PAGE);
    }
}
