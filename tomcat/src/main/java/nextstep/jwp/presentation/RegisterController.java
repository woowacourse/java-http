package nextstep.jwp.presentation;

import java.util.Map;
import nextstep.jwp.model.user.UserService;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11request.QueryStringUtil;
import org.apache.coyote.http11.http11response.Http11Response;
import org.apache.coyote.http11.http11response.ResponseManager;

public class RegisterController extends AbstractController {

    private static final String REDIRECT_URI = "/register.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String REDIRECT_WHEN_REGISTER_SUCCESS = "/index.html";
    private static final String REDIRECT_WHEN_REGISTER_FAIL = "/500.html";

    private final UserService userService = new UserService();

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        Map<String, String> queryStringDatas = QueryStringUtil.extractQueryStringDatas(request.getBody());
        if (userService.addNewUser(queryStringDatas.get(ACCOUNT_KEY), queryStringDatas.get(EMAIL_KEY), queryStringDatas.get(PASSWORD_KEY))) {
            ResponseManager.redirectResponseComponent(response, REDIRECT_WHEN_REGISTER_SUCCESS, StatusCode.FOUND);
            return;
        }
        ResponseManager.redirectResponseComponent(response, REDIRECT_WHEN_REGISTER_FAIL, StatusCode.FOUND);
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        ResponseManager.redirectResponseComponent(response, REDIRECT_URI, StatusCode.FOUND);
    }
}
