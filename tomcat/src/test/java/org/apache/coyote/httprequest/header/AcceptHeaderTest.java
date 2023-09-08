package org.apache.coyote.httprequest.header;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class AcceptHeaderTest {

    @Test
    void 반점_기준으로_Accept_헤더에_지원_가능한_미디어_타입이_들어간다() {
        // given
        final String mediaTypes = "text/css,*/*;q=0.1";

        // when
        final AcceptHeader acceptHeader = AcceptHeader.from(mediaTypes);

        // then
        assertThat(acceptHeader.getValue()).isEqualTo(mediaTypes);
    }
}
