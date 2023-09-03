package org.apache.coyote.http11.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.util.QueryParser;
import org.apache.coyote.http11.util.StaticFileLoader;

public class RegisterHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            String content = StaticFileLoader.load("/register.html");

            HttpHeaders headers = new HttpHeaders();
            headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
            headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
            return HttpResponse.create(StatusCode.OK, headers, content);
        }
        if (request.getMethod() == HttpMethod.POST) {
            QueryParams params = QueryParser.parse(request.getBody().getContent());

            User user = new User(params.getParam("account"), params.getParam("password"), params.getParam("email"));
            InMemoryUserRepository.save(user);

            HttpHeaders headers = new HttpHeaders();
            headers.addHeader(HttpHeaderName.LOCATION, "/index.html");
            return HttpResponse.create(StatusCode.FOUND, headers);
        }
        return null;
    }
}
