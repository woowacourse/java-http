package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
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

public class LoginHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.OK);

        String responseBody = FileReader.read("/login.html");
        MessageBody messageBody = MessageBody.from(responseBody);

        if (httpRequest.getRequestUri().startsWith("/login?")) {
            Map<String, Object> queryParams = httpRequest.getQueryParams();
            String account = (String) queryParams.get("account");

            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                log.info("user : {}", user);
            }
        }

        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), ContentType.TEXT_HTML.getValue());
        responseHeaders.addHeader(HttpHeaderName.CONTENT_LENGTH.getValue(), messageBody.getBodyLength());

        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }
}
