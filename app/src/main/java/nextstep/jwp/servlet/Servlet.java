package nextstep.jwp.servlet;

import nextstep.jwp.request.RequestParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class Servlet {
    protected OutputStream outputStream;

    public static Servlet of(InputStream inputStream, OutputStream outputStream) throws IOException {
        final RequestParser requestParser = new RequestParser(inputStream);
        return null;
//        return requestParser.parse().chooseServlet(outputStream);
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    protected String readFile(String filePath) throws IOException {
        final List<String> fileLines = readFileByLine(filePath);
        final StringBuilder response = new StringBuilder();
        for (String fileLine : fileLines) {
            response.append(fileLine).append("\r\n");
        }
        return response.toString();
    }

    private List<String> readFileByLine(String filePath) throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(filePath)).getFile());
        final String path = URLDecoder.decode(file.getAbsolutePath(), StandardCharsets.UTF_8);
        return Files.readAllLines(Path.of(path));
    }

    public abstract void response() throws IOException;
}
