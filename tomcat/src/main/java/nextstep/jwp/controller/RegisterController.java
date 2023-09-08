package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResourceResponseHandler;

import static org.apache.coyote.http11.HttpStatus.FOUND;

public class RegisterController extends AbstractController {

    private final ResourceResponseHandler resourceResponseHandler = new ResourceResponseHandler(); // todo: 지우기

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        doGet(request, response);

    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        final var email = form.get("email");
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        response.setStatus(FOUND);
        response.sendRedirect("/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final var responseBody = resourceResponseHandler.buildBodyFrom("/register.html");
        response.setStatus(HttpStatus.OK);
        response.setBody(responseBody);
    }

}
