package org.apache.catalina.session.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import org.apache.catalina.routing.RequestHandler;
import org.apache.catalina.routing.RequestRegistry;
import org.apache.catalina.routing.RouteKey;
import org.apache.coyote.http11.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("요청 매핑")
class RequestRegistryTest {

    @Test
    @DisplayName("HTTP 메서드와 경로가 일치하는 RequestHandler를 조회한다")
    void findsRequestHandlerByMethodAndPath() {
        // given
        final RequestRegistry requestRegistry = new RequestRegistry(new HashMap<>());
        final RouteKey routeKey = new RouteKey(HttpMethod.GET, "/login");
        final RequestHandler requestHandler = (request, response) -> "/login.html";
        requestRegistry.add(routeKey, requestHandler);

        // when & then
        assertThat(requestRegistry.getHandler(routeKey)).containsSame(requestHandler);
    }

    @Test
    @DisplayName("경로가 같아도 HTTP 메서드가 다르면 서로 다른 RequestHandler를 조회한다")
    void distinguishesRequestHandlersByMethod() {
        // given
        final RequestRegistry requestRegistry = new RequestRegistry(new HashMap<>());
        final RouteKey getRoute = new RouteKey(HttpMethod.GET, "/login");
        final RouteKey postRoute = new RouteKey(HttpMethod.POST, "/login");
        final RequestHandler getRequestHandler = (request, response) -> "/login.html";
        final RequestHandler postRequestHandler = (request, response) -> "/index.html";
        requestRegistry.add(getRoute, getRequestHandler);
        requestRegistry.add(postRoute, postRequestHandler);

        // when & then
        assertThat(requestRegistry.getHandler(getRoute)).containsSame(getRequestHandler);
        assertThat(requestRegistry.getHandler(postRoute)).containsSame(postRequestHandler);
    }

    @Test
    @DisplayName("일치하는 매핑이 없으면 빈 Optional을 반환한다")
    void returnsEmptyWhenMappingDoesNotExist() {
        // given
        final RequestRegistry requestRegistry = new RequestRegistry(new HashMap<>());
        final RouteKey unknownRoute = new RouteKey(HttpMethod.GET, "/unknown");

        // when & then
        assertThat(requestRegistry.getHandler(unknownRoute)).isEmpty();
    }

    @Test
    @DisplayName("동일한 HTTP 메서드와 경로를 중복 등록하면 예외를 던진다")
    void throwsExceptionWhenRegisteringDuplicateMapping() {
        // given
        final RequestRegistry requestRegistry = new RequestRegistry(new HashMap<>());
        final RouteKey routeKey = new RouteKey(HttpMethod.GET, "/login");
        requestRegistry.add(routeKey, (request, response) -> "/login.html");

        // when & then
        assertThatThrownBy(() -> requestRegistry.add(routeKey, (request, response) -> "/index.html"))
                .isInstanceOf(IllegalStateException.class);
    }
}
