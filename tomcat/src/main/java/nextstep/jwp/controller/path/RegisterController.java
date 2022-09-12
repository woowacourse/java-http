package nextstep.jwp.controller.path;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import web.request.HttpRequest;
import web.request.RequestUri;
import web.response.HttpResponse;
import web.response.HttpResponseSetter;
import web.util.QueryStringParser;

public class RegisterController extends PathController {

    private static RegisterController instance = new RegisterController();

    public static RegisterController getInstance() {
        if (instance == null) {
            instance = new RegisterController();
        }
        return instance;
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String method = httpRequest.getRequestLine().getMethod();
        if (method.equals("GET")) {
            HttpResponseSetter.setStaticResource(httpResponse, new RequestUri("/register.html"));
        }
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String method = httpRequest.getRequestLine().getMethod();
        String body = httpRequest.getBody();
        if (method.equals("POST")) {
            Map<String, String> queryString = QueryStringParser.parseQueryString(body);
            User user = new User(queryString.get("account"), queryString.get("password"), queryString.get("email"));
            InMemoryUserRepository.save(user);
            HttpResponseSetter.set302Redirect(httpResponse, "http://localhost:8080/index.html");
        }
    }

    @Override
    public String getPath() {
        return "/register";
    }
}
