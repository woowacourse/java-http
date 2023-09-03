package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LoginGetResponseMaker implements ResponseMaker{

    @Override
    public String createResponse(final String request) throws Exception {
        String[] requestLines = request.split("\\s+");

//        if (requestLines.length < 2) {
//            throw new UncheckedServletException(new Exception("예외"));
//        }

        String resourcePath = requestLines[1]+".html";

        HttpResponse httpResponse = new HttpResponse(StatusCode.OK,ContentType.from(resourcePath),new String(getResponseBodyBytes(resourcePath), UTF_8));
        return httpResponse.getResponse();
    }

    private byte[] getResponseBodyBytes(String resourcePath) throws IOException {
        final URL fileUrl = this.getClass().getClassLoader().getResource("static"+resourcePath);
        return Files.readAllBytes(Paths.get(fileUrl.getPath()));
    }
}
