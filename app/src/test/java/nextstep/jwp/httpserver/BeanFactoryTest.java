package nextstep.jwp.httpserver;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.mapping.HandlerMapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BeanFactory 테스트")
class BeanFactoryTest {

    @Test
    @DisplayName("클래스 타입으로 해당 클래스 타입 인스턴스 불러오기")
    void findByClassType() {
        // given
        BeanFactory.init();

        // when
        Map<String, HandlerMapping> handlerMappings = BeanFactory.findByClassType(HandlerMapping.class);

        // then
        assertThat(handlerMappings).isNotEmpty();
    }
}
