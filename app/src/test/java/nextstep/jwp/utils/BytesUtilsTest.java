package nextstep.jwp.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BytesUtilsTest {

    @DisplayName("여러 byte[] 를 잇는 새로운 배열을 만든다")
    @Test
    void concat() {
        // given
        byte[] firstBytes = {1, 2, 3};
        byte[] secondBytes = {4, 5, 6};
        byte[] thirdBytes = {7, 8, 9};

        // when
        byte[] bytes = BytesUtils.concat(firstBytes, secondBytes, thirdBytes);

        // then
        assertThat(bytes).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }
}
