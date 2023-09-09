package org.apache.coyote.request;

import java.io.IOException;
import java.util.List;

public interface Reader {

    String getFirstLine();

    List<String> getHeader();

    String getBody(int bodyLength) throws IOException;
}
