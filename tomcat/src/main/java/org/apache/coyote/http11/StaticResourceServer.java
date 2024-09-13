package org.apache.coyote.http11;

public class StaticResourceServer {

    private static final String EXTENSION_SEPARATOR = ".";

    private StaticResourceServer() {
    }

    public static void load(HttpResponse response, String fileName) {
        try {
            byte[] body = StaticResourceLoader.load(fileName);
            ContentType contentType = extractContentType(fileName);
            response.setContentType(contentType);
            response.ok(body);
        } catch (ResourceNotFoundException e) {
            response.setStatusCode(StatusCode.NOT_FOUND);
        } catch (IllegalStateException e) {
            response.setStatusCode(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private static ContentType extractContentType(String fileName) {
        int extensionSeparatorIndex = fileName.lastIndexOf(EXTENSION_SEPARATOR);
        String extension = fileName.substring(extensionSeparatorIndex);
        return ContentType.fromExtension(extension);
    }
}
