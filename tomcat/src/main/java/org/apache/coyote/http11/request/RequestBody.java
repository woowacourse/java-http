package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

class RequestBody {

    private final Parameters parameters;

    private RequestBody(Parameters parameters) {
        this.parameters = parameters;
    }

    static RequestBody from(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int count = reader.read(buffer, read, contentLength - read);
            if (count == -1) {
                break;
            }
            read += count;
        }
        return new RequestBody(Parameters.from(new String(buffer, 0, read)));
    }

    String getParameter(String name) {
        return parameters.get(name);
    }
}
