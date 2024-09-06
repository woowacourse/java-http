package cache.com.example;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Controller
public class GreetingController {

    @GetMapping("/")
    public ResponseEntity<StreamingResponseBody> index() {
        StreamingResponseBody stream = new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                String htmlContent = "<html><body><h1>Hello, World!</h1></body></html>";
                outputStream.write(htmlContent.getBytes());
                outputStream.flush();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                .body(stream);
    }

    /**
     * 인터셉터를 쓰지 않고 response에 직접 헤더값을 지정할 수도 있다.
     */
    @GetMapping("/cache-control")
    public String cacheControl(final HttpServletResponse response) {
        final String cacheControl = CacheControl
                .noCache()
                .cachePrivate()
                .getHeaderValue();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
        return "index.html";
    }

    @GetMapping("/etag")
    public String etag() {
        return "index.html";
    }

    @GetMapping("/resource-versioning")
    public String resourceVersioning() {
        return "resource-versioning.html";
    }
}
