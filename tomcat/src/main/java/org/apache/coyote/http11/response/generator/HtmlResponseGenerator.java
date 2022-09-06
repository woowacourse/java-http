package org.apache.coyote.http11.response.generator;

import static org.apache.coyote.http11.request.HttpMethod.GET;

import java.io.IOException;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;

public class HtmlResponseGenerator extends FileResponseGenerator {

    private static final String HTML_FILE_EXTENSION = ".html";
    private static final String INDEX_HTML_LOCATION = "http://localhost:8080/index.html";
    private static final String FILE_EXTENSION_PREFIX = ".";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return isBasicHtmlRequest(httpRequest) || isHtmlRequestWithoutHtmlExtension(httpRequest);
    }

    private boolean isBasicHtmlRequest(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathIncluding(HTML_FILE_EXTENSION) && httpRequest.hasHttpMethodOf(GET);
    }

    private boolean isHtmlRequestWithoutHtmlExtension(HttpRequest httpRequest) {
        return !httpRequest.hasRequestPathIncluding(FILE_EXTENSION_PREFIX) && httpRequest.hasHttpMethodOf(GET);
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        if (isLoginRequestOfAlreadyLoginUser(httpRequest)) {
            return HttpResponse.found(INDEX_HTML_LOCATION);
        }
        return HttpResponse.ok(generate(httpRequest.getPath()), ContentType.TEXT_HTML);
    }

    private boolean isLoginRequestOfAlreadyLoginUser(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathOf("/login") && httpRequest.hasHttpMethodOf(GET) &&
                httpRequest.hasCookieOf("JSESSIONID") &&
                SessionManager.isValid(httpRequest.getCookieOf("JSESSIONID"));
    }
}
