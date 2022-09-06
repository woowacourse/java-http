package nextstep.jwp.handler;

import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.StaticFile;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class StaticHandler {

    public static HttpResponse handleStatic(String path) throws IOException {
        ContentType contentType = ContentType.from(path);
        String responseBody = getBody(path);
        return getResponse(contentType, responseBody);
    }

    private static String getBody(String uri) throws IOException {
        return new String(Files.readAllBytes(StaticFile.findByUrl(uri)
                .toPath()));
    }

    public static HttpResponse getResponse(ContentType contentType, String responseBody) {
        return HttpResponse.status(HttpStatus.OK)
                .setHeader("Content-Type", contentType.value())
                .setHeader("Content-Type", "charset=utf-8")
                .setHeader("Content-Length", responseBody.getBytes().length)
                .body(responseBody);
    }
}
