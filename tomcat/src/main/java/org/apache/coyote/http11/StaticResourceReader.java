package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;

public class StaticResourceReader {

    public static String read(String resourceUri) throws IOException {
        final InputStream staticResource = ResponseGenerator.class.getClassLoader()
                .getResourceAsStream("static" + resourceUri);
        return new String(staticResource.readAllBytes());
    }
}
