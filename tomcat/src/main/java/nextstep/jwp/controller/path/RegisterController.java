package nextstep.jwp.controller.path;

import java.util.Map;
import nextstep.jwp.controller.FrontController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import web.request.HttpRequest;
import web.request.RequestLine;
import web.request.RequestUri;
import web.response.HttpResponse;
import web.util.QueryStringParser;

public class RegisterController implements PathController {

    private static RegisterController instance = new RegisterController();

    public static RegisterController getInstance() {
        if (instance == null) {
            instance = new RegisterController();
        }
        return instance;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();
        String method = requestLine.getMethod();
        String body = httpRequest.getBody();

        if (method.equals("GET")) {
            httpResponse.setStaticResource(new RequestUri("/register.html"));
        }

        if (method.equals("POST")) {
            Map<String, String> queryString = QueryStringParser.parseQueryString(body);
            User user = new User(queryString.get("account"), queryString.get("password"), queryString.get("email"));
            InMemoryUserRepository.save(user);
            httpResponse.set302Redirect("http://localhost:8080/index.html");
        }
    }

    @Override
    public String getPath() {
        return "/register";
    }
}
