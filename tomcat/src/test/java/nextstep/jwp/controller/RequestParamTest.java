package nextstep.jwp.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestParamTest {

    @Test
    void 일반_쿼리스트링_변환_테스트() {
        RequestParam requestParam = RequestParam.of("nickname=파워&age=26");
        Assertions.assertThat(requestParam.get("nickname")).isEqualTo("파워");
    }

    @Test
    void 인코딩된_쿼리스트링_변환_테스트() {
        RequestParam requestParam = RequestParam.of("nickname%3d%ed%8c%8c%ec%9b%8c%26age%3d26");
        Assertions.assertThat(requestParam.get("nickname")).isEqualTo("파워");
    }
}
