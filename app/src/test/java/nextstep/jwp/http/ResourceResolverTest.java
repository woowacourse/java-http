package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.Fixture;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.entity.HttpStatus;
import nextstep.jwp.http.entity.HttpUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ResourceResolverTest {

    @ParameterizedTest
    @CsvSource({"index.html", "js/scripts.js", "css/style.css"})
    void checkIfUriHasResourceExtension(String uriName) {
        HttpUri httpUri = HttpUri.of(uriName);
        boolean actual = ResourceResolver.checkIfUriHasResourceExtension(httpUri);

        assertTrue(actual);
    }

    @Test
    @DisplayName("정의되지 않은 리소스 extension - false")
    void checkIfUriHasResourceExtensionFalse() {
        boolean actual = ResourceResolver.checkIfUriHasResourceExtension(HttpUri.of("notExtension.notExtension"));

        assertFalse(actual);
    }

    @Test
    @DisplayName("정상 리소스 요청")
    void resolveResourceRequest() throws IOException {
        String uri = "/index.html";
        HttpRequest httpRequest = Fixture.httpRequest("GET", uri);
        HttpResponse httpResponse = HttpResponse.empty();

        final URL resource = ResourceResolver.class.getClassLoader().getResource("static" + uri);
        final Path path = new File(Objects.requireNonNull(resource).getPath()).toPath();

        String responseBody = Files.readString(path);

        ResourceResolver.resolveResourceRequest(httpRequest, httpResponse);

        assertThat(httpResponse.httpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(httpResponse.httpHeaders().get("Content-Type")).isEqualTo("text/html;charset=utf-8");
        assertThat(httpResponse.httpBody().body()).isEqualTo(responseBody);

    }

    @Test
    @DisplayName("존재 하지 않는 리소스 요청 - 에러 발생")
    void resolveResourceRequestNotExisting() {
        String uri = "/notExisting.html";
        HttpRequest httpRequest = Fixture.httpRequest("GET", uri);
        HttpResponse httpResponse = HttpResponse.empty();

        assertThatThrownBy(
                () -> ResourceResolver.resolveResourceRequest(httpRequest, httpResponse)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Post 리소스 요청 - 에러 발생")
    void resolveResourceRequestWithPost() {
        String uri = "/index.html";
        HttpRequest httpRequest = Fixture.httpRequest("POST", uri);
        HttpResponse httpResponse = HttpResponse.empty();

        assertThatThrownBy(
                () -> ResourceResolver.resolveResourceRequest(httpRequest, httpResponse)
        ).isInstanceOf(MethodNotAllowedException.class);
    }
}
