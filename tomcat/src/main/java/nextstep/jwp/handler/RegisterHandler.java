package nextstep.jwp.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.QueryString;
import nextstep.jwp.model.User;

public class RegisterHandler implements RequestHandler {

    private static final RequestHandler handler = new RegisterHandler();

    private RegisterHandler() {
    }

    public static RequestHandler getInstance() {
        return handler;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpMethod httpMethod = request.getHttpMethod();

        if (httpMethod.equals(HttpMethod.GET)) {
            HttpStatus httpStatus = HttpStatus.OK;
            HttpVersion httpVersion = request.getHttpVersion();
            URL url = getClass().getClassLoader().getResource("static/register.html");
            HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
            HttpHeaders httpHeaders = createHeaders(httpBody);

            return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
        }

        if (httpMethod.equals(HttpMethod.POST)) {
            HttpStatus httpStatus = HttpStatus.FOUND;
            HttpVersion httpVersion = request.getHttpVersion();
            HttpBody httpBody = request.getHttpBody();
            HttpHeaders httpHeaders = createHeadersWithLocation(httpBody, "/index.html");

            join(httpBody);

            return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
        }

        throw new NoSuchElementException();
    }

    private void join(HttpBody httpBody) {
        QueryString queryString = QueryString.from(httpBody.getHttpBody());

        if (queryString.containsKey("account") &&
                queryString.containsKey("password") &&
                queryString.containsKey("email")) {
            User user = new User(queryString.get("account"), queryString.get("password"), queryString.get("email"));

            InMemoryUserRepository.save(user);

            return;
        }

        throw new IllegalArgumentException();
    }

    private HttpHeaders createHeaders(HttpBody httpBody) {
        List<String> headers = List.of(
                "Content-Type: text/html;charset=utf-8",
                "ContentLength: " + httpBody.getBytesLength()
        );

        return HttpHeaders.from(headers);
    }

    private HttpHeaders createHeadersWithLocation(HttpBody httpBody, String location) {
        List<String> headers = List.of(
                "Content-Type: text/html;charset=utf-8",
                "ContentLength: " + httpBody.getBytesLength(),
                "Location: " + location
        );

        return HttpHeaders.from(headers);
    }
}
