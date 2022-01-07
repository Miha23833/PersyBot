package com.persybot.db.sql.sourcereader.impl;

import com.persybot.db.sql.sourcereader.SqlSource;
import com.persybot.logger.impl.PersyBotLogger;
import org.apache.logging.log4j.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class XmlSqlSource implements SqlSource {
    private final Map<String, Map<String, String>> queries;
    private final String sqlFileDir;
    public XmlSqlSource(String xmlPath, String sqlFileDir) throws ParserConfigurationException, IOException, SAXException {
        if (!new File(sqlFileDir).isDirectory()) {
            throw new InvalidPathException(Paths.get("").toAbsolutePath() + sqlFileDir, "Path is not directory");
        }
        if (!sqlFileDir.endsWith("/")) {
            sqlFileDir = sqlFileDir + "/";
        }
        this.sqlFileDir = sqlFileDir;

        queries = new HashMap<>();
        loadQueriesFromDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlPath));
    }

    @Override
    public String getQuery(String entity, String queryName) {
        try {
            return this.queries.get(entity).get(queryName);
        } catch (Exception e) {
            PersyBotLogger.BOT_LOGGER.fatal("Cannot get query of Entity " + entity + " and query name " + queryName, e.getStackTrace(), e);
            throw e;
        }
    }

    private void loadQueriesFromDocument(Document document) {
        NodeList queryNodes = document.getDocumentElement().getElementsByTagName("SQL");

        int queryNodesCount = queryNodes.getLength();

        for (int i = 0; i < queryNodesCount; i++) {
            Node queryNode = queryNodes.item(i);
            String entityName = queryNode.getParentNode().getNodeName();
            String queryId = queryNode.getAttributes().getNamedItem("id").getNodeValue();
            String query = null;

            String filename = null;
            Optional<Node> filenameAttribute = Optional.ofNullable(queryNode.getAttributes().getNamedItem("file"));
            if (filenameAttribute.isPresent()) {
                filename = filenameAttribute.get().getNodeValue();
            }

            if (filename != null) {
                try {
                    query = String.join("\n", Files.readAllLines(Paths.get(sqlFileDir + filename)));
                } catch (IOException e) {
                    PersyBotLogger.BOT_LOGGER.error(e);
                }
            } else {
                query = queryNode.getFirstChild().getNodeValue();
            }

            if (Strings.isBlank(query)) {
                StringBuilder msg = new StringBuilder()
                        .append("Cannot get query with entity name = ")
                        .append(entityName)
                        .append(", queryId = ")
                        .append(queryId);

                if (filename != null) {
                    msg.append(", filename = ").append(filename);
                }

                PersyBotLogger.BOT_LOGGER.error(msg.toString());
                break;
            }

            queries.computeIfAbsent(entityName, k -> new HashMap<>())
                    .put(queryId, query);
        }
    }
}
