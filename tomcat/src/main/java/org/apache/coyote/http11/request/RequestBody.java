package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.utils.QueryParser;

public class RequestBody {

    private final Map<String, String> requestBody;

    public RequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody from(BufferedReader bufferedReader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return new RequestBody(new HashMap<>());
        }

        char[] bodys = new char[contentLength];
        bufferedReader.read(bodys, 0, contentLength);
        String body = new String(bodys);

        return new RequestBody(QueryParser.parse(body));
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }
}
