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
import nextstep.jwp.http.HttpUri;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.QueryString;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final RequestHandler handler = new LoginHandler();

    private LoginHandler() {
    }

    public static RequestHandler getInstance() {
        return handler;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            HttpUri httpUri = request.getHttpUri();
            if (httpUri.hasQueryString()) {
                QueryString queryString = httpUri.getQueryString();

                if (login(queryString)) {
                    return redirectToIndexPage(request);
                }

                return redirectToUnauthorizedPage(request);
            }

            HttpStatus httpStatus = HttpStatus.OK;
            HttpVersion httpVersion = request.getHttpVersion();
            URL url = getClass().getClassLoader().getResource("static/login.html");
            HttpBody httpBody = HttpBody.from(new String(Files.readAllBytes(Path.of(url.getPath()))));
            HttpHeaders httpHeaders = createHeaders(httpBody);

            return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
        }

        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            HttpBody httpBody = request.getHttpBody();
            QueryString queryString = QueryString.from(httpBody.getHttpBody());

            if (login(queryString)) {
                return redirectToIndexPage(request);
            }

            return redirectToUnauthorizedPage(request);
        }

        throw new NoSuchElementException();
    }

    private HttpResponse redirectToUnauthorizedPage(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = HttpBody.from("");
        HttpHeaders httpHeaders = createHeadersWithLocation(httpBody, "/401.html");

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

    private HttpResponse redirectToIndexPage(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = HttpBody.from("");
        HttpHeaders httpHeaders = createHeadersWithLocation(httpBody, "/index.html");

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

    private boolean login(QueryString queryString) {
        if (queryString.containsKey("account") && queryString.containsKey("password")) {
            User user = InMemoryUserRepository.findByAccount(queryString.get("account"))
                    .orElseThrow(IllegalArgumentException::new);

            if (user.checkPassword(queryString.get("password"))) {
                log.info("로그인 성공 ! 아이디 : {}", user.getAccount());
                return true;
            }
        }

        return false;
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
