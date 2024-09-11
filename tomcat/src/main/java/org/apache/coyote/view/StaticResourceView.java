package org.apache.coyote.view;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.response.HttpResponse;

public class StaticResourceView implements View {

    private static final String PREFIX = "static/";

    private final String viewPath;

    public StaticResourceView(String viewPath) {
        this.viewPath = viewPath;
    }

    @Override
    public void render(HttpResponse response) {
        try {
            URL resource = StaticResourceResolver.class.getClassLoader().getResource(PREFIX + viewPath);
            if (resource == null) {
                throw new IllegalArgumentException(viewPath + " 파일이 존재하지 않습니다.");
            }
            Path path = Path.of(resource.getPath());
            String contentType = Files.probeContentType(path);
            response.addContentType(contentType);
            response.addBody(new String(Files.readAllBytes(path)));
            response.updateHttpStatus(HttpStatus.OK);
        } catch (IOException exception) {
            response.updateHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            throw new RuntimeException("서버 내부 IO 작업 오류 발생 : " + exception.getMessage());
        }
    }
}
