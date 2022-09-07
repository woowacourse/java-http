package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.controller.support.FileReader;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class RegisterController extends AbstractController {

    private static final String REGISTER_RESOURCE_PATH = "/register.html";
    private static final String INDEX_RESOURCE_PATH = "/index.html";
    private static final String NOT_FOUND_RESOURCE_PATH = "/404.html";

    private final HttpRequest httpRequest;

    public RegisterController(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.matchRequestMethod(Method.GET)) {
            return doGet(httpRequest);
        }
        if (httpRequest.matchRequestMethod(Method.POST)) {
            return doPost(httpRequest);
        }
        HttpResponse response = HttpResponse.of(ResponseStatusCode.FOUND, httpRequest.getVersion(), ContentType.HTML);
        response.addLocationHeader(NOT_FOUND_RESOURCE_PATH);
        return response;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.of(ResponseStatusCode.OK, httpRequest.getVersion(), ContentType.HTML,
                FileReader.getFile(REGISTER_RESOURCE_PATH, getClass()));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        saveUser(httpRequest.getBody());
        HttpResponse response = HttpResponse.of(ResponseStatusCode.FOUND, httpRequest.getVersion(), ContentType.HTML);
        response.addLocationHeader(INDEX_RESOURCE_PATH);
        return response;
    }

    private void saveUser(final Map<String, String> body) {
        User user = new User(body.get("account"), body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);
    }
}
