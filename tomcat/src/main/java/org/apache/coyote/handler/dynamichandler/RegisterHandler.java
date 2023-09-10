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

            httpResponse.setStatus(HttpStatus.OK);
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
        if (query.isEmpty()) {
            handleRedirectPage(httpRequest, httpResponse);
        }

        String account = query.get(ACCOUNT);
        String email = query.get(EMAIL);
        String password = query.get(PASSWORD);
        // success
        if (account != null && email != null && password != null && !isDuplicateAccount(account)) {
            InMemoryUserRepository.save(new User(account, password, email));
            handleRedirectPage(httpRequest, httpResponse);
            return;
        }
        handleRedirectPage(httpRequest, httpResponse);
    }

    private boolean isDuplicateAccount(String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    private void handleRedirectPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader(HttpHeader.LOCATION.value(), "/index.html");
    }
}
