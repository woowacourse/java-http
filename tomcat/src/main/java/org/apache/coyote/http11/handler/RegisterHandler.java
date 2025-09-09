package org.apache.coyote.http11.handler;

import com.techcourse.service.UserService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResourceLoader;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParser;
import org.apache.coyote.http11.RequestLine;

public class RegisterHandler implements HttpHandler {

    private final HttpResourceLoader httpResourceLoader;
    private final QueryParser queryParser;

    public RegisterHandler(final HttpResourceLoader httpResourceLoader, final QueryParser queryParser) {
        this.httpResourceLoader = httpResourceLoader;
        this.queryParser = queryParser;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws Exception {
        RequestLine requestLine = request.requestLine();
        if (requestLine.method() == HttpMethod.GET) {
            return httpResourceLoader.load(request.path());
        }

        String requestBody = new String(request.body());
        Map<String, String> queriesFromBody = queryParser.parse(requestBody);

        String account = queriesFromBody.get("account");
        String email = queriesFromBody.get("email");
        String password = queriesFromBody.get("password");

        UserService.register(account, email, password);

        return HttpResponse.redirect("/index.html", new LinkedHashMap<>());
    }
}
