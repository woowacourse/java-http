package nextstep.jwp;

import nextstep.jwp.network.ContentType;
import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;
import nextstep.jwp.network.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    public DefaultController(String resource) {
        super(resource);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        if ("/".equals(path)) {
            log.info("HELLO WORLD!");
            return HttpResponse.ofByteArray(HttpStatus.OK, "Hello world!".getBytes());
        }
        if ("/index.html".equals(path)) {
            log.info("GET /index.html");
            final byte[] bytes = readIndex();
            return HttpResponse.ofByteArray(HttpStatus.OK, bytes);
        }
        if ("/css/styles.css".equals(path)) {
            log.info("GET /css/styles.css");
            final byte[] bytes = readFile(path);
            return HttpResponse.ofByteArray(HttpStatus.OK, ContentType.CSS, bytes);
        }
        if ("/js/scripts.js".equals(path)) {
            log.info("GET /js/scripts.js");
            final byte[] bytes = readFile(path);
            return HttpResponse.ofByteArray(HttpStatus.OK, ContentType.JAVASCRIPT, bytes);
        }
        if ("/assets/chart-pie.js".equals(path)) {
            log.info("GET /assets/chart-pie.js");
            final byte[] bytes = readFile(path);
            return HttpResponse.ofByteArray(HttpStatus.OK, ContentType.JAVASCRIPT, bytes);
        }
        if ("/assets/chart-area.js".equals(path)) {
            log.info("GET /assets/chart-area.js");
            final byte[] bytes = readFile(path);
            return HttpResponse.ofByteArray(HttpStatus.OK, ContentType.JAVASCRIPT, bytes);
        }
        if ("/assets/chart-bar.js".equals(path)) {
            log.info("GET /assets/chart-bar.js");
            final byte[] bytes = readFile(path);
            return HttpResponse.ofByteArray(HttpStatus.OK, ContentType.JAVASCRIPT, bytes);
        }
        if ("/favicon.ico".equals(path)) {
            log.info("GET /favicon.ico");
            final byte[] bytes = readFile(path);
            return HttpResponse.ofByteArray(HttpStatus.OK, ContentType.IMAGE, bytes);
        }

        log.info(path);
        byte[] bytes = "Hello world!".getBytes();
        return HttpResponse.ofByteArray(HttpStatus.OK, bytes);
    }
}
