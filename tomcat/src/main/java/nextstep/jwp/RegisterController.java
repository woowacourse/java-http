package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.request.spec.HttpHeaders;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;

public class RegisterController extends AbstractController {

    public RegisterController(ResourceLocator resourceLocator) {
        super(resourceLocator);
    }

    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isPathEqualTo("/register");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPathString() + ".html";
        doHtmlResponse(response, path);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HttpHeaders headers = request.getHeaders();
        String contentType = headers.get("Content-Type");
        if (!contentType.equals("application/x-www-form-urlencoded")) {
            response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        String body = request.getBody();
        Map<String, String> data = new HashMap<>();
        String[] components = body.split("&");
        for (String component : components) {
            String[] keyVal = component.split("=");
            String key = keyVal[0];
            String value = keyVal[1];
            data.put(key, value);
        }

        String account = data.get("account");
        String password = data.get("password");
        String email = data.get("email");

        LoginService loginService = new LoginService();
        loginService.register(account, password, email);

        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }
}
