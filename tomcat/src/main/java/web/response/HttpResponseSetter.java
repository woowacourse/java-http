package web.response;

import java.io.IOException;
import java.util.Optional;
import web.request.RequestUri;
import web.util.StaticResourceFinder;

public class HttpResponseSetter {

    public static void setStaticResource(final HttpResponse httpResponse, final RequestUri requestUri) {
        try {
            Optional<String> staticResource = StaticResourceFinder.findStaticResource(requestUri);
            if (staticResource.isEmpty()) {
                throw new RuntimeException("[ERROR] 리소스가 존재하지 않습니다.");
            }
            String body = staticResource.get();
            httpResponse.setBody(body);
            httpResponse.setStatusLine(new StatusLine("HTTP/1.1", "200", "OK"));
            httpResponse.putHeader(
                    "Content-Type",
                    "text/" + requestUri.findStaticResourceType().get() + ";charset=utf-8"
            );
            httpResponse.putHeader("Content-Length", String.valueOf(body.getBytes().length));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set302Redirect(final HttpResponse httpResponse, final String location) {
        String body = "";
        httpResponse.setBody(body);
        httpResponse.setStatusLine(new StatusLine("HTTP/1.1", "302", "FOUND"));
        httpResponse.putHeader("Location", location);
    }
}
