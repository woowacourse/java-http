package support;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DynamicMethodInvokeTest {

    @Test
    void parse() throws Exception {
        final SampleParser targetObject = new SampleParser();
        final Method method = SampleParser.class.getDeclaredMethod("parse", String.class);
        method.setAccessible(true);

        final Map result = (Map) method.invoke(targetObject, "/login?account=philz&password=1234");

        assertAll(
                () -> assertThat(result.get("account")).isEqualTo("philz"),
                () -> assertThat(result.get("password")).isEqualTo("1234")
        );
    }

    /**
     * Reflection Utils 테스트
     */
    @Test
    void parse_by_reflection() {
        final Map result = (Map) DynamicMethodInvoke
                .builder()
                .objectType(SampleParser.class)
                .methodName("parse")
                .parameters("/login?account=philz&password=1234")
                .execute();

        assertAll(
                () -> assertThat(result.get("account")).isEqualTo("philz"),
                () -> assertThat(result.get("password")).isEqualTo("1234")
        );
    }

    static class SampleParser {
        private static Map<String, String> parse(final String uri) {
            final String[] queryStringArr = uri.split("\\?")[1].split("\\&");

            return stream(queryStringArr)
                    .map(it -> it.split("\\="))
                    .collect(Collectors.toMap(it -> it[0], it -> it[1]));
        }
    }
}