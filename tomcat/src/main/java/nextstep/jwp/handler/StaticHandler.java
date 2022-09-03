package nextstep.jwp.handler;

import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.StaticFile;
import org.apache.coyote.http11.ContentType;

public class StaticHandler {

    public static String handleStatic(String path) throws IOException {
        ContentType contentType = ContentType.from(path);
        String responseBody = getBody(path);
        return getResponse(contentType, responseBody);
    }

    private static String getBody(String uri) throws IOException {
        return new String(Files.readAllBytes(StaticFile.findByUrl(uri)
                .toPath()));
    }

    public static String getResponse(ContentType contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.value() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
