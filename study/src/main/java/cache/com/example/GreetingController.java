package cache.com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    @GetMapping("/")
    public String index() {
        log.info("index 호출");
        return "index.html";
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
