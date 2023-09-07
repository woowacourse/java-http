package mvc.controller;

import mvc.controller.exception.InvalidParameterException;
import nextstep.jwp.application.UserService;
import org.apache.coyote.http.HttpSession;
import servlet.request.HttpRequest;
import servlet.response.HttpResponse;

public class RegisterController extends AbstractPathController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    private final String path;
    private final UserService userService;

    public RegisterController(final String path, final UserService userService) {
        this.path = path;
        this.userService = userService;
    }

    @Override
    public boolean supports(final HttpRequest request) {
        return request.matchesByPath(path);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter(ACCOUNT_KEY);
        final String password = request.getParameter(PASSWORD_KEY);
        final String email = request.getParameter(EMAIL_KEY);

        if (isInvalidQueryParameter(account) || isInvalidQueryParameter(password) || isInvalidQueryParameter(email)) {
            throw new InvalidParameterException();
        }

        userService.register(account, password, email);

        response.sendRedirect("/login");
    }

    private boolean isInvalidQueryParameter(final String targetParameter) {
        return targetParameter == null || targetParameter.isEmpty() || targetParameter.isBlank();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpSession session = request.getSession(false);

        if (session != null) {
            final Object user = session.getAttribute(LoginController.ACCOUNT_KEY);

            if (user != null) {
                response.sendRedirect("/index.html");
                return;
            }

        }
        response.sendRedirect("/register.html");
    }
}
