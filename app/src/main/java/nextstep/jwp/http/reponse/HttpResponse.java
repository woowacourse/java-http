package nextstep.jwp.http.reponse;

import static nextstep.jwp.tomcat.ServletContainer.STATIC_RESOURCE_PATH;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.http.request.HttpHeader;

public class HttpResponse {

    public static final String NEW_LINE = "\r\n";

    private HttpStatusLine httpStatusLine;
    private HttpHeader httpHeader = new HttpHeader();
    private String httpBody;

    public String getValue(){
        String response = httpStatusLine.getValue() + NEW_LINE + httpHeader.getValue();
        if (httpBody != null) {
            response += NEW_LINE + NEW_LINE + httpBody;
        }
        return response;
    }

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatusLine = new HttpStatusLine(httpStatus);
    }

    public void setHeader(String name, String value) {
        httpHeader.setHeader(name, value);
    }

    public void addBody(byte[] data) {
        setHeader("Content-Length", String.valueOf(data.length));
        this.httpBody = new String(data, Charset.forName("UTF-8"));
    }

    public void addFile(String fileName) throws IOException {
        URL resource = getClass().getResource(STATIC_RESOURCE_PATH + fileName);
        addFileContentType(fileName);
        addBody(readFile(resource));
    }

    private byte[] readFile(URL resource) throws IOException {
        Path path = new File(resource.getPath()).toPath();
        return Files.readAllBytes(path);
    }

    private void addFileContentType(String fileName) {
        setHeader("Content-Type", ContentType.matchByFileExtension(fileName));
    }

}
