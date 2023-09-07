package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        ResponseHeaders responseHeaders = new ResponseHeaders();
        StatusLine statusLine = new StatusLine(request.getHttpVersion(), Status.OK);
        MessageBody messageBody = MessageBody.from(FileReader.read("/register.html"));
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());

        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        ResponseHeaders responseHeaders = new ResponseHeaders();
        StatusLine statusLine = new StatusLine(request.getHttpVersion(), Status.FOUND);
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/index.html");
        MessageBody messageBody = MessageBody.empty();

        User user = generateUser(request);
        InMemoryUserRepository.save(user);
        log.info("registered user : {}", user);

        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }

    private User generateUser(final HttpRequest httpRequest) {
        Map<String, Object> formDataMap = httpRequest.getMessageBody().getFormData();
        String account = (String) formDataMap.get("account");
        String email = (String) formDataMap.get("email");
        String password = (String) formDataMap.get("password");

        return new User(account, password, email);
    }
}
