package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpResponseTest {

    @Test
    void ResponseEntity_의_응답코드에_따라서_StatusLine을_생성한다() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();

        // when
        httpResponse.responseFrom(responseEntity);

        // then
        assertThat(httpResponse.getStatusLine().toString())
            .hasToString("HTTP/1.1 200 OK");
    }

    @Test
    void ResponseEntity_가_Body가_없다면_Body_는_비어있다() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();

        // when
        httpResponse.responseFrom(responseEntity);

        // then
        assertThat(httpResponse.getBody().getValue()).isEmpty();
    }

    @Test
    void ResponseEntity_에_유효한_viewPath_가_있으면_body에_해당_응답_Body_가_있다() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();
        responseEntity.responseView("/index.html");

        // when
        httpResponse.responseFrom(responseEntity);

        // then
        assertThat(httpResponse.getBody().getValue()).isPresent();
    }

    @Test
    void ResponseEntity_에_유효하지_viewPath_가_있으면_예외_발생() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();
        responseEntity.responseView("/pooh.html");

        // when & then
        assertThatThrownBy(() -> httpResponse.responseFrom(responseEntity))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("파일이 존재하지 않습니다.");
    }

    @Test
    void ResponseEntity_의_Body_를_넣은_응답은_아직_만들지_않아서_예외() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200)
            .body(new Pooh("pooh"));

        // when & then
        assertThatThrownBy(() -> httpResponse.responseFrom(responseEntity))
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("ResponseBody 에 Body 가 들어있는 경우는 아직 만들지 않았습니다.");
    }

    @Test
    void Body_가_있으면_ContentType_과_ContentLength_헤더가_있다() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();
        responseEntity.responseView("/index.html");

        // when
        httpResponse.responseFrom(responseEntity);

        // then
        assertThat(httpResponse.getHeaders().getHeaders())
            .containsKeys("Content-Length", "Content-Type");
    }

    @Test
    void ResponseEntity_에서_추가한_헤더도_있다() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).
            location("index.html").build();
        responseEntity.responseView("/index.html");

        // when
        httpResponse.responseFrom(responseEntity);

        // then
        assertThat(httpResponse.getHeaders().getHeaders())
            .containsKeys("Content-Length", "Content-Type", "Location");
    }

    private class Pooh {

        private final String name;

        public Pooh(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
