package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class ResponseHandler {

	private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

	public static void printFileResource(String fileName, OutputStream outputStream) {
		final URL url = ResponseHandler.class.getClassLoader().getResource(fileName);
		if (url == null) {
			sendNotFoundResponse(outputStream);
			return;
		}
		try {
			File file = new File(url.getPath());
			String contentType = determineContentType(fileName);
			String responseBody = new String(Files.readAllBytes(file.toPath()));
			String response = String.join("\r\n",
				"HTTP/1.1 200 OK",
				"Content-Type: " + contentType + ";charset=utf-8",
				"Content-Length: " + responseBody.getBytes().length,
				"",
				responseBody);
			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String determineContentType(String fileName) {
		if (fileName.endsWith("css")) {
			return "text/css";
		}
		if (fileName.endsWith("js")) {
			return "application/javascript";
		}
		if(fileName.endsWith("html")) {
			return "text/html";
		}
		return "application/json";
	}

	private static void sendNotFoundResponse(OutputStream outputStream) {
		try {
			String response = "HTTP/1.1 404 Not Found \r\n" +
				"Content-Length: 0 \r\n" +
				"\r\n";
			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void redirect(String location, OutputStream outputStream) {
	    try {
	        String contentType = "text/html";
	        var response = "HTTP/1.1 302 Found \r\n" +
	            "Location: " + location + "\r\n" +
	            String.format("Content-Type: %s;charset=utf-8 \r\n", contentType) +
	            "Content-Length: 0";

	        outputStream.write(response.getBytes());
	        outputStream.flush();
	    } catch (IOException | UncheckedServletException e) {
	        log.error(e.getMessage(), e);
		}
	}

	public static void redirectWithSetCookie(String location, String sessionId, OutputStream outputStream) {
	    try {
	        String contentType = "text/html";
	        var response = "HTTP/1.1 302 Found \r\n" +
	            "Set-Cookie: JSESSIONID=" + sessionId + " \r\n" +
	            "Location: " + location + " \r\n" +
	            String.format("Content-Type: %s;charset=utf-8 \r\n", contentType) +
	            "Content-Length: 0";

	        outputStream.write(response.getBytes());
	        outputStream.flush();
	    } catch (IOException | UncheckedServletException e) {
	        log.error(e.getMessage(), e);
	    }
	}
}
