package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.dto.HttpRequest;

public interface Handler {

    boolean canHandle(HttpRequest request);

    void handle(HttpRequest request, OutputStream outputStream) throws IOException;
}
