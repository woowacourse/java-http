package org.apache.coyote.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberRegisterHandler extends RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(MemberRegisterHandler.class);
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    public MemberRegisterHandler(String mappingUri) {
        this.mappingUri = mappingUri;
    }

    @Override
    public Http11Response doService(HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getRequestLine().getHttpMethod();

        if (httpMethod.equals("POST")) {
            return doPost(httpRequest);
        }

        if (httpMethod.equals("GET")) {
            return doGet(httpRequest);
        }

        return redirectNotFoundPage();
    }

    @Override
    Http11Response doGet(HttpRequest httpRequest) {
        return registerPage();
    }

    @Override
    Http11Response doPost(final HttpRequest httpRequest) {
        final RequestBody body = httpRequest.getRequestBody();

        final String account = body.getByKey(ACCOUNT);
        final String password = body.getByKey(PASSWORD);
        final String email = body.getByKey(EMAIL);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("User create - {}", user);

        return redirectIndexPage();
    }

    private Http11Response redirectNotFoundPage() {
        final String resourcePath = RESOURCE_PATH + NOT_FOUND_PAGE;
        return new Http11Response(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
    }

    private Http11Response redirectIndexPage() {
        String resourcePath = RESOURCE_PATH + INDEX_PAGE;
        return new Http11Response(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
    }

    private Http11Response registerPage() {
        final String resourcePath = RESOURCE_PATH + "/register.html";
        return new Http11Response(classLoader.getResource(resourcePath), 200, "OK");
    }
}
