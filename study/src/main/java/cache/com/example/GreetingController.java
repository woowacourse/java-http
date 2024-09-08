package cache.com.example;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import cache.com.example.etag.Etag;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class GreetingController {
    private static final String eTag = "33a64df551425fcc55e4d42a148795d9f25f89d4";

    @GetMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache().cachePrivate())
            .header("Content-Encoding", "gzip")
            .body("index");
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
        return "index";
    }

    @GetMapping("/etag")
    public ResponseEntity<String> etag() {
        return ResponseEntity.ok()
            .body("index");
    }

    @GetMapping("/resource-versioning")
    public String resourceVersioning() {
        return "resource-versioning";
    }
}
