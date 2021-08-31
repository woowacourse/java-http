package nextstep.project.presentation;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.dispatcher.handler.HttpHandler;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.HttpStatus;
import nextstep.project.db.InMemoryUserRepository;
import nextstep.project.model.User;

public class RegisterController extends HttpHandler {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doGet(httpRequest, httpResponse);
        renderPage("./static/register.html", HttpStatus.OK, httpResponse);
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doPost(httpRequest, httpResponse);
        Map<String, String> body = parseFormData(httpRequest.getBody());

        String requestAccount = body.get("account");
        String requestPassword = body.get("password");
        String requestEmail = body.get("email");

        User user = new User(0L, requestAccount, requestPassword, requestEmail);
        InMemoryUserRepository.save(user);

        redirectTo("index.html", httpResponse);
    }

    private Map<String, String> parseFormData(String requestBody) {
        Map<String, String> requestBodyAsMap = new HashMap<>();
        String[] splittedBody = requestBody.split("&");
        for (String q : splittedBody) {
            String[] keyValue = q.split("=");
            if (2 == keyValue.length) {
                requestBodyAsMap.put(keyValue[0], keyValue[1]);
            }
        }

        return requestBodyAsMap;
    }
}
