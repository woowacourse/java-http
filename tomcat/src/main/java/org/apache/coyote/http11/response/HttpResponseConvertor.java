package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CRLF;
import static org.apache.coyote.Constants.CSS;
import static org.apache.coyote.Constants.HTML;
import static org.apache.coyote.Constants.IMG;
import static org.apache.coyote.Constants.JS;
import static org.apache.coyote.Constants.ROOT;
import static org.apache.coyote.http11.response.element.HttpMethod.GET;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;
import org.apache.coyote.http11.utils.UriParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseConvertor {

    private static final Logger log = LoggerFactory.getLogger(HttpResponseConvertor.class);

    private HttpResponse response;


    public HttpResponseConvertor(HttpRequest request) {
        try {
            this.response = parseResponse(request);
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            this.response = HttpResponse.from(HttpResponseBody.of(ROOT + "/404.html"), HttpStatus.NOT_FOUND,
                    "text/html");
        } catch (NoUserException e) {
            log.error(e.getMessage(), e);
            this.response = HttpResponse.from(HttpResponseBody.of(ROOT + "/401.html"), HttpStatus.UNAUTHORIZED,
                    "text/html");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.response = HttpResponse.from(HttpResponseBody.of(ROOT + "/500.html"), HttpStatus.INTERNAL_SERVER_ERROR,
                    "text/html");
        }
    }

    private HttpResponse parseResponse(HttpRequest request) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String path = request.getPath();

        HttpResponse staticResponse = getResponseOfStaticResource(method, path);
        if (staticResponse != null) {
            return staticResponse;
        }

        if (method == GET && List.of("/", "").contains(path)) {
            return HttpResponse.from(new HttpResponseBody("Hello world!"), HttpStatus.OK, "text/html");
        }
        if (method == GET && path.split("\\?")[0].equals("/login")) {
            logIfLogin(path);
            return HttpResponse.from(HttpResponseBody.of(ROOT + "/login.html"), HttpStatus.OK, "text/html");
        }

        throw new NoSuchElementException("해당 페이지를 찾을 수 없습니다: " + path);
    }

    private HttpResponse getResponseOfStaticResource(HttpMethod method, String path) {
        HttpStatus status = HttpStatus.OK;

        if (method == GET && HTML.stream().anyMatch(e -> e.equals(path))) {
            return HttpResponse.from(HttpResponseBody.of(ROOT + path), status, "text/html");
        }
        if (method == GET && JS.stream().anyMatch(e -> e.equals(path))) {
            return HttpResponse.from(HttpResponseBody.of(ROOT + path), status, "text/plain");
        }
        if (method == GET && CSS.stream().anyMatch(e -> e.equals(path))) {
            return HttpResponse.from(HttpResponseBody.of(ROOT + path), status, "text/css");
        }
        if (method == GET && IMG.stream().anyMatch(e -> e.equals(path))) {
            return HttpResponse.from(HttpResponseBody.of(ROOT + path), status, "image/svg+xml");
        }

        return null;
    }

    private void logIfLogin(String uri) {
        if (!uri.contains("?")) {
            return;
        }
        User user = InMemoryUserRepository.findByAccount(new UriParser(uri).find("account"))
                .orElseThrow(NoUserException::new);
        log.info("userLogin: " + CRLF + user.toString() + CRLF);
    }

    public String getHeader() {
        return response.getHeader().getHeaders();
    }

    public String getResponse() {
        return String.join(CRLF, getHeader(), "", response.getBody().getBodyContext());
    }
}
