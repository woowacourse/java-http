package org.apache.coyote.http11;

import com.techcourse.controller.ControllerMapping;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.FileExtension;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public class PathInfo {

    private static final URI LOGIN_PATH = URI.create("/login");
    private static final String STATIC_FOLDER_NAME = "static";
    private static final ClassLoader CLASS_LOADER = PathInfo.class.getClassLoader();

    private final URI path;
    private final FileExtension fileExtension;

    public PathInfo(URI path, FileExtension fileExtension) {
        this.path = path;
        this.fileExtension = fileExtension;
    }

    public boolean isLogin() {
        return LOGIN_PATH.equals(path);
    }

    public ControllerMapping getControllerMapping(HttpMethod method) {
        return ControllerMapping.of(method, path);
    }

    public HttpResponse<String> getHttpResponse(HttpResponse<?> response) throws IOException {
        URL resource = CLASS_LOADER.getResource(STATIC_FOLDER_NAME + path + fileExtension.getExtension());
        if (resource == null) {
            throw new UncheckedHttpException(new IllegalArgumentException("잘못된 경로입니다."));
        }
        File file = new File(resource.getFile());
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        HttpResponse<String> httpResponse = response.getFileResponse(responseBody);
        int contentLength = httpResponse.getBody().getBytes().length;
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, fileExtension.getMediaType());
        httpResponse.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        return httpResponse;
    }
}
