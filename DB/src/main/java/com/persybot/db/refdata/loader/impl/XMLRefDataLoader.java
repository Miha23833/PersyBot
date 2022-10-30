package com.persybot.db.refdata.loader.impl;

import com.persybot.db.entity.DBEntity;
import com.persybot.db.refdata.loader.RefDataLoader;
import com.persybot.db.service.DBService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class XMLRefDataLoader<T extends DBEntity> implements RefDataLoader<T> {
    private final DBService storage;
    private final String refDataPath;
    private final String elementNodeName;
    private final Class<T> dataClass;

    protected XMLRefDataLoader(DBService storage, String refDataFilePath, String elementNodeName, Class<T> dataClass) {
        this.storage = storage;
        this.elementNodeName = elementNodeName;
        this.dataClass = dataClass;

        this.refDataPath = refDataFilePath;
    }

    @Override
    public final void loadRefData() {
        File refDataFile = new File(this.refDataPath);

        if (!refDataFile.exists()) {
            throw new RuntimeException("Could not find " + this.refDataPath + " to load ref data");
        }

        Document refDataDoc;
        try {
            refDataDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(refDataFile);
            refDataDoc.normalize();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        NodeList xmlEntities = refDataDoc.getElementsByTagName(elementNodeName);

        List<T> parsedEntities = new ArrayList<>();
        for (int i = 0; i < xmlEntities.getLength(); i++) {
            try {
                if (xmlEntities.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    parsedEntities.add(createInstanceFromXmlNode((Element) xmlEntities.item(i)));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        storage.create(parsedEntities, dataClass);
    }

    protected abstract T createInstanceFromXmlNode(Element node);
}
