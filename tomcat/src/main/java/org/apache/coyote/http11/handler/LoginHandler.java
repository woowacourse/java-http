package org.apache.coyote.http11.handler;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResourceLoader;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParser;

public class LoginHandler implements HttpHandler {

    private final HttpResourceLoader httpResourceLoader;
    private final QueryParser queryParser;

    public LoginHandler(final HttpResourceLoader httpResourceLoader, final QueryParser queryParser) {
        this.httpResourceLoader = httpResourceLoader;
        this.queryParser = queryParser;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws Exception {
        if (request.requestLine().method() == HttpMethod.GET) {
            return httpResourceLoader.load(request.path());
        }

        String requestBody = new String(request.body());
        Map<String, String> queriesFromBody = queryParser.parse(requestBody);

        String account = queriesFromBody.get("account");
        String password = queriesFromBody.get("password");

        UserService.login(account, password);

        return HttpResponse.redirect("/index.html");
    }
}
