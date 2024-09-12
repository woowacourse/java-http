package com.techcourse.web.util;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceLoaderTest {

	@DisplayName("정적 리소스를 로드한다.")
	@Test
	void loadResource() throws IOException {
		ResourceLoader resourceLoader = ResourceLoader.getInstance();
		String filePath = "/index.html";

		HttpResponseBody responseBody = resourceLoader.loadResource(filePath);

		byte[] expected = Files.readAllBytes(Path.of(getClass().getResource(filePath).getPath()));
		assertThat(responseBody).satisfies(body -> {
			assertThat(body.getContentType()).isEqualTo("text/html");
			assertThat(body.getContent()).isEqualTo(expected);
		});
	}

	@DisplayName("파일이 존재하지 않을 경우 예외를 던진다.")
	@Test
	void loadResourceWithNonExistFile() {
		ResourceLoader resourceLoader = ResourceLoader.getInstance();
		String filePath = "/non-exist.html";

		assertThatThrownBy(() -> resourceLoader.loadResource(filePath))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Resource not found. resource: " + "/static" + filePath);
	}
}
