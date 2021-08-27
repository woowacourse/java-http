package nextstep.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class HomeControllerTest {

    @Test
    @DisplayName("index.html 불러오기")
    void index() {
        // given
        // when
        ExtractableResponse<Response> response = index_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
    }

    private ExtractableResponse<Response> index_요청() {
        return given()
                .log().all()
                .when()
                .get("/index.html")
                .then()
                .log().all()
                .extract();
    }


}
