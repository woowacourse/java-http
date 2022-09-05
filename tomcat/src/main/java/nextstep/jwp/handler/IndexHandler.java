package nextstep.jwp.handler;

import org.apache.coyote.http11.FileExtension;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponse.Builder;
import org.apache.coyote.http11.HttpStatus;

public class IndexHandler {

    public static HttpResponse perform(HttpRequest request) {
        String contentType = FileExtension.HTML.getContentType();

        return new Builder()
                .statusCode(HttpStatus.OK)
                .header("Content-Type", contentType)
                .responseBody("Hello world!")
                .build();
    }
}
