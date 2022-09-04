package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.junit.jupiter.api.Test;

class QueryMapperTest {

    @Test
    void getParameters() throws URISyntaxException {
        URI uri = new URI("http://localhost:8080/login?account=gugu&password=password");

        QueryMapper queryMapper = new QueryMapper(uri);
        Map<String, String> parameters = queryMapper.getParameters();

        assertAll(
                () -> assertThat(parameters.get("account")).isEqualTo("gugu"),
                () -> assertThat(parameters.get("password")).isEqualTo("password")
        );
    }
}