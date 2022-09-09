package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    public HttpResponse service(HttpRequest request) {
        RequestLine requestLine = request.getRequestLine();
        Method method = requestLine.getMethod();

        if (method.isPost()) {
            return doPost(request);
        }
        if (method.isGet()) {
            return doGet(request);
        }

        return doNotFoundRequest();
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.ok()
                .addResponseBody(View.REGISTER.getContents(), ContentType.TEXT_HTML_CHARSET_UTF_8);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getRequestBody().getValue("account");
        String password = request.getRequestBody().getValue("password");
        String email = request.getRequestBody().getValue("email");

        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponse.redirect()
                .addLocation(View.INDEX.getViewFileName());
    }
}
