package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public abstract class Handler {

    abstract Response handle(Request request) throws IOException;

    protected String findResourceWithPath(String absolutePath) throws IOException {
        URL resourceUrl = Handler.class.getClassLoader().getResource("static/" + absolutePath);
        return new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
    }
}
