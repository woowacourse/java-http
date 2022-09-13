package nextstep.jwp.ui;

import nextstep.jwp.application.UserService;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestParams;
import org.apache.coyote.http11.request.mapping.controller.Controller;
import org.apache.coyote.http11.request.mapping.controllerscan.ControllerScan;
import org.apache.coyote.http11.request.mapping.controllerscan.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.RedirectResponse;

@ControllerScan
public class UserRegisterController implements Controller {

    private final UserService userService = UserService.getInstance();

    @Override
    @RequestMapping(method = HttpMethod.POST, uri = "/register")
    public HttpResponse handle(final HttpRequest httpRequest) {
        final RequestParams requestParams = httpRequest.getRequestParams();
        try {
            final String account = getAccountFromRequestParam(requestParams, "account", "account 파라미터가 존재하지 않습니다.");
            final String password = getAccountFromRequestParam(requestParams, "password", "password 파라미터가 존재하지 않습니다.");
            final String email = getAccountFromRequestParam(requestParams, "email", "email 파라미터가 존재하지 않습니다.");

            userService.save(account, password, email);
            return RedirectResponse.of("/index.html");
        } catch (final BadRequestException e) {
            return RedirectResponse.of("/400.html");
        }
    }

    private String getAccountFromRequestParam(final RequestParams requestParams,
                                              final String key,
                                              final String exceptionMessage) {
        return requestParams.getValue(key)
                .orElseThrow(() -> new BadRequestException(exceptionMessage));
    }
}
