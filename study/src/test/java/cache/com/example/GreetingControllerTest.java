package cache.com.example;

import cache.com.example.version.ResourceVersion;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingControllerTest {

    private static final Logger log = LoggerFactory.getLogger(GreetingControllerTest.class);

    @Autowired
    private ResourceVersion version;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testNoCachePrivate() {
        final var response = webTestClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().cacheControl(CacheControl.noCache().cachePrivate())
                .expectBody(String.class).returnResult();

        log.info("response body\n{}", response.getResponseBody());
    }

    @Test
    void testCompression() {
        final var response = webTestClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()

                // gzip으로 요청 보내도 어떤 방식으로 압축할지 서버에서 결정한다.
                // 웹브라우저에서 localhost:8080으로 접근하면 응답 헤더에 "Content-Encoding: gzip"이 있다.
                .expectHeader().valueEquals(HttpHeaders.TRANSFER_ENCODING, "chunked")
                .expectBody(String.class).returnResult();

        log.info("response body\n{}", response.getResponseBody());
    }

    @Test
    void testETag() {
        final var response = webTestClient
                .get()
                .uri("/etag")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.ETAG)
                .expectBody(String.class).returnResult();

        log.info("response body\n{}", response.getResponseBody());
    }

    /**
     * http://localhost:8080/resource-versioning
     * 위 url의 html 파일에서 사용하는 js, css와 같은 정적 파일에 캐싱을 적용한다.
     * 보통 정적 파일을 캐싱 무효화하기 위해 캐싱과 함께 버전을 적용시킨다.
     * 정적 파일에 변경 사항이 생기면 배포할 때 버전을 바꿔주면 적용된 캐싱을 무효화(Caching Busting)할 수 있다.
     */
    @Test
    void testCacheBustingOfStaticResources() {
        final var uri = String.format("%s/%s/js/index.js", PREFIX_STATIC_RESOURCES, version.getVersion());

        // "/resource-versioning/js/index.js" 경로의 정적 파일에 ETag를 사용한 캐싱이 적용되었는지 확인한다.
        final var response = webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.ETAG)
                .expectHeader().cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic())
                .expectBody(String.class).returnResult();

        log.info("response body\n{}", response.getResponseBody());

        final var etag = response.getResponseHeaders().getETag();

        // 캐싱되었다면 "/resource-versioning/js/index.js"로 다시 호출했을때 HTTP status는 304를 반환한다.
        webTestClient.get()
                .uri(uri)
                .header(HttpHeaders.IF_NONE_MATCH, etag)
                .exchange()
                .expectStatus()
                .isNotModified();
    }
}
