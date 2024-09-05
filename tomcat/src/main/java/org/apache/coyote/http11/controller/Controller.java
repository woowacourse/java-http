package org.apache.coyote.http11.controller;

import java.nio.file.Path;
import org.apache.coyote.http11.RequestLine;

public interface Controller {
    boolean canHandle(String url);

    Path handle(RequestLine requestLine);
}
