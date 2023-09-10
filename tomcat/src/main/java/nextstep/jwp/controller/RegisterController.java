package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.FileFinder;
import org.apache.coyote.http11.FormData;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

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
