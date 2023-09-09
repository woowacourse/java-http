package org.apache.coyote.http11.request;

import org.apache.coyote.http11.util.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class RequestBody {

    private final String requestBody;

    private RequestBody(final String requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody of(final String contentLength, final BufferedReader br) throws IOException {
        if (contentLength == null) {
            return new RequestBody("");
        }

        char[] tmp = new char[Integer.parseInt(contentLength.trim())];
        br.read(tmp, 0, Integer.parseInt(contentLength.trim()));

        return new RequestBody(String.copyValueOf(tmp));
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Map<String, String> getParam(){
        return Parser.queryParamParse(requestBody);
    }

    public int getContentLength() {
        return requestBody.length();
    }
}
