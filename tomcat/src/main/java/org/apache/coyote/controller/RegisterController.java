package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.util.Extension;
import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.message.HttpResponse;
import org.apache.coyote.http11.http.util.ReasonPhrase;

import java.io.IOException;
import java.util.Map;

import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.LOCATION;

public class RegisterController extends AbstractController {

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.setReasonPhrase(ReasonPhrase.OK);
        final String messageBody = FileResolver.readFileToString(FileResolver.REGISTER);
        response.setMessageHeaders(CONTENT_LENGTH, Integer.toString(messageBody.getBytes().length));
        response.setMessageHeaders(CONTENT_TYPE, Extension.findExtension(FileResolver.REGISTER.getFileName())
                                                          .getContentType());
        response.setMessageBody(messageBody);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> body = request.getBody();
        final User user = new User(body.get("account"), body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);
        response.setReasonPhrase(ReasonPhrase.FOUND);
        response.setMessageHeaders(LOCATION, FILE_PATH_PREFIX + FileResolver.INDEX_HTML.getFileName());
        response.setMessageBody(EMPTY_MESSAGE_BODY);
    }
}
