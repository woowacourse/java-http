package servlet.view;

import static org.apache.coyote.Constants.CRLF;
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
        if (status == HttpStatus.FOUND) {
            return HttpResponse.found(url);
        }
        if (method == HttpMethod.GET) {
            return getStaticResponse(url, status);
        }
        throw new NoSuchElementException();
    }

    private HttpResponse getStaticResponse(String url, HttpStatus status) {
        try {
            URL fileUrl = getClass().getClassLoader().getResource(ROOT + url);
            String file = Objects.requireNonNull(fileUrl).getFile();
            Path path = new File(file).toPath();

            String contentType = Files.probeContentType(path);
            return HttpResponse.from(new HttpResponseBody(bodyOf(ROOT + url)), status,
                    contentType);
        } catch (NullPointerException | IOException e) {
            throw new NoSuchElementException("해당 페이지를 찾을 수 없습니다: " + url);
        }
    }

    private String bodyOf(final String fileName) {
        try {
            URL url = HttpResponseBody.class.getClassLoader().getResource(fileName);
            String file = Objects.requireNonNull(url).getFile();
            Path path = new File(file).toPath();
            return String.join(CRLF, new String(Files.readAllBytes(path)));
        } catch (NullPointerException | IOException e) {
            throw new NoSuchElementException("해당 이름의 파일을 찾을 수 없습니다: " + fileName);
        }
    }

    public HttpResponse getResponse(HttpMethod method, String url) {
        return getResponse(method, url, HttpStatus.OK);
    }
}
