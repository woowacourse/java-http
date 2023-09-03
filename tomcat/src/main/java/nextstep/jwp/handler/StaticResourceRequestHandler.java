package nextstep.jwp.handler;

import nextstep.jwp.util.ResourceFileUtil;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.URI;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.HttpStatus;
import org.apache.catalina.servlet.response.ResponseHeaders;
import org.apache.catalina.servlet.response.StatusLine;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        URI uri = request.startLine().uri();
        String resource = ResourceFileUtil.readAll("static" + path(uri));
        return httpResponse(uri, resource);
    }

    private String path(URI uri) {
        if (uri.path().contains(".")) {
            return uri.path();
        } else {
            return uri.path() + ".html";
        }
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
