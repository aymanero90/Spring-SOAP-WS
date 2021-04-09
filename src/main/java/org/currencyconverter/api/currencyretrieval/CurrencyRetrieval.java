package org.currencyconverter.api.currencyretrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CurrencyRetrieval {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyRetrieval.class);
    private static final String URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    private static final String XML_CURRENCY_NODES_PATH = "//*/Cube/Cube/Cube";
    private static final int XML_CURRENCY_CODE_ATTRIBUTE_INDEX = 0;
    private static final int XML_CURRENCY_RATE_ATTRIBUTE_INDEX = 1;

    private static DocumentBuilder documentBuilder;
    private static NodeList currencyNodeList;

    private CurrencyRetrieval() {

    }

    public static Map<String, Double> retrieveCurrencyRates() {
        if (!build() || !retrieveCurrencyNodes()) {
            return null;
        }

        try {
            Map<String, Double> currencyMap = IntStream.range(0, currencyNodeList.getLength())
                    .mapToObj(currencyNodeList::item)
                    .collect(Collectors.toMap(
                            CurrencyRetrieval::getKey,
                            CurrencyRetrieval::getValue
                    ));
            LOGGER.info("The currency rates were retrieved: {}", currencyMap);
            return currencyMap;
        } catch (NumberFormatException e) {
            LOGGER.error("Number format went wrong", e);
            return null;
        }
    }

    public static List<String> retrieveCurrencyList() {
        if (!build() || !retrieveCurrencyNodes()) {
            return Collections.emptyList();
        }

        List<String> currencyList = IntStream.range(0, currencyNodeList.getLength())
                .mapToObj(currencyNodeList::item)
                .map(CurrencyRetrieval::getKey)
                .collect(Collectors.toList());
        LOGGER.info("The currency was retrieved: {}", currencyList);
        return currencyList;

    }

    private static String getKey(Node item) {
        return item.getAttributes().item(XML_CURRENCY_CODE_ATTRIBUTE_INDEX).getNodeValue();  //  currency code
    }

    private static double getValue(Node item) throws NumberFormatException {
        return Double.parseDouble(item.getAttributes().item(XML_CURRENCY_RATE_ATTRIBUTE_INDEX).getNodeValue());   // currency rate
    }

    private static boolean build() {
        try {
            // to prevent XXE attack https://stackoverflow.com/a/44258600
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            documentBuilder = factory.newDocumentBuilder();
            return true;
        } catch (ParserConfigurationException e) {
            LOGGER.error("Build the document went wrong", e);
            return false;
        }
    }

    private static boolean retrieveCurrencyNodes() {
        if (documentBuilder == null) {
            return false;
        }

        try {
            XPath xmlPath = XPathFactory.newInstance().newXPath();
            Document doc = documentBuilder.parse(new URL(URL).openStream());
            currencyNodeList = (NodeList) xmlPath.compile(XML_CURRENCY_NODES_PATH).evaluate(doc, XPathConstants.NODESET);
            return true;
        } catch (SAXException | IOException | XPathExpressionException e) {
            LOGGER.error("Retrieve currency nodes failed", e);
            return false;
        }
    }
}
