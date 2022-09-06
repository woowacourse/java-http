package nextstep.jwp.handler;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public class IndexHandler {

    public static HttpResponse perform(HttpRequest request) {
        if (request.getMethod().isGet()) {
            return doGet();
        }
        return HttpResponse.notFound();
    }

    private static HttpResponse doGet() {
        String contentType = ContentType.HTML.getContentType();
        return new HttpResponse.Builder()
                .statusCode(HttpStatus.OK)
                .header(HttpHeaderType.CONTENT_TYPE, contentType)
                .responseBody("Hello world!")
                .build();
    }
}
