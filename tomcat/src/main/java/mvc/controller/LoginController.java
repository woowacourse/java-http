package mvc.controller;

import mvc.controller.exception.InvalidParameterException;
import nextstep.jwp.application.UserService;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpSession;
import servlet.request.HttpRequest;
import servlet.response.HttpResponse;

public class LoginController extends AbstractPathController {

    public static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    private final String path;
    private final UserService userService;

    public LoginController(final String path, final UserService userService) {
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

        if (isInvalidQueryParameter(account) || isInvalidQueryParameter(password)) {
            throw new InvalidParameterException();
        }

        final User loginUser = userService.login(account, password);

        final HttpSession session = request.getSession(true);
        session.setAttribute(ACCOUNT_KEY, loginUser);
        final HttpCookie cookie = HttpCookie.fromSessionId(session.getId());

        response.addCookie(cookie);
        response.sendRedirect("/index.html");
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
                return ;
            }
        }
        response.sendRedirect("/login.html");
    }
}
