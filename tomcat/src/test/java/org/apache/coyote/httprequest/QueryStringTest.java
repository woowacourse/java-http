package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidQueryStringFormatException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class QueryStringTest {

    @Test
    void 잘못된_쿼리_스트링_형식이면_생성에_실패한다() {
        // given
        final String queryStringInput = "age+11;city+seoul";

        // when, then
        assertThatThrownBy(() -> QueryString.from(queryStringInput))
                .isInstanceOf(InvalidQueryStringFormatException.class);
    }
}
