package com.clock.main;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import utils.SourceDataGetUtil;

public class Test {

    public static void main(String[] args) throws FileNotFoundException, DocumentException, URISyntaxException {
        SAXReader sReader = new SAXReader();
        Document document = sReader.read(SourceDataGetUtil.loadFile("conf.xml"));
        Element root = document.getRootElement();
        Iterator<Element> iterator = root.elementIterator("少女");
        while (iterator.hasNext()) {
            Element e = iterator.next();
            System.out.println(e.getName());
        }
    }
}
