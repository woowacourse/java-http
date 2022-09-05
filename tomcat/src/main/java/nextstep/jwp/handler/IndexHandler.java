package nextstep.jwp.handler;

import org.apache.coyote.http11.FileExtension;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class IndexHandler {

    public static HttpResponse perform(HttpRequest request) {
        if (!request.getMethod().isGet()) {
            return HttpResponse.notFound();
        }

        String contentType = FileExtension.HTML.getContentType();
        return new HttpResponse.Builder()
                .statusCode(HttpStatus.OK)
                .header("Content-Type", contentType)
                .responseBody("Hello world!")
                .build();
    }
}
