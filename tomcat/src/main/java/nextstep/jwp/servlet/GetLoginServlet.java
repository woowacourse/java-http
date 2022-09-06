package nextstep.jwp.servlet;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.Servlet;

public class GetLoginServlet implements Servlet {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.FOUND)
                .setLocationAsHome();
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        return httpRequest.isLoginPage() && httpRequest.alreadyLogin();
    }
}
