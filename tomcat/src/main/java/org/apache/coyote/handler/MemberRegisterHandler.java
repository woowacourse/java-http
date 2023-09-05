package org.apache.coyote.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.ResponseInfo;
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
    public ResponseInfo doService(HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getRequestLine().getHttpMethod();

        if (httpMethod.equals("POST")) {
            return doPost(httpRequest);
        }

        final String resourcePath = RESOURCE_PATH + "/register.html";
        return new ResponseInfo(classLoader.getResource(resourcePath), 200, "OK");
    }

    @Override
    ResponseInfo doGet(HttpRequest httpRequest) {
        return null;
    }

    @Override
    ResponseInfo doPost(final HttpRequest httpRequest) {
        final RequestBody body = httpRequest.getRequestBody();

        final String account = body.getByKey(ACCOUNT);
        final String password = body.getByKey(PASSWORD);
        final String email = body.getByKey(EMAIL);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("User create - {}", user);

        String resourcePath = RESOURCE_PATH + INDEX_PAGE;
        return new ResponseInfo(classLoader.getResource(resourcePath), 302, HTTP_FOUND);
    }
}
