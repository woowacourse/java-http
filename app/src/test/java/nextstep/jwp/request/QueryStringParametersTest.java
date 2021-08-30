package nextstep.jwp.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class QueryStringParametersTest {

    private QueryStringParameters parameters;

    @BeforeEach
    void setUp() {
        String queryString = "account=charlie&age=30";
        parameters = QueryStringParameters.of(queryString);
    }

    @DisplayName("존재하는 파라미터를 잘 가져온다.")
    @Test
    void getParameter() {
        // given
        String parameterName1 = "account";
        String parameterValue1 = "charlie";

        // when
        String value1 = parameters.getParameter(parameterName1);

        // then
        assertThat(value1).isEqualTo(parameterValue1);

        // given
        String parameterName2 = "age";
        String parameterValue2 = "30";

        // when
        String value2 = parameters.getParameter(parameterName2);

        // then
        assertThat(value2).isEqualTo(parameterValue2);
    }

    @DisplayName("존재하지 않는 파라미터를 가져오려고 하면 예외가 발생한다.")
    @Test
    void getNonExistParameter() {
        // given
        String invalidName = "invalidName";

        // when then
        assertThatThrownBy(() -> parameters.getParameter(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(QueryStringParameters.NON_EXIST_PARAMETER_EXCEPTION_MESSAGE);
    }

    @Test
    void existParameter() {
        // when
        boolean result = parameters.existParameter();

        // then
        assertThat(result).isTrue();

        // given
        QueryStringParameters emptyParameters = QueryStringParameters.getEmptyParameters();

        // when
        boolean result2 = emptyParameters.existParameter();

        // then
        assertThat(result2).isFalse();
    }
}