package nextstep.jwp.handler;

import static org.apache.coyote.http11.ViewResolver.staticFileRequest;

import java.util.Map;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.UriParser;

public class RegisterHandler {

    private static final String REGISTER_PAGE = "/register.html";

    public static HttpResponse perform(HttpRequest request) {
        Map<String, String> queries = UriParser.parseUri(request.getUri());
        if (request.getMethod().isGet() && queries.isEmpty()) {
            return staticFileRequest(REGISTER_PAGE);
        }
        return HttpResponse.notFound();
    }
}
