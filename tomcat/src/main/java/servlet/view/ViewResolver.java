package servlet.view;

import static org.apache.coyote.Constants.CRLF;
import static org.apache.coyote.Constants.ROOT;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.mapping.ResponseEntity;

public class ViewResolver {

    public ViewResolver() {
    }

    public HttpResponse getResponse(ResponseEntity entity) {
        String url = entity.getUri();
        HttpStatus status = entity.getStatus();
        Map<String, String> headers = entity.getHeaders();

        if (status == HttpStatus.FOUND) {
            return HttpResponse.found().addHeaders(headers);
        }
        return getStaticResponse(url, status).addHeaders(headers);
    }

    private HttpResponse getStaticResponse(String url, HttpStatus status) {
        try {
            URL fileUrl = getClass().getClassLoader().getResource(ROOT + url);
            String file = Objects.requireNonNull(fileUrl).getFile();
            Path path = new File(file).toPath();

            String contentType = Files.probeContentType(path);
            return HttpResponse.from(new HttpResponseBody(String.join(CRLF, new String(Files.readAllBytes(path)))), status,
                    contentType);
        } catch (NullPointerException | IOException e) {
            throw new NoSuchElementException("해당 페이지를 찾을 수 없습니다: " + url);
        }
    }

    public HttpResponse getResponse(String url) {
        return getResponse(ResponseEntity.ok(url));
    }
}
