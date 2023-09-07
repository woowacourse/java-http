package org.apache.catalina.connector;

import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServerXmlParser {

    private static final int SERVER_NODE_LOCATION = 0;
    private static final int CONNECTOR_NODE_LOCATION = 0;

    private ServerXmlParser() {
    }

    public static ConnectorConfiguration parse() {
        Document document = parseServerDocument();
        NodeList server = document.getElementsByTagName("Server");

        Node serverNode = server.item(SERVER_NODE_LOCATION);

        Element serverElement = (Element) serverNode;
        Element connectorElement = (Element) serverElement
                .getElementsByTagName("Connector")
                .item(CONNECTOR_NODE_LOCATION);

        return new ConnectorConfiguration(
                Integer.parseInt(serverElement.getAttribute("port")),
                Integer.parseInt(connectorElement.getAttribute("acceptCount")),
                Integer.parseInt(connectorElement.getAttribute("maxThreads"))
        );
    }

    private static Document parseServerDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ClassLoader classLoader = ServerXmlParser.class.getClassLoader();
            URL resource = classLoader.getResource("server.xml");

            return builder.parse(resource.getPath());
        } catch (Exception e) {
            throw new ServerXmlNotFoundException();
        }
    }
}
