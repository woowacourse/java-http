package org.apache.coyote.http11.file;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Objects;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FileFinder {

    private static final String STATIC_FOLDER_NAME = "static";

    private final HttpRequest httpRequest;

    public FileFinder(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void resolve(HttpResponse response) throws IOException {
        FileDetails fileDetails = httpRequest.getFileDetails();
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(STATIC_FOLDER_NAME + fileDetails.getFilePath());
        if (Objects.isNull(resource)) {
            throw new UncheckedHttpException(new NoSuchFileException("해당 경로에 파일이 존재하지 않습니다."));
        }

        File file = new File(resource.getFile());
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        response.addHeader(HttpHeaders.CONTENT_TYPE, fileDetails.extension().getMediaType());
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        response.setBody(responseBody);
    }
}
