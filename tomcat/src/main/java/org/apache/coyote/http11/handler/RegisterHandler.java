package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpMethodNotAllowedException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {

        if (httpRequest.getRequestLine().getHttpMethod() == HttpMethod.GET) {
            ResponseHeaders responseHeaders = new ResponseHeaders();
            StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.OK);
            MessageBody messageBody = MessageBody.from(FileReader.read("/register.html"));
            responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());

            return new HttpResponse(statusLine, responseHeaders, messageBody);
        }

        if (httpRequest.getRequestLine().getHttpMethod() == HttpMethod.POST) {
            ResponseHeaders responseHeaders = new ResponseHeaders();
            StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.FOUND);
            responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
            responseHeaders.addHeader(HttpHeaderName.LOCATION.getValue(), "/index.html");
            MessageBody messageBody = MessageBody.from(null);

            User user = generateUser(httpRequest);
            InMemoryUserRepository.save(user);
            log.info("registered user : {}", user);

            return new HttpResponse(statusLine, responseHeaders, messageBody);
        }
        throw new HttpMethodNotAllowedException("허용되지 않는 HTTP Method입니다.");
    }

    private User generateUser(final HttpRequest httpRequest) {
        Map<String, Object> formDataMap = httpRequest.getMessageBody().getFormData();
        String account = (String) formDataMap.get("account");
        String email = (String) formDataMap.get("email");
        String password = (String) formDataMap.get("password");

        return new User(account, password, email);
    }
}
