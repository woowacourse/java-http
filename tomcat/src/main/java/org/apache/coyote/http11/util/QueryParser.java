package org.apache.coyote.http11.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.QueryParams;

public class QueryParser {
    public static QueryParams parse(String line) {
        String[] split = line.split("&");

        Map<String, String> params = new HashMap<>();
        for (String piece : split) {
            int equalSignIdx = piece.indexOf("=");
            params.put(piece.substring(0, equalSignIdx), piece.substring(equalSignIdx+1));
        }

        return new QueryParams(params);
    }
}
