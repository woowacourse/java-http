package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.FOUND;

import java.util.Map;
import nextstep.jwp.service.AuthService;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.ResponseHeaders;
import org.apache.catalina.servlet.response.StatusLine;
import org.apache.catalina.servlet.util.RequestParamUtil;

public class SignUpRequestHandler implements RequestHandler {

    private final AuthService authService = new AuthService();

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.startLine().uri().path().equals("/register")
                && request.startLine().method().equals("POST");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> formData = RequestParamUtil.parse(request.body());
        authService.signUp(formData.get("account"), formData.get("email"), formData.get("password"));
        StatusLine statusLine = new StatusLine(FOUND);
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.put("Location", "/index.html");
        return new HttpResponse(statusLine, responseHeaders, null);
    }
}
