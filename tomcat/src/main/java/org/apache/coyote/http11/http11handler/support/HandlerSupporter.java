package org.apache.coyote.http11.http11handler.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.ExtensionContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11response.Http11Response;

public class HandlerSupporter {

    private static final String DIRECTORY = "static";
    private static final String EXTENSION_IDENTIFIER = ".";

    private HandlerSupporter() {
    }

    public static Http11Response redirectResponseComponent(String uri, StatusCode statusCode) {
        Http11Response http11Response = new Http11Response(statusCode);
        http11Response.setLocation(uri);
        return http11Response;
    }

    public static Http11Response resourceResponseComponent(String uri, StatusCode statusCode) {
        Http11Response http11Response = new Http11Response(statusCode, extractBody(uri));
        http11Response.setContentLength(getContentLength(uri));
        http11Response.setContentType(getContentType(uri));
        return http11Response;
    }

    public static Http11Response defaultResponseComponent(String message, StatusCode statusCode) {
        Http11Response http11Response = new Http11Response(statusCode, message);
        http11Response.setContentLength(Integer.toString(message.length()));
        http11Response.setContentType(ExtensionContentType.HTML.getContentType());
        return http11Response;
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
