package nextstep.jwp.handler;

import nextstep.jwp.util.ResourceFileUtil;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.URI;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        URI uri = request.startLine().uri();
        String resource = ResourceFileUtil.readAll("static" + uri.path());
        return httpResponse(uri, resource);
    }

    private HttpResponse httpResponse(URI uri, String responseBody) {
        StatusLine statusLine = new StatusLine(HttpStatus.OK);
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.put("Content-Type", contentType(uri) + "charset=utf-8");
        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    private String contentType(URI uri) {
        if (uri.path().endsWith(".css")) {
            return "text/css;";
        }
        return "text/html;";
    }
}
