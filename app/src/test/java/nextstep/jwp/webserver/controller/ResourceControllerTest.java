package nextstep.jwp.webserver.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceControllerTest {

    private static final String RESOURCE = "nextstep.txt";

    @DisplayName("리소스 파일이 존재할 경우 true 반환 테스트")
    @Test
    void canHandleIfResourceExists() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, RESOURCE, HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final ResourceController resourceController = new ResourceController();

        // when
        boolean canHandle = resourceController.canHandle(httpRequest);

        //then
        assertThat(canHandle).isTrue();
    }

    @DisplayName("리소스 파일이 존재하지 않을 경우 false 반환 테스트")
    @Test
    void cannotHandleIfResourceDoesNotExists() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "없는_파일.txt", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final ResourceController resourceController = new ResourceController();

        // when
        boolean canHandle = resourceController.canHandle(httpRequest);

        //then
        assertThat(canHandle).isFalse();
    }

    @Test
    @DisplayName("정적 파일 반환 테스트")
    public void doGetTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, RESOURCE, HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final ResourceController resourceController = new ResourceController();

        // when
        final HttpResponse httpResponse = resourceController.handle(httpRequest);

        //then
        final HttpResponse expected = new ResourceResponseTemplate().ok(RESOURCE);
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }
}
