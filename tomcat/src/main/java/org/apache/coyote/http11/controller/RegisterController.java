package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.QueryMapper;

public class RegisterController extends AbstractController{

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getRequestBody().getBody();
        QueryMapper queryMapper = new QueryMapper(body);
        Map<String, String> parameters = queryMapper.getParameters();

        User user = new User(parameters.get("account"), parameters.get("password"), parameters.get("email"));
        InMemoryUserRepository.save(user);

        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBodyFromFile("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBodyFromFile(request.getPath().concat("." + ContentType.HTML.getExtension()));
    }
}
