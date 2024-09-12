package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;

public class ResponsePrinter {

	private final OutputStream outputStream;

	public ResponsePrinter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void print(HttpResponse response) throws IOException {
		String plainText = response.toPlainText();
		try {
			outputStream.write(plainText.getBytes());
			outputStream.flush();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
