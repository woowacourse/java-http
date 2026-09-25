package org.apache.coyote.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import org.apache.coyote.http11.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("요청 매핑")
class RequestMappingTest {

    @Test
    @DisplayName("HTTP 메서드와 경로가 일치하는 Controller를 조회한다")
    void findsControllerByMethodAndPath() {
        // given
        final RequestMapping requestMapping = new RequestMapping(new HashMap<>());
        final RouteKey routeKey = new RouteKey(HttpMethod.GET, "/login");
        final Controller controller = (request, response) -> "/login.html";
        requestMapping.add(routeKey, controller);

        // when & then
        assertThat(requestMapping.getHandler(routeKey)).containsSame(controller);
    }

    @Test
    @DisplayName("경로가 같아도 HTTP 메서드가 다르면 서로 다른 Controller를 조회한다")
    void distinguishesControllersByMethod() {
        // given
        final RequestMapping requestMapping = new RequestMapping(new HashMap<>());
        final RouteKey getRoute = new RouteKey(HttpMethod.GET, "/login");
        final RouteKey postRoute = new RouteKey(HttpMethod.POST, "/login");
        final Controller getController = (request, response) -> "/login.html";
        final Controller postController = (request, response) -> "/index.html";
        requestMapping.add(getRoute, getController);
        requestMapping.add(postRoute, postController);

        // when & then
        assertThat(requestMapping.getHandler(getRoute)).containsSame(getController);
        assertThat(requestMapping.getHandler(postRoute)).containsSame(postController);
    }

    @Test
    @DisplayName("일치하는 매핑이 없으면 빈 Optional을 반환한다")
    void returnsEmptyWhenMappingDoesNotExist() {
        // given
        final RequestMapping requestMapping = new RequestMapping(new HashMap<>());
        final RouteKey unknownRoute = new RouteKey(HttpMethod.GET, "/unknown");

        // when & then
        assertThat(requestMapping.getHandler(unknownRoute)).isEmpty();
    }

    @Test
    @DisplayName("동일한 HTTP 메서드와 경로를 중복 등록하면 예외를 던진다")
    void throwsExceptionWhenRegisteringDuplicateMapping() {
        // given
        final RequestMapping requestMapping = new RequestMapping(new HashMap<>());
        final RouteKey routeKey = new RouteKey(HttpMethod.GET, "/login");
        requestMapping.add(routeKey, (request, response) -> "/login.html");

        // when & then
        assertThatThrownBy(() -> requestMapping.add(routeKey, (request, response) -> "/index.html"))
                .isInstanceOf(IllegalStateException.class);
    }
}
