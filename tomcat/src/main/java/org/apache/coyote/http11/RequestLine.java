package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final Method method;
    private String path; //TODO final 필요
    private final String version;

    public RequestLine(Method method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public RequestLine(BufferedReader bufferedReader) throws IOException {
        String[] values = bufferedReader.readLine().split(DELIMITER);
        this.method = Method.from(values[METHOD_INDEX]);
        this.path = values[PATH_INDEX];
        this.version = values[VERSION_INDEX];
    }

    public String getPath() {
        return path;
    }

    public boolean isMethod(Method method) {
        return this.method.equals(method);
    }
}
