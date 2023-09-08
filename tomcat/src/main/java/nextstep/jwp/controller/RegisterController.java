package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.http.ContentType;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ViewResolver;
import org.apache.http.annotation.Controller;
import org.apache.http.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public final class RegisterController {

    @RequestMapping(method = "POST", path = "/register")
    public void register(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        InMemoryUserRepository.save(new User(httpRequest.getRequestBody().get("account"), httpRequest.getRequestBody().get("password"), httpRequest.getRequestBody().get("email")));
        httpResponse.sendRedirect("/index.html");
    }

    @RequestMapping(method = "GET", path = "/register")
    public void registerView(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.setHttpStatus(HttpStatus.OK);
        final ViewResolver viewResolver = new ViewResolver(Path.of(httpRequest.getRequestURI()));
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
        httpResponse.write(Files.readString(viewResolver.getResourcePath()));
    }
}
