package org.apache.controller.path;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.request.HttpRequest;
import org.apache.request.RequestLine;
import org.apache.request.RequestUri;
import org.apache.response.HttpResponse;
import org.apache.util.QueryStringParser;

public class RegisterController implements PathController{

    private static RegisterController registerController = new RegisterController();

    private String path = "/register";

    public static RegisterController getInstance() {
        return registerController;
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
        return path;
    }
}
