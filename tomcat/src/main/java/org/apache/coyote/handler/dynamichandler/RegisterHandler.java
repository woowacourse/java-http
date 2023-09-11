package org.apache.coyote.handler.dynamichandler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.Query;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RegisterHandler extends AbstractHandler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";
    private static final String ACCOUNT = "account";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String body = findStaticPage(DEFAULT_DIRECTORY_PATH + httpRequest.path() + ContentType.TEXT_HTML.extension());

            httpResponse.setStatusLine(httpRequest.httpVersion(), HttpStatus.OK);
            httpResponse.setHeader(HttpHeader.CONTENT_TYPE.value(), ContentType.TEXT_HTML.value());
            httpResponse.setHeader(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(body.getBytes().length));
            httpResponse.setBody(body);
        } catch (IOException e) {
            new ExceptionHandler(HttpStatus.NOT_FOUND).service(httpRequest, httpResponse);
        }
    }

    private String findStaticPage(String path) throws IOException {
        File file = new File(HttpResponse.class
                .getClassLoader()
                .getResource(path)
                .getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Query query = Query.create(httpRequest.body());

        String account = query.get(ACCOUNT);
        String email = query.get(EMAIL);
        String password = query.get(PASSWORD);
        if (isInvalidRegisterFormat(account, email, password)) {
            handleRedirectPage(httpRequest, httpResponse);
            return;
        }

        if (isDuplicateAccount(account)) {
            handleRedirectPage(httpRequest, httpResponse);
            return;
        }
        // success
        InMemoryUserRepository.save(new User(account, password, email));
        handleRedirectPage(httpRequest, httpResponse);
    }

    private boolean isInvalidRegisterFormat(String account, String email, String password) {
        return account == null || email == null || password == null;
    }

    private boolean isDuplicateAccount(String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    private void handleRedirectPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatusLine(httpRequest.httpVersion(), HttpStatus.FOUND);
        httpResponse.setHeader(HttpHeader.LOCATION.value(), "/index.html");
    }
}
