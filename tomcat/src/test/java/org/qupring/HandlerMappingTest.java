package org.qupring;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import org.apache.http.HttpMethod;
import org.junit.jupiter.api.Test;
import org.qupring.mvc.annotation.Route;
import org.qupring.mvc.handler.HandlerMapping;
import org.qupring.mvc.handler.MappingTarget;

public class HandlerMappingTest {

    private final HandlerMapping handlerMapping = new HandlerMapping();

    @Test
    void 컨트롤러_매핑_테스트() {
        // given
        List<MappingTarget> mappingTargets = List.of(
                new MappingTarget("/test", HttpMethod.GET)
        );

        // when
        handlerMapping.addControllerMappings(List.of(TestController.class));

        // then
        assertThat(mappingTargets.size()).isEqualTo(1);
        assertThat(mappingTargets.get(0).path()).isEqualTo("/test");
        assertThat(mappingTargets.get(0).method()).isEqualTo(HttpMethod.GET);
    }

    class TestController {
        @Route(path = "/test", method = HttpMethod.GET)
        public String test() {
            return "test";
        }
    }
}
