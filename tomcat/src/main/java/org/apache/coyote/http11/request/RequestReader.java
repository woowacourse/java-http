package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.List;

public interface RequestReader {

    String readRequestLine() throws IOException;

    List<String> readRequestHeaders() throws IOException;

    String readRequestBody(int contentLength) throws IOException;
}
