package org.apache.coyote.http11.web;

import java.io.IOException;

public interface View {

    String renderView() throws IOException;

    String getContentType();
}
