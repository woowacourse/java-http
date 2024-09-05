package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("GET 메서드인지 판별한다.")
    @Test
    void isGet() {
        //given
        HttpMethod get = HttpMethod.GET;
        List<HttpMethod> notGetMethods = Arrays.stream(HttpMethod.values())
                .filter(method -> !HttpMethod.GET.equals(method))
                .toList();

        //when
        boolean result = get.isGet();
        List<Boolean> notGetResults = notGetMethods.stream()
                .map(HttpMethod::isGet)
                .toList();

        //then
        assertAll(
                () -> assertThat(result).isTrue(),
                () -> assertThat(notGetResults).isNotEmpty().allMatch(method -> !method)
        );
    }
}
