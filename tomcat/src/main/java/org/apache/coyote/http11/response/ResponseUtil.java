package org.apache.coyote.http11.response;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class ResponseUtil {

    private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

    public static void buildStaticFileResponse(HttpResponse response, String url) {
        try (
                FileInputStream fileStream = new FileInputStream(
                        findStaticResourceURL(url).getFile())
        ) {
            String path = findStaticResourceURL(url).getFile();
            String extension = getResourceExtension(path);
            response.setHttpStatusCode(HttpStatusCode.OK);
            response.setContentType(toTextContentType(extension));
            response.setBody(fileStream.readAllBytes());
        } catch (IOException | NullPointerException e) {
            setBadRequest(response);
        }
    }

    public static void setBadRequest(HttpResponse response) {
        response.setHttpStatusCode(HttpStatusCode.BAD_REQUEST);
    }

    private static URL findStaticResourceURL(String url) {
        return SYSTEM_CLASS_LOADER.getResource("static" + url);
    }

    private static String getResourceExtension(String path) {
        return path.split("\\.")[1];
    }

    private static String toTextContentType(String extension) {
        return "text/" + extension;
    }
}
