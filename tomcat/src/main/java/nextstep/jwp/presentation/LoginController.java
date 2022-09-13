package nextstep.jwp.presentation;

import java.util.Map;
import nextstep.jwp.model.user.UserService;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11request.QueryStringUtil;
import org.apache.coyote.http11.http11response.Http11Response;
import org.apache.coyote.http11.http11response.ResponseManager;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String REDIRECT_WHEN_LOGIN_SUCCESS = "/index.html";
    private static final String REDIRECT_WHEN_LOGIN_FAIL = "/401.html";
    private static final String URI_WITH_EXTENSION = "/login.html";
    private static final String REDIRECT_URI_ALREADY_LOGIN = "/index.html";

    private final UserService userService = new UserService();
    private final SessionManager sessionManager = SessionManager.connect();

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        Map<String, String> queryStringDatas = QueryStringUtil.extractQueryStringDatas(request.getBody());
        if (userService.login(queryStringDatas.get(ACCOUNT_KEY), queryStringDatas.get(PASSWORD_KEY))) {
            sessionManager.addUserInSession(request.getSessionId(), userService.findUser(queryStringDatas.get(ACCOUNT_KEY)));
            ResponseManager.redirectResponseComponent(response, REDIRECT_WHEN_LOGIN_SUCCESS, StatusCode.FOUND);
            return;
        }
        ResponseManager.redirectResponseComponent(response, REDIRECT_WHEN_LOGIN_FAIL, StatusCode.FOUND);
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        if (sessionManager.checkLogin(request.getSessionId())) {
            ResponseManager.redirectResponseComponent(response, REDIRECT_URI_ALREADY_LOGIN, StatusCode.FOUND);
            return;
        }
        ResponseManager.redirectResponseComponent(response, URI_WITH_EXTENSION, StatusCode.FOUND);
    }
}
