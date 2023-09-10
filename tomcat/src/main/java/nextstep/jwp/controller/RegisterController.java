package nextstep.jwp.controller;

import nextstep.jwp.FileFinder;
import nextstep.jwp.FormData;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        FormData formData = new FormData(request.getBody());
        InMemoryUserRepository.save(new User(
            formData.getValue("account"),
            formData.getValue("password"),
            formData.getValue("email")
        ));
        response.toRedirect("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK);
        response.setBody(FileFinder.getFileContent("/register.html"));
    }
}
