package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    @DisplayName("Query String 으로 QueryParams 객체를 만든다.")
    void createQueryParams() {
        final String name = "alex";
        final String age = "7";
        final QueryParams queryParams = QueryParams.from("name=" + name +"&age=" + age);

        assertAll(
                () -> assertThat(queryParams.getValue("name").get()).isEqualTo(name),
                () -> assertThat(queryParams.getValue("age").get()).isEqualTo(age)
        );
    }
}