package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.QueryParameters;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        QueryParameters queryParameters = new QueryParameters(request.getRequestBody());
        Map<String, String> mappedQuery = queryParameters.getValue();

        User user = new User(mappedQuery.get("account"), mappedQuery.get("password"), mappedQuery.get("email"));
        InMemoryUserRepository.save(user);

        createIndexPageResponse(response);
    }

    private void createIndexPageResponse(HttpResponse response) {
        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBodyFromFile("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBodyFromFile(request.getPath().concat("." + ContentType.HTML.getExtension()));
    }
}
