package org.apache.coyote.http11.http11response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.ExtensionContentType;

public class ResponseManager {

    private static final String DIRECTORY = "static";
    private static final String EXTENSION_IDENTIFIER = ".";

    private ResponseManager() {
    }

    public static void redirectResponseComponent(Http11Response response, String uri, StatusCode statusCode) {
        response.setStatusCode(statusCode);
        response.setLocation(uri);
    }

    public static void resourceResponseComponent(Http11Response response, String uri, StatusCode statusCode) {
        response.setStatusCode(statusCode);
        response.setBody(extractBody(uri));
        response.setContentLength(getContentLength(uri));
        response.setContentType(getContentType(uri));
    }

    public static void defaultResponseComponent(Http11Response response, String message, StatusCode statusCode) {
        response.setStatusCode(statusCode);
        response.setContentLength(Integer.toString(message.length()));
        response.setContentType(ExtensionContentType.HTML.getContentType());
        response.setBody(message);
    }

    public static String extractBody(String uri) {
        try {
            return Files.readString(new File(Objects.requireNonNull(
                    ResponseManager.class.getClassLoader().getResource(DIRECTORY + uri)).getFile()).toPath());
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
                ResponseManager.class.getClassLoader().getResource(DIRECTORY + uri)).getFile()).length());
    }
}
