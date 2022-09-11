package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.QueryParameters;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        QueryParameters queryParameters = new QueryParameters(request.getRequestBody());

        User user = new User(
                queryParameters.find("account"),
                queryParameters.find("password"),
                queryParameters.find("email"));
        InMemoryUserRepository.save(user);

        createIndexPageResponse(response);
    }

    private void createIndexPageResponse(HttpResponse response) {
        response.addStatusLine(HttpStatus.FOUND.getStatusCodeAndMessage());
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addHeader("Location: /index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.addStatusLine(HttpStatus.OK.getStatusCodeAndMessage());
        response.addContentTypeHeader(ContentType.HTML.getContentType());
        response.addBodyFromFile(request.getPath().concat("." + ContentType.HTML.getExtension()));
    }
}
