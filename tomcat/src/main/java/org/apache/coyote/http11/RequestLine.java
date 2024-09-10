package org.apache.coyote.http11;

import org.apache.Method;
import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {

    private final Method method;
    private String path; //TODO final 필요
    private final String version;

    public RequestLine(BufferedReader bufferedReader) throws IOException {
        String[] values = bufferedReader.readLine().split(" ");

        this.method = Method.from(values[0]);
        this.path = values[1];
        this.version = values[2];
    }

    public String getPath() {
        if (method.equals(Method.GET)) {
            if (path.equals("/login") || path.equals("/register")) {
                path = path + ".html";
            }

            if (path.equals("/")) {
                path = "home.html";
            }
        }

        return path;
    }

    public boolean isMethod(Method method) {
        return this.method.equals(method);
    }
}
