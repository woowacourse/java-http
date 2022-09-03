package servlet.view;

import static org.apache.coyote.Constants.ROOT;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;

public class ViewResolver {

    public ViewResolver() {}

    public HttpResponse getResponse(HttpMethod method, String url, HttpStatus status) {
        if (method != HttpMethod.GET) {
            throw new NoSuchElementException();
        }
        try {
            URL fileUrl = getClass().getClassLoader().getResource(ROOT + url);
            String file = Objects.requireNonNull(fileUrl).getFile();
            Path path = new File(file).toPath();

            String contentType = Files.probeContentType(path);
            return HttpResponse.from(HttpResponseBody.of(ROOT + url), status,
                    contentType);
        } catch (NullPointerException | IOException e) {
            throw new NoSuchElementException("해당 페이지를 찾을 수 없습니다: " + url);
        }
    }

    public HttpResponse getResponse(HttpMethod method, String url) {
        return getResponse(method, url, HttpStatus.OK);
    }
}
