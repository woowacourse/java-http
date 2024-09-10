package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class ResponseHandler {

	private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

	public static void printFileResource(String fileName, OutputStream outputStream) {
		URL url = ResponseHandler.class.getClassLoader().getResource(fileName);
		try {
			HttpResponse httpResponse = generateHttpResponse(url);
			outputStream.write(httpResponse.toPlainText().getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static HttpResponse generateHttpResponse(URL url) throws IOException {
		if (url == null) {
			return new HttpResponse(
				"HTTP/1.1", 404, "Not Found", Map.of("Content-Length", "0"));
		}
		File file = new File(url.getPath());
		ContentType contentType = ContentType.fromPath(url.getPath());
		Body body = new Body(new String(Files.readAllBytes(file.toPath())));
		return new HttpResponse("HTTP/1.1", 200, "OK",
			Map.of("Content-Type", contentType + ";charset=utf-8",
				"Content-Length", String.valueOf(body.getContentLength())),
			body.getValue());
	}

	public static void redirect(String path, OutputStream outputStream) {
		try {
			String contentType = "text/html";
			var response = "HTTP/1.1 302 Found \r\n" +
				"Location: http://localhost:8080" + path + "\r\n" +
				String.format("Content-Type: %s;charset=utf-8 \r\n", contentType) +
				"Content-Length: 0";

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void redirectWithSetCookie(String path, String sessionId, OutputStream outputStream) {
		try {
			String contentType = "text/html";
			var response = "HTTP/1.1 302 Found \r\n" +
				"Set-Cookie: JSESSIONID=" + sessionId + " \r\n" +
				"Location: http://localhost:8080" + path + " \r\n" +
				String.format("Content-Type: %s;charset=utf-8 \r\n", contentType) +
				"Content-Length: 0";

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}
