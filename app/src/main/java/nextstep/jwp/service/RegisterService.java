package nextstep.jwp.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.Params.BodyParams;
import nextstep.jwp.model.User;

public class RegisterService {

    public void registerUser(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        BodyParams bodyParams = parseBodyData(httpRequest, new HashMap<>());
        if (InMemoryUserRepository.isExistAccount(bodyParams.get("account"))) {
            httpResponse.redirect302Transfer("/register.html");
            return;
        }
        User user = new User(bodyParams.get("account"), bodyParams.get("password"), bodyParams.get("email"));
        InMemoryUserRepository.save(user);
        httpResponse.transfer("/index.html");
    }


    private BodyParams parseBodyData(final HttpRequest httpRequest, final Map<String, String> bodyType) {
        BodyParams bodyParams = new BodyParams(bodyType);
        String[] parseRequestBody = parseRequestBody(httpRequest.getBody());

        for (String bodyData : parseRequestBody) {
            String[] parseBodyData = bodyData.split("=");
            bodyParams.put(parseBodyData[0], parseBodyData[1]);
        }
        return bodyParams;
    }

    private String[] parseRequestBody(final String body) {
        return body.split("&");
    }
}
