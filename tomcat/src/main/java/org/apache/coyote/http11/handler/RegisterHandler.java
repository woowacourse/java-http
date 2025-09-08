package org.apache.coyote.http11.handler;

import com.techcourse.service.UserService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RequestLine;

public class RegisterHandler implements HttpHandler {

    private final HttpResourceHandler httpResourceHandler;
    private final QueryParser queryParser;

    public RegisterHandler(final HttpResourceHandler httpResourceHandler, final QueryParser queryParser) {
        this.httpResourceHandler = httpResourceHandler;
        this.queryParser = queryParser;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws Exception {
        RequestLine requestLine = request.requestLine();
        if (requestLine.method() == HttpMethod.GET) {
            return httpResourceHandler.handle(request);
        }

        String requestBody = new String(request.body());
        Map<String, String> queriesFromBody = queryParser.parse(requestBody);

        String account = queriesFromBody.get("account");
        String email = queriesFromBody.get("email");
        String password = queriesFromBody.get("password");

        UserService.register(account, email, password);

        return redirect("/index.html");
    }

    private HttpResponse redirect(final String path) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", path);

        return new HttpResponse(HttpStatus.FOUND, headers, new byte[0]);
    }
}
