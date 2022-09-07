package org.apache.coyote.http11.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class RegisterHandler implements Handler {

    private static final String REGISTER_RESOURCE_PATH = "/register.html";
    private static final String INDEX_RESOURCE_PATH = "/index.html";
    private static final String NOT_FOUND_RESOURCE_PATH = "/404.html";

    private final HttpRequest httpRequest;

    public RegisterHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        HttpResponse response = createHttpResponse();
        return response.getResponse();
    }

    private HttpResponse createHttpResponse() {
        if (httpRequest.matchRequestMethod(Method.GET)) {
            return doGet();
        }
        if (httpRequest.matchRequestMethod(Method.POST)) {
            return doPost();
        }
        HttpResponse response = HttpResponse.of(ResponseStatusCode.FOUND, httpRequest.getVersion(), ContentType.HTML);
        response.addLocationHeader(NOT_FOUND_RESOURCE_PATH);
        return response;
    }

    private HttpResponse doGet() {
        return HttpResponse.of(ResponseStatusCode.OK, httpRequest.getVersion(), ContentType.HTML,
                FileReader.getFile(REGISTER_RESOURCE_PATH, getClass()));
    }

    private HttpResponse doPost() {
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
