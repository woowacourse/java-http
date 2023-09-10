package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResponseBody {

    private final String body;

    private ResponseBody(final String body) {
        this.body = body;
    }

    public static ResponseBody fromUri(final String uri) throws IOException {
        final URL fileUrl = findResourceUrl(uri);
        final String filePath = fileUrl.getPath();
        final String body = Files.readString(new File(filePath).toPath());
        return new ResponseBody(body);
    }

    public static ResponseBody fromText(final String text) {
        return new ResponseBody(text);
    }

    private static URL findResourceUrl(final String uri) {
        final URL fileUrl = ResponseBody.class.getClassLoader().getResource("./static" + uri);
        if (fileUrl == null) {
            return ResponseBody.class.getClassLoader().getResource("./static/404.html");
        }
        return fileUrl;
    }

    public String getBody() {
        return body;
    }
}
