package org.apache.coyote.http11.controller;


import java.util.Map;
import org.apache.coyote.http11.request.RequestLine;

public interface Controller {
    boolean canHandle(String url);

    Map<String, String> handle(RequestLine requestLine);

}
