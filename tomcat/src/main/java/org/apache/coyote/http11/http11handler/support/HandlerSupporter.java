package org.apache.coyote.http11.http11handler.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.ExtensionContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11response.ResponseComponent;

public class HandlerSupporter {

    private static final String DIRECTORY = "static";
    private static final String EXTENSION_IDENTIFIER = ".";

    private HandlerSupporter() {
    }

    public static ResponseComponent redirectResponseComponent(String uri, StatusCode statusCode) {
        ResponseComponent responseComponent = new ResponseComponent(statusCode);
        responseComponent.setLocation(uri);
        return responseComponent;
    }

    public static ResponseComponent resourceResponseComponent(String uri, StatusCode statusCode) {
        ResponseComponent responseComponent = new ResponseComponent(statusCode, extractBody(uri));
        responseComponent.setContentLength(getContentLength(uri));
        responseComponent.setContentType(getContentType(uri));
        return responseComponent;
    }

    public static ResponseComponent defaultResponseComponent(String message, StatusCode statusCode) {
        ResponseComponent responseComponent = new ResponseComponent(statusCode, message);
        responseComponent.setContentLength(Integer.toString(message.length()));
        responseComponent.setContentType(ExtensionContentType.HTML.getContentType());
        return responseComponent;
    }

    public static String extractBody(String uri) {
        try {
            return Files.readString(new File(Objects.requireNonNull(
                    HandlerSupporter.class.getClassLoader().getResource(DIRECTORY + uri)).getFile()).toPath());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static String getContentType(String uri) {
        String extension = extractExtension(uri);
        return ExtensionContentType.toContentType(extension);
    }

    public static String extractExtension(String uri) {
        int extensionStartIndex = uri.lastIndexOf(EXTENSION_IDENTIFIER) + 1;
        return uri.substring(extensionStartIndex);
    }

    public static String getContentLength(String uri) {
        return Long.toString(new File(Objects.requireNonNull(
                HandlerSupporter.class.getClassLoader().getResource(DIRECTORY + uri)).getFile()).length());
    }
}
