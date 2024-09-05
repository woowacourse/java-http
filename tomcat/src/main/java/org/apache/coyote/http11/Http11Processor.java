package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceProcessor resourceProcessor;
    private final ApiProcessor apiProcessor;

    public Http11Processor(final Socket connection) {
        this.resourceProcessor = new ResourceProcessor();
        this.apiProcessor = new ApiProcessor();
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            Map<String, String> httpRequestHeaders = new HashMap<>();
            String requestTitle = br.readLine();
            if (requestTitle == null) {
                return;
            }
            log.info(requestTitle);
            StringTokenizer st = new StringTokenizer(requestTitle);
            MethodType requestMethodType = MethodType.toMethodType(st.nextToken());
            String requestPath = st.nextToken();
            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }

            // header
            String header = br.readLine();
            while (!"".equals(header)) {
                String[] splitHeader = header.split(": ");
//                log.info(splitHeader[0]+": "+splitHeader[1]);
                httpRequestHeaders.put(splitHeader[0], splitHeader[1]);
                header = br.readLine();
            }

            // body
            Map<String, String> requestBody = new HashMap<>();
            if (requestMethodType == MethodType.POST) {
                int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                br.read(buffer, 0, contentLength);
                String[] requestBodyParams = new String(buffer).split("&");
                for (String requestBodyParam : requestBodyParams) {
                    String[] paramEntry = requestBodyParam.split("=");
                    requestBody.put(paramEntry[0], paramEntry[1]);
                }
            }

            RequestPathType requestPathType = RequestPathType.reqeustPathToRequestPathType(requestPath);

            if (requestPathType.isAPI()) {
                apiProcessor.process(connection, requestPath, requestMethodType, httpRequestHeaders, requestBody);
            }
            if (requestPathType.isResource()) {
                resourceProcessor.process(connection, requestPath);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
