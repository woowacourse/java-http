package org.apache.coyote.handler.dynamichandler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.Query;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String DEFAULT_DIRECTORY_PATH = "static";
    private static final String ACCOUNT = "account";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (HttpMethod.GET == httpRequest.httpMethod()) {
            return handleGetMapping(httpRequest);
        }
        return handlePostMapping(httpRequest);
    }

    private HttpResponse handleGetMapping(HttpRequest httpRequest) {
        try {
            return HttpResponse.createStaticResponseByPath(
                    httpRequest.httpVersion(),
                    HttpStatus.OK,
                    DEFAULT_DIRECTORY_PATH + httpRequest.path() + ContentType.TEXT_HTML.extension());
        } catch (IOException e) {
            return new ExceptionHandler(HttpStatus.NOT_FOUND).handle(httpRequest);
        }
    }

    private HttpResponse handlePostMapping(HttpRequest httpRequest) {
        Query query = Query.create(httpRequest.body());
        if (query.isEmpty()) {
            handleRedirectPage(httpRequest);
        }

        String account = query.get(ACCOUNT);
        String email = query.get(EMAIL);
        String password = query.get(PASSWORD);
        // success
        if (account != null && email != null && password != null && !isDuplicateAccount(account)) {
            InMemoryUserRepository.save(new User(account, password, email));
            return handleRedirectPage(httpRequest);
        }
        return handleRedirectPage(httpRequest);
    }

    private boolean isDuplicateAccount(String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    private HttpResponse handleRedirectPage(HttpRequest httpRequest) {
        StatusLine statusLine = new StatusLine(httpRequest.httpVersion(), HttpStatus.FOUND);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeader.LOCATION.value(), "/index.html");

        return new HttpResponse(statusLine, new Header(headers));
    }
}
