package org.apache.catalina.controller;

import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.ResponseHeaderType.LOCATION;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.util.Authorizer;
import org.apache.catalina.util.FileLoader;
import org.apache.coyote.http11.response.ResponseContentType;
import org.apache.catalina.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RegisterController extends AbstractController {

    public static final String REGISTER_PAGE = "/register.html";
    public static final String DEFAULT_PAGE = "/index.html";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Session session = Authorizer.findSession(request);
        final HttpRequestBody requestBody = request.getBody();
        final Map<String, String> body = parse(requestBody.getBody());
        final User user = new User(body.get("account"),
                body.get("password"),
                body.get("email"));

        if (session != null || InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
            response.setHttpVersion(request.getHttpVersion())
                    .setStatusCode(HttpStatusCode.FOUND)
                    .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                    .addHeader(LOCATION, DEFAULT_PAGE);
            return;
        }

        InMemoryUserRepository.save(user);

        response.setHttpVersion(request.getHttpVersion())
                .setStatusCode(HttpStatusCode.FOUND)
                .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                .addHeader(LOCATION, DEFAULT_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Session session = Authorizer.findSession(request);

        if (session != null) {
            response.setHttpVersion(request.getHttpVersion())
                    .setStatusCode(HttpStatusCode.FOUND)
                    .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                    .addHeader(LOCATION, DEFAULT_PAGE);
            return;
        }

        final String resource = FileLoader.load(RESOURCE_DIRECTORY + REGISTER_PAGE);

        response.setHttpVersion(request.getHttpVersion())
                .setStatusCode(HttpStatusCode.OK)
                .addHeader(CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                .addHeader(CONTENT_LENGTH, Objects.requireNonNull(resource).getBytes().length)
                .setResponseBody(new HttpResponseBody(resource));
    }

    Map<String, String> parse(final String body) {
        final Map<String, String> requestBody = new HashMap<>();
        Arrays.stream(body.split("&"))
                .forEach(value -> requestBody.put(value.split("=")[0],
                        value.split("=")[1]));
        return requestBody;
    }
}
