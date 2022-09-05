package org.apache.coyote.http11.handler.ApiHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginApiHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginApiHandler.class);

    private static final Pattern LOGIN_URI_PATTERN = Pattern.compile("/login");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.POST, LOGIN_URI_PATTERN);
    }

    @Override
    public ApiHandlerResponse getResponse(HttpRequest httpRequest) {
        Map<String, String> parameters = getParameters(httpRequest.getRequestBody());
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        final User user = findUser(account);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return new ApiHandlerResponse(HttpStatus.FOUND, "/index.html");
        }

        return new ApiHandlerResponse(HttpStatus.UNAUTHORIZED, "/401.html");
    }

    private Map<String, String> getParameters(RequestBody requestBody) {
        String body = requestBody.getBody();
        String[] params = body.split("&");

        HashMap<String, String> parameters = new HashMap<>();
        for (String param : params) {
            int index = param.indexOf("=");
            String key = param.substring(0, index);
            String value = param.substring(index + 1);

            parameters.put(key, value);
        }
        return parameters;
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 account 입니다."));
    }
}


