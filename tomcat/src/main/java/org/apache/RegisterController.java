package org.apache;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberExistException;
import nextstep.jwp.model.User;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.exception.FileNotReadableException;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws FileNotReadableException {
        final String fileContent = fileReader.readStaticFile("/register.html");
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setBody(fileContent, ContentType.findResponseContentTypeFromRequest(httpRequest));
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws FileNotReadableException {
        try {
            register(httpRequest);
        } catch (MemberExistException e) {
            httpResponse.setHttpStatus(HttpStatus.CONFLICT);
            httpResponse.setBody(fileReader.readStaticFile("/login.html"),
                ContentType.findResponseContentTypeFromRequest(httpRequest));
            return;
        }
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.setHeader("Location", "/index.html");
    }

    private void register(final HttpRequest httpRequest) {
        final String account = httpRequest.getBodyOf("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new MemberExistException();
        }
        final User user = new User(
            account, httpRequest.getBodyOf("password"), httpRequest.getBodyOf("email")
        );
        InMemoryUserRepository.save(user);
    }
}
