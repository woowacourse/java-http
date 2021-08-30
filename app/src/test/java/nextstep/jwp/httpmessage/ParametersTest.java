package nextstep.jwp.httpmessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Parameters 기능 테스트")
class ParametersTest {

    private Map<String, String> mapParameters;

    @BeforeEach
    void setUp() {
        mapParameters = Map.ofEntries(
                Map.entry("account", "gumgum"),
                Map.entry("password", "password2"),
                Map.entry("email", "ggump%40woowahan.com")
        );
    }

    @Test
    void getParameterNames() {
        //given
        //when
        final Parameters parameters = new Parameters(mapParameters);
        final Enumeration<String> parameterNames = parameters.getParameterNames();
        //then
        assertThat(parameterNames.nextElement()).isNotNull();
        assertThat(parameterNames.nextElement()).isNotNull();
        assertThat(parameterNames.nextElement()).isNotNull();
    }

    @Test
    void getNoParameterNamesWhenParameterEmpty() {
        //given
        final Map<String, String> mapParameters = Collections.emptyMap();
        //when
        final Parameters parameters = new Parameters(mapParameters);
        final Enumeration<String> parameterNames = parameters.getParameterNames();
        //then
        assertThat(parameterNames.hasMoreElements()).isFalse();
    }

    @Test
    void getParameter() {
        //given
        //when
        final Parameters parameters = new Parameters(mapParameters);
        final String parameter = parameters.getParameter("account");
        //then
        assertThat(parameter).isEqualTo("gumgum");
    }

    @Test
    void getNoParameterWhenNotFoundParameter() {
        //given
        //when
        final Parameters parameters = new Parameters(mapParameters);
        final String parameter = parameters.getParameter("age");
        //then
        assertThat(parameter).isNull();
    }
}