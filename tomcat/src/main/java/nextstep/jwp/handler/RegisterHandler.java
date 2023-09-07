package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidRequestMethodException;
import nextstep.jwp.http.FormData;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpStatusLine;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.model.User;

public class RegisterHandler implements RequestHandler {

    private static final String RESOURCE_PATH = "static/register.html";
    private static final String INDEX_URI = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpMethod httpMethod = request.getHttpMethod();

        if (httpMethod.equals(HttpMethod.GET)) {
            return handleGetMethod(request);
        }

        if (httpMethod.equals(HttpMethod.POST)) {
            return handlePostMethod(request);
        }

        throw new InvalidRequestMethodException("지원하지 않는 메서드입니다.");
    }

    private HttpResponse handlePostMethod(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpStatusLine httpStatusLine = new HttpStatusLine(httpVersion, httpStatus);
        HttpBody httpBody = request.getHttpBody();
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);
        httpHeaders.setLocation(INDEX_URI);

        join(httpBody);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

    private void join(HttpBody httpBody) {
        FormData formData = FormData.from(httpBody);

        String account = formData.get(ACCOUNT);
        String password = formData.get(PASSWORD);
        String email = formData.get(EMAIL);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private HttpResponse handleGetMethod(HttpRequest request) throws IOException {
        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpStatusLine httpStatusLine = new HttpStatusLine(httpVersion, httpStatus);
        URL url = getClass().getClassLoader().getResource(RESOURCE_PATH);
        HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

}
