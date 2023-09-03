package org.apache.coyote.http11.mvc.view;

import java.io.IOException;

public interface View {

    String renderView() throws IOException;

    String getContentType();
}
