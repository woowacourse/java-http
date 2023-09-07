package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.FOUND;

import java.util.Map;
import nextstep.jwp.service.AuthService;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.StatusLine;
import org.apache.catalina.servlet.util.RequestParamUtil;

public class SignUpRequestHandler implements RequestHandler {

    private final AuthService authService = new AuthService();

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.requestLine().uri().path().equals("/register")
                && request.requestLine().method().equals("POST");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = RequestParamUtil.parse(request.body());
        authService.signUp(formData.get("account"), formData.get("email"), formData.get("password"));
        response.setStatusLine(new StatusLine(FOUND));
        response.addHeader("Location", "/index.html");
    }
}
