package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.http11.request.Request;

public abstract class Handler {
    abstract void setNext(Handler handler);
    abstract String getResponse(Request request);
}
