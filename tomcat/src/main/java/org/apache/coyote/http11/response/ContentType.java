package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContentType {

    public static String find(String uri) throws IOException {
        final Path path = Path.of( uri);
        String contentType = Files.probeContentType(  path);
        if(contentType == null) {
            contentType = "text/html";
        }
        return contentType;
    }
}
