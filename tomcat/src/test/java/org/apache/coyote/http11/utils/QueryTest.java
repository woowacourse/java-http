package org.apache.coyote.http11.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.request.element.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryTest {

    @Test
    @DisplayName("쿼리를 매핑한 후 조회할 수 있다.")
    void find() {
        Query query = Query.of(URI.create("/login?name=hunch&password=1234").getQuery());

        assertThat(query.find("name")).isEqualTo("hunch");
        assertThat(query.find("password")).isEqualTo("1234");
        assertThatThrownBy(() -> query.find("account"))
                .isInstanceOf(NoSuchElementException.class);
    }
}
