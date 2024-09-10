package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class ResourceToHttpBodyConverter {

    private ResourceToHttpBodyConverter() {
    }

    public static HttpResponse covert(URL resource) throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
        }

        Path path = Paths.get(resource.getPath());

        HttpResponse response = HttpResponse.of(HttpStatus.OK, Files.readAllBytes(path));
        String contentType = probeContentType(path);
        response.setHeader("Content-Type", contentType);

        return response;
    }

    public static String probeContentType(Path path) throws IOException {
        String mimeType = Files.probeContentType(path);
        if (mimeType.startsWith("text")) {
            return mimeType + ";charset=utf-8";
        }

        return mimeType;
    }
}
