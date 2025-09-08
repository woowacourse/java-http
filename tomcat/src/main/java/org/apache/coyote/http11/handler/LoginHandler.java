package org.apache.coyote.http11.handler;

import com.techcourse.service.UserService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class LoginHandler implements HttpHandler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        Map<String, String> queries = request.queries();
        String account = queries.get("account");
        String password = queries.get("password");

        UserService.login(account, password);
        return redirect("/index.html");
    }

    private HttpResponse redirect(final String path) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", path);

        return new HttpResponse(HttpStatus.FOUND, headers, new byte[0]);
    }
}
