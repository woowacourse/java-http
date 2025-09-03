package org.apache.coyote.http11;

import java.io.IOException;

public interface HttpContentParser {

    byte[] parseContent(String contentPath) throws IOException;
}
