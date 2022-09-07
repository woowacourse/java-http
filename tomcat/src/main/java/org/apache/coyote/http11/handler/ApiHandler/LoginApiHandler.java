package org.apache.coyote.http11.handler.ApiHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.session.Cookie;
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
    public ApiHandlerResponse handle(HttpRequest httpRequest) {
        Map<String, Object> parameters = getParameters(httpRequest.getRequestBody());
        final String account = (String) parameters.get("account");
        final String password = (String) parameters.get("password");

        Map<String, Object> headers = new LinkedHashMap<>();
        Optional<User> user = findUser(account);

        if (user.isEmpty()) {
            return ApiHandlerResponse.of(HttpStatus.UNAUTHORIZED, headers, "", ContentType.HTML);
        }

        User existedUser = user.orElseThrow();

        if (!existedUser.checkPassword(password)) {
            return ApiHandlerResponse.of(HttpStatus.UNAUTHORIZED, headers, "", ContentType.HTML);
        }

        log.info("로그인 성공! 아이디: " + existedUser.getAccount());
        headers.put("Location", "/index.html ");
        headers.put("Set-Cookie", new Cookie(Map.of("JSESSIONID", UUID.randomUUID())));
        return ApiHandlerResponse.of(HttpStatus.FOUND, headers, "", ContentType.HTML);
    }

    private Map<String, Object> getParameters(RequestBody requestBody) {
        String body = requestBody.getBody();
        String[] params = body.split("&");

        HashMap<String, Object> parameters = new HashMap<>();
        for (String param : params) {
            int index = param.indexOf("=");
            String key = param.substring(0, index);
            String value = param.substring(index + 1);

            parameters.put(key, value);
        }
        return parameters;
    }

    private Optional<User> findUser(String account) {
        return InMemoryUserRepository.findByAccount(account);
    }
}


