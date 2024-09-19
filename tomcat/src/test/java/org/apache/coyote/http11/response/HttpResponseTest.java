package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.common.HeaderKey;
import org.apache.coyote.http11.common.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {
	@DisplayName("파일의 내용을 Body로 세팅한다.")
	@Test
	void setBodyByFileName() throws IOException {
		HttpResponse response = new HttpResponse();

		response.setBodyByFileName("static/index.html");

		Headers headers = response.getHeaders();
		assertThat(headers.getValue(HeaderKey.CONTENT_LENGTH)).isEqualTo(String.valueOf(5676));
		assertThat(headers.getValue(HeaderKey.CONTENT_TYPE)).isEqualTo("text/html;charset=utf-8");
	}

	@DisplayName("존재하지 않는 파일 입력으로 파일의 내용을 Body로 세팅하는 것에 실패한다.")
	@Test
	void failToSetBodyByFileName() throws IOException {
		HttpResponse response = new HttpResponse();

		response.setBodyByFileName("static/none.html");

		assertThat(response.getStatusCode()).isEqualTo(new StatusCode(404));
		assertThat(response.getStatusMessage()).isEqualTo(new StatusMessage("NOT FOUND"));
	}
}