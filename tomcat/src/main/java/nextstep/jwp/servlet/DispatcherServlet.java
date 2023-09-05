package nextstep.jwp.servlet;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.servlet.Controller;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DispatcherServlet implements Servlet {

    private static Map<String, Controller> container = new HashMap<>();

    static {
        container.put("/", new HomeController());
        container.put("/login", new LoginController());
        container.put("/register", new RegisterController());
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String requestUri = httpRequest.getRequestUri();
        if (isStaticResource(httpResponse, requestUri)) {
            return;
        }
        Controller controller = container.get(requestUri);
        try {
            controller.service(httpRequest, httpResponse);
        } catch (IllegalAccessException e) {

        } catch (Exception e) {

        }
    }

    private boolean isStaticResource(HttpResponse httpResponse, String requestUri) throws IOException {
        if (container.containsKey(requestUri)) {
            return false;
        }
        StaticResource staticResource = StaticResource.of(requestUri);
        ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
        httpResponse.setResponseBody(responseBody);
        return true;
    }
}
