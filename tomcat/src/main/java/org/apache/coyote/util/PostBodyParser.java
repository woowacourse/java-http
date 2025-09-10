package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class PostBodyParser {

    public static Map<String, String> parse(final BufferedReader reader, int contentLength) throws IOException {
        String postBody = "";
        if (contentLength > 0) {
            final char[] buffer = new char[contentLength];
            final int bytesRead = reader.read(buffer, 0, contentLength);
            postBody = new String(buffer, 0, bytesRead);
        }
        return FormDataParser.parseFormData(postBody);
    }


}
