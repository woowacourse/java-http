package org.apache.coyote.http11.response;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class ResponseUtil {

    private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

    public static HttpResponse buildStaticFileResponse(String url) {
        try (
                FileInputStream fileStream = new FileInputStream(
                        findStaticResourceURL(url).getFile())
        ) {
            String path = findStaticResourceURL(url).getFile();
            String extension = getResourceExtension(path);
            return new HttpResponse.Builder()
                    .setHttpStatusCode(HttpStatusCode.OK)
                    .setContentType(toTextContentType(extension))
                    .setBody(fileStream.readAllBytes())
                    .build();
        } catch (IOException | NullPointerException e) {
            return buildBadRequest();
        }
    }

    public static HttpResponse buildBadRequest() {
        return new HttpResponse.Builder()
                .setHttpStatusCode(HttpStatusCode.BAD_REQUEST)
                .build();
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
