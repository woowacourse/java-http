package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidRequestMethod;
import nextstep.jwp.http.FormData;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.model.User;

public class RegisterHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpMethod httpMethod = request.getHttpMethod();

        if (httpMethod.equals(HttpMethod.GET)) {
            return handleGetMethod(request);
        }

        if (httpMethod.equals(HttpMethod.POST)) {
            return handlePostMethod(request);
        }

        throw new InvalidRequestMethod();
    }

    private HttpResponse handlePostMethod(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = request.getHttpBody();
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);
        httpHeaders.setLocation("/index.html");

        join(httpBody);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

    private void join(HttpBody httpBody) {
        FormData formData = FormData.from(httpBody);

        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private HttpResponse handleGetMethod(HttpRequest request) throws IOException {
        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        URL url = getClass().getClassLoader().getResource("static/register.html");
        HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

}
