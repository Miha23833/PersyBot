package com.persybot.db.refdata.loader.impl;

import com.persybot.db.entity.EqualizerPreset;
import com.persybot.db.service.DBService;
import org.w3c.dom.Element;

import java.util.Arrays;

public class EqualizerPresetXmlLoader extends XMLRefDataLoader<EqualizerPreset> {
    public EqualizerPresetXmlLoader(DBService storage, String refDataFilePath) {
        super(storage, refDataFilePath, "EqualizerPreset", EqualizerPreset.class);
    }

    @Override
    protected EqualizerPreset createInstanceFromXmlNode(Element node) {
        String name = node.getElementsByTagName("Name").item(0).getTextContent();

        String[] bandsContent = node.getElementsByTagName("Bands").item(0).getTextContent().split(",");
        Integer[] bands = Arrays.stream(bandsContent).map(Integer::parseInt).toArray(Integer[]::new);

        return new EqualizerPreset(name, bands);
    }
}
