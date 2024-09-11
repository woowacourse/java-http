package hoony.was;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceReturnValueResolverTest {

    @Test
    @DisplayName("문자열 리턴 타입만을 지원한다.")
    void checkSupportingTypes() {
        StaticResourceReturnValueResolver resolver = new StaticResourceReturnValueResolver();
        assertAll(
                () -> assertThat(resolver.supportsReturnType(String.class)).isTrue(),
                () -> assertThat(resolver.supportsReturnType(Integer.class)).isFalse()
        );
    }

    @Test
    @DisplayName("redirect:로 시작하면 리다이렉트 응답을 반환한다.")
    void checkRedirectResponse() {
        StaticResourceReturnValueResolver resolver = new StaticResourceReturnValueResolver();
        String redirectPath = "redirect:/index.html";
        HttpResponse response = new HttpResponse();
        resolver.resolve(null, response, redirectPath);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(StatusCode.FOUND),
                () -> assertThat(response.getHeader("Location")).isEqualTo("/index.html")
        );
    }

    @Test
    @DisplayName("정적 리소스를 읽어서 응답을 반환한다.")
    void checkStaticResourceResponse() {
        StaticResourceReturnValueResolver resolver = new StaticResourceReturnValueResolver();
        String staticResourcePath = "/index.html";
        HttpResponse response = new HttpResponse();
        resolver.resolve(null, response, staticResourcePath);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(StatusCode.OK),
                () -> assertThat(response.getContent()).contains("<!DOCTYPE html>")
        );
    }
}
