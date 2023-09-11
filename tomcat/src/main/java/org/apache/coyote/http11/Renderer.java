package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.common.MimeType;
import org.apache.coyote.http11.response.ResponseBody;

public class Renderer {

    public static final Renderer EMPTY = Renderer.from("/404");

    private final String mimeType;
    private final String contentLength;
    private final ResponseBody body;

    private Renderer(String mimeType, String contentLength, ResponseBody body) {
        this.mimeType = mimeType;
        this.contentLength = contentLength;
        this.body = body;
    }

    public static Renderer from(String path) {
        URL resource = Renderer.class.getClassLoader()
                .getResource(String.format("static/%s", findResourcePath(path)));

        if (resource != null) {
            return createRenderer(resource);
        }
        return EMPTY;
    }

    private static String findResourcePath(String path) {
        if (path.contains(".")) {
            return path;
        }
        return path + ".html";
    }

    private static Renderer createRenderer(URL resource) {
        File file = new File(resource.getFile());

        String content = readFileContent(file);
        int contentLength = content.getBytes().length;

        return new Renderer(
                determineMimeType(file),
                String.valueOf(contentLength),
                new ResponseBody(content)
        );
    }

    private static String determineMimeType(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        MimeType mimeType = MimeType.from(fileExtension);


        return String.format("%s;charset=utf-8 ", mimeType.getContentType());
    }

    public static String readFileContent(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException | NullPointerException e) {
            return "";
        }
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public ResponseBody getResponseBody() {
        return body;
    }
}
