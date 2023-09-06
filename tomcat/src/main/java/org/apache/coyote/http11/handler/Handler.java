package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.http11.request.Request;

public abstract class Handler {
    abstract void setNext(Handler handler);
    abstract String getResponse(Request request);

    protected String getFile(String fileName){
        try {
            final var fileUrl = getClass().getClassLoader().getResource("static/" + fileName);
            final var fileBytes = Files.readAllBytes(new File(fileUrl.getFile()).toPath());
            return new String(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            return "";
        }
    }
}
