package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.QueryString;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.util.QueryParser;
import org.apache.coyote.util.ResourceResolver;

public class RegisterHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            return doGet();
        }
        if (httpMethod == HttpMethod.POST) {
            return doPost(request);
        }
        throw new MethodNotAllowedException(List.of(HttpMethod.GET, HttpMethod.POST));
    }

    private HttpResponse doGet() throws IOException {
        HttpResponse response = new HttpResponse();
        response.setContentBody(ResourceResolver.resolve("/register.html"));
        response.setContentType(HttpContentType.TEXT_HTML);
        response.setHttpStatus(HttpStatus.OK);
        return response;
    }

    private HttpResponse doPost(HttpRequest request) {
        String requestBody = request.getRequestBody();
        QueryString query = QueryParser.parse(requestBody);
        User user = getUser(query);
        InMemoryUserRepository.save(user);
        HttpResponse response = new HttpResponse();
        response.sendRedirect("/index.html");
        return response;
    }

    private User getUser(QueryString query) {
        String account = query.get("account");
        String password = query.get("password");
        String email = query.get("email");
        return new User(account, password, email);
    }
}
