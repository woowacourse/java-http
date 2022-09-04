package org.apache.coyote.http11.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseLine;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class RegisterHandler implements Handler{

    private static final String REGISTER_RESOURCE_PATH = "/register.html";
    private static final String INDEX_RESOURCE_PATH = "/index.html";
    private static final String NOT_FOUND_RESOURCE_PATH = "/404.html";

    private final HttpRequest httpRequest;

    public RegisterHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        if (httpRequest.matchRequestMethod(Method.GET)) {
            return createHttpResponse(ResponseStatusCode.OK, FileReader.getFile(REGISTER_RESOURCE_PATH, getClass()))
                    .getResponse();
        }
        if (httpRequest.matchRequestMethod(Method.POST)) {
            saveUser(httpRequest);
            return createHttpResponse(ResponseStatusCode.OK, FileReader.getFile(INDEX_RESOURCE_PATH, getClass()))
                    .getResponse();
        }
        return createHttpResponse(ResponseStatusCode.NOT_FOUND, FileReader.getFile(NOT_FOUND_RESOURCE_PATH, getClass()))
                .getResponse();
    }

    private void saveUser(final HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.getBody();
        User user = new User(body.get("account"), body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);
    }

    private HttpResponse createHttpResponse(final ResponseStatusCode statusCode, String resourcePath) {
        ResponseLine responseLine = ResponseLine.of(statusCode);
        return HttpResponse.of(responseLine, ContentType.HTML, resourcePath);
    }
}
