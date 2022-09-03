package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CRLF;
import static org.apache.coyote.http11.response.element.HttpMethod.GET;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.NoUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;
import org.apache.coyote.http11.response.factory.StaticResponseFactory;
import org.apache.coyote.http11.utils.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseMapper {

    private static final Logger LOG = LoggerFactory.getLogger(HttpResponseMapper.class);
    private static final StaticResponseFactory STATIC_RESPONSE_FACTORY = new StaticResponseFactory();

    private HttpResponse response;

    public HttpResponseMapper(HttpRequest request) {
        try {
            this.response = parseResponse(request);
        } catch (NoSuchElementException e) {
            LOG.error(e.getMessage(), e);
            this.response = STATIC_RESPONSE_FACTORY.getResponse(GET, "/404.html", HttpStatus.NOT_FOUND);
        } catch (NoUserException | InvalidPasswordException e) {
            LOG.error(e.getMessage(), e);
            this.response = STATIC_RESPONSE_FACTORY.getResponse(GET, "/401.html", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            this.response = STATIC_RESPONSE_FACTORY.getResponse(GET, "/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpResponse parseResponse(HttpRequest request) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String path = request.getPath();

        if (method == GET && List.of("/", "").contains(path)) {
            return STATIC_RESPONSE_FACTORY.getResponse(method, "/welcome.html");
        }
        if (method == GET && path.split("\\?")[0].equals("/login")) {
            logIfLogin(path);
            return STATIC_RESPONSE_FACTORY.getResponse(method, "/login.html");
        }
        return STATIC_RESPONSE_FACTORY.getResponse(method, path);
    }

    private void logIfLogin(String uri) {
        if (!uri.contains("?")) {
            return;
        }
        Query query = new Query(uri);
        User user = InMemoryUserRepository.findByAccount(query.find("account"))
                .orElseThrow(NoUserException::new);
        if (!user.checkPassword(query.find("password"))) {
            throw new InvalidPasswordException();
        }
        LOG.info("userLogin: " + CRLF + user + CRLF);
    }

    public String getHeader() {
        return response.getHeader().getHeaders();
    }

    public String getResponse() {
        return String.join(CRLF, getHeader(), "", response.getBody().getBodyContext());
    }
}
