package nextstep.jwp.servlet;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private static final Map<String, Controller> container = new HashMap<>();

    static {
        container.put("/", new HomeController());
        container.put("/login", new LoginController());
        container.put("/register", new RegisterController());
    }

    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        String requestUri = httpRequest.getRequestUri();
        if (isStaticResource(httpResponse, requestUri) && requestUri.contains(".")) {
            return;
        }
        Controller controller = container.get(requestUri);
        controller.service(httpRequest, httpResponse);
    }

    private boolean isStaticResource(HttpResponse httpResponse, String requestUri) {
        if (container.containsKey(requestUri)) {
            return false;
        }
        StaticResource staticResource = StaticResource.of(requestUri);
        ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
        httpResponse.setResponseBody(responseBody);
        return true;
    }

}
