package cache.com.example;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingController {

    private static final String INDEX_HTML = "index.html";

    @GetMapping("/")
    public String index() {
        return INDEX_HTML;
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
        return INDEX_HTML;
    }

    @GetMapping("/etag")
    public String etag() {
        return INDEX_HTML;
    }

    @GetMapping("/resource-versioning")
    public String resourceVersioning() {
        return "resource-versioning.html";
    }
}
