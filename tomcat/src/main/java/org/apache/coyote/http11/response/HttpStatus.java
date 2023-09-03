package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public enum HttpStatus {
    OK(200,"OK"),
    CREATED(201,"Created"),
    FOUND(302,"Found"),
    UNAUTHORIZED(401,"Unauthorized");

    private final int value;
    private final String detail;

    HttpStatus(int value, String detail) {
        this.value = value;
        this.detail = detail;
    }

    public int getValue() {
        return value;
    }

    public String getDetail() {
        return detail;
    }

    public String getFile(){
        try {
            final var fileUrl = getClass().getClassLoader().getResource("static/" + value + ".html");
            final var fileBytes = Files.readAllBytes(new File(fileUrl.getFile()).toPath());
            return new String(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            return "";
        }
    }

    @Override
    public String toString() {
        return value + " " + detail;
    }
}
