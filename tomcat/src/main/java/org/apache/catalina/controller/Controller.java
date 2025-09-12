package org.apache.catalina.controller;


import java.io.IOException;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public interface Controller {
    Http11Response service(final Http11Request request) throws IOException;
}
