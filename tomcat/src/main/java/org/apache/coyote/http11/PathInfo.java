package org.apache.coyote.http11;

import com.techcourse.controller.ControllerMapping;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.exception.UncheckedHttpException;

public class PathInfo {

    private static final String STATIC_FOLDER_NAME = "static";
    private static final ClassLoader CLASS_LOADER = PathInfo.class.getClassLoader();

    private final String filePath;
    private final MediaType mediaType;

    public PathInfo(String filePath, MediaType mediaType) {
        this.filePath = filePath;
        this.mediaType = mediaType;
    }

    public ControllerMapping getControllerMapping(HttpMethod method) {
        return ControllerMapping.of(method, filePath);
    }

    public HttpResponse<String> getHttpResponse(HttpResponse<?> response) throws IOException {
        URL resource = CLASS_LOADER.getResource(STATIC_FOLDER_NAME + filePath + mediaType.getExtension());
        if (resource == null) {
            throw new UncheckedHttpException(new IllegalArgumentException("잘못된 경로입니다."));
        }
        File file = new File(resource.getFile());
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        HttpResponse<String> httpResponse = response.getFileResponse(responseBody);
        int contentLength = httpResponse.getBody().getBytes().length;
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, mediaType.getValue());
        httpResponse.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        return httpResponse;
    }
}
