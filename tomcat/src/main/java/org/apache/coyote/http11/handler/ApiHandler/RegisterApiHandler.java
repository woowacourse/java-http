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

public class RegisterApiHandler implements Handler {

    private static final Pattern REGISTER_URI_PATTERN = Pattern.compile("/register");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.POST, REGISTER_URI_PATTERN);
    }

    @Override
    public Object getResponse(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();

        try {
            Map<String, String> parameters = getParameters(requestBody);
            String account = parameters.get("account");
            String password = parameters.get("password");
            String email = parameters.get("email");
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
        } catch (Exception e) {
            return new ApiHandlerResponse(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
        }

        return new ApiHandlerResponse(HttpStatus.FOUND, "/index.html");
    }

    private Map<String, String> getParameters(RequestBody requestBody) {
        String body = requestBody.getBody();
        String[] params = body.split("&");

        HashMap<String, String> parameters = new HashMap<>();
        for (String param : params) {
            String[] keyValue = param.split("=");
            parameters.put(keyValue[0], keyValue[1]);
        }
        return parameters;
    }
}
