package org.apache.coyote.http11.response;

import java.io.IOException;

public interface Response {

    String get() throws IOException;
}
