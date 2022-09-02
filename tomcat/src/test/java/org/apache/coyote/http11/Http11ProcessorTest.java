package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.Test;
import support.DynamicMethodInvoke;

class Http11ProcessorTest {

    @Test
    void parseUserMap() throws Exception {
        final Http11Processor processor = new Http11Processor();
        final Method method = Http11Processor.class.getDeclaredMethod("parseUserMap", String.class);
        method.setAccessible(true);

        final Map<String, String> result = (Map<String, String>) method.invoke(processor,
                "/login?account=philz&password=1234");

        assertAll(
                () -> assertThat(result.get("account")).isEqualTo("philz"),
                () -> assertThat(result.get("password")).isEqualTo("1234")
        );
    }

    /**
     * Reflection Utils 테스트
     */
    @Test
    void parseUserMap_by_reflection() {
        final Map<String, String> result = (Map<String, String>) DynamicMethodInvoke
                .builder()
                .objectType(Http11Processor.class)
                .methodName("parseUserMap")
                .parameters("/login?account=philz&password=1234")
                .execute();

        assertAll(
                () -> assertThat(result.get("account")).isEqualTo("philz"),
                () -> assertThat(result.get("password")).isEqualTo("1234")
        );
    }
}