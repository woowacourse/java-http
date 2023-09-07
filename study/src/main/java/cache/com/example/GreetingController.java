package cache.com.example;

import cache.com.example.version.ResourceVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Controller
public class GreetingController {

    @Autowired
    private ResourceVersion resourceVersion;

    @GetMapping("/")
    public String index(HttpServletResponse response) {
        String cacheControl = CacheControl.noCache()
                                         .cachePrivate()
                                         .getHeaderValue();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
        response.addHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
        return "index";
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
    public String etag(HttpServletResponse response) {
        return "index";
    }

    @GetMapping("/resource-versioning")
    public String resourceVersioning(HttpServletResponse response) {
        String cacheControl = CacheControl.noCache()
                                         .sMaxAge(Duration.ofDays(365))
                                         .cachePublic()
                                         .getHeaderValue();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
        return "resource-versioning";
        /**
         * 1. http://localhost:8080/resource-versioning GET 요청
         * 2. resource-versioning.html 응답
         * 3. html 내부에서 VersionHandlebarsHelper.staticUrls() 통해서 /resources/${version}/js/index.js 접근
         * 4. ResourceHandler 통해서 /resources/${version}/js/index.js 접근을 classpath:/static/ 에서 리소스를 찾도록 함
         * 5. /resources/* 경로에 eTag 필터를 등록
         */
    }
}
