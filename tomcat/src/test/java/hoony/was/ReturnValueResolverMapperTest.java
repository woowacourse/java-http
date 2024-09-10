package hoony.was;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReturnValueResolverMapperTest {

    @Test
    @DisplayName("매치되는 Resolver가 존재하지 않는 경우, 예외를 발생한다.")
    void resolverNotFound() {
        ReturnValueResolverMapper returnValueResolverMapper = new ReturnValueResolverMapper();
        assertThatThrownBy(() -> returnValueResolverMapper.resolve(Object.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No ReturnValueResolver found for returnType");
    }

    @Test
    @DisplayName("매치되는 Resolver가 존재하는 경우, 정상적으로 처리한다.")
    void resolverFound() {
        ReturnValueResolverMapper returnValueResolverMapper = new ReturnValueResolverMapper();
        ReturnValueResolver resolver = new ReturnValueResolver() {
            @Override
            public boolean supportsReturnType(Class<?> returnType) {
                return returnType == String.class;
            }

            @Override
            public HttpResponse resolve(Object returnValue) {
                return HttpResponse.builder()
                        .ok()
                        .build();
            }
        };
        returnValueResolverMapper.register(resolver);
        HttpResponse actual = returnValueResolverMapper.resolve("ReturnValueAsString");
        assertThat(actual.getStatusCode()).isEqualTo(StatusCode.OK);
    }
}
