package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;
import org.apache.coyote.http.SessionManager;

import java.io.IOException;
import java.util.Map;

public class GetLoginController implements Controller {

    private static final String SESSION_ID = "JSESSIONID";

    @Override
    public String process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> cookies = httpRequest.findCookies();
        if (cookies.containsKey(SESSION_ID) && SessionManager.isAlreadyLogin(cookies.get(SESSION_ID))) {
            return HttpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, httpResponse, "/index.html");
        }
        return HttpResponseBuilder.buildStaticFileOkResponse(httpRequest, httpResponse, "/login.html");
    }
}
