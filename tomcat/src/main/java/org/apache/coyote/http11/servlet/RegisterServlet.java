package org.apache.coyote.http11.servlet;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.request.HttpMethod;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.request.QueryParams;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.util.Parser;
import org.apache.coyote.http11.util.StaticFileLoader;

public class RegisterServlet implements Servlet {

    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet();
        }
        if (request.getMethod() == HttpMethod.POST) {
            return doPost(request);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.ALLOW, HttpMethod.GET + ", " + HttpMethod.POST);
        return HttpResponse.create(StatusCode.METHOD_NOT_ALLOWED, headers);
    }

    private static HttpResponse doGet() throws IOException {
        String content = StaticFileLoader.load(Page.REGISTER.getUri());

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        return HttpResponse.create(StatusCode.OK, headers, content);
    }

    private static HttpResponse doPost(final HttpRequest request) throws IOException {
        QueryParams params = Parser.parseToQueryParams(request.getBody().getContent());

        if (params.getParam(ACCOUNT).isEmpty() || params.getParam(PASSWORD).isEmpty() || params.getParam(EMAIL).isEmpty()) {
            String content = StaticFileLoader.load(Page.BAD_REQUEST.getUri());

            HttpHeaders headers = new HttpHeaders();
            headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
            headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));

            return HttpResponse.create(StatusCode.BAD_REQUEST, headers, content);
        }
        User user = new User(params.getParam(ACCOUNT).get(), params.getParam(PASSWORD).get(), params.getParam(EMAIL).get());
        InMemoryUserRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.LOCATION, Page.INDEX.getUri());
        return HttpResponse.create(StatusCode.FOUND, headers);
    }
}
