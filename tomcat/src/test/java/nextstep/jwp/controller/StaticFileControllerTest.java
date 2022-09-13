package nextstep.jwp.controller;

import java.net.URL;
import java.util.List;
import nextstep.jwp.utils.FileUtils;
import org.apache.http.ContentType;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.QueryParams;
import org.apache.http.StatusCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticFileControllerTest {

    @Test
    @DisplayName("Get 메서드는 주소로 파일이름을 받으면 해당하는 정적파일을 반환한다.")
    void handleGet() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/css/styles.css",
            "/css/styles.css", QueryParams.empty(),
            HttpHeaders.parse(List.of("Accept: text/css,*/*;q=0.1")), null);
        StaticFileController staticFileController = new StaticFileController();

        HttpResponse actual = staticFileController.handleGet(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.OK, ContentType.TEXT_CSS,
            FileUtils.readFile(FileUtils.getResource("/css/styles.css")));
        Assertions.assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Get 메서드는 주소로 받은 파일이름에 해당하는 정적파일이 없으면 404 Not Found를 반환한다.")
    void handleGet_not_found() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/css/styles.css",
            "/invalid12345678.html", QueryParams.empty(),
            HttpHeaders.parse(List.of("Accept: text/html,*/*;q=0.1")), null);
        StaticFileController staticFileController = new StaticFileController();

        HttpResponse actual = staticFileController.handleGet(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.NOT_FOUND, ContentType.TEXT_HTML,
            FileUtils.readFile(FileUtils.getResource("/404.html")));
        Assertions.assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
