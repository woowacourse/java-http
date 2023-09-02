package cache.com.example;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@Controller
public class GreetingController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/index.html")
    public String indexHtml() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam String account, @RequestParam String password) {
        return "login";
    }

    @GetMapping("/login")
    public String loginWithNoParams() {
        return "login";
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
    public String etag() {
        return "index";
    }

    @GetMapping("/resource-versioning")
    public String resourceVersioning() {
        return "resource-versioning";
    }
}
