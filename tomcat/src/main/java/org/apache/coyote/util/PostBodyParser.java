package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PostBodyParser {

    public static Map<String, String> parse(final BufferedReader reader) throws IOException {
        int contentLength = 0;
        String line = "";
        while ((line = reader.readLine()) != null) {

            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }

            if (line.isEmpty()) {
                break;
            }
        }

        String postBody = "";
        if (contentLength > 0) {
            final char[] buffer = new char[contentLength];
            final int bytesRead = reader.read(buffer, 0, contentLength);
            postBody = new String(buffer, 0, bytesRead);
        }
        return FormDataParser.parseFormData(postBody);
    }


}
