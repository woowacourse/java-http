package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.coyote.http11.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

public class RegisterController extends Controller {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final Map<String, Set<String>> requestType = new HashMap<>(Map.of(
                "/register", new HashSet<>(Set.of("GET", "POST"))
        ));

        if (requestType.containsKey(httpRequest.getTarget())) {
            final Set<String> methodType = requestType.get(httpRequest.getTarget());
            return methodType.contains(httpRequest.getMethod());
        }
        return false;
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final String resourceUrl = "register.html";
        String contentType = "text/html;charset=utf-8";

        final String acceptHeader = httpRequest.getHeaders().getHeaderValue(ACCEPT);
        if (acceptHeader != null) {
            contentType = acceptHeader.split(",")[0];
        }

        URL resource = getClass().getClassLoader().getResource("static/" + resourceUrl);
        if (resource != null) {
            httpResponse.setStatusCode(OK);
        } else {
            resource = getClass().getClassLoader().getResource("static/" + "404.html");
            httpResponse.setStatusCode(NOT_FOUND);
            contentType = "text/html;charset=utf-8";
        }

        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        httpResponse.addHeader(CONTENT_TYPE, contentType);
        httpResponse.setBody(responseBody);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final User newUser = new User(
                httpRequest.getBody().get("account"),
                httpRequest.getBody().get("password"),
                httpRequest.getBody().get("email")
        );
        InMemoryUserRepository.save(newUser);
        httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponse.addHeader(LOCATION, "/index.html");
        httpResponse.setStatusCode(FOUND);
    }
}
