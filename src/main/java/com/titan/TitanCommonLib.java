package com.titan;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSONObject;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.titan.mainframe.MainFrame;
import com.titan.mainframe.ServerTreeNode;
import com.titanserver.structure.TitanServerDefinition;

public class TitanCommonLib {
	public static String getJSONString(JSONObject obj, String key, String returnValue) {
		try {
			return obj.getString(key);
		} catch (Exception ex) {
			return returnValue;
		}
	}

	public static String getCurrentServerIP() {
		if (MainFrame.serverTree.getSelectionCount() == 0 || !(MainFrame.serverTree.getLastSelectedPathComponent() instanceof ServerTreeNode)) {
			return Global.primaryServerIP;
		} else {
			TitanServerDefinition server = ((ServerTreeNode) MainFrame.serverTree.getLastSelectedPathComponent()).server;
			return server.ip;
		}
	}

	public static String getXPath(String xml, String xpathStr) {
		String str = null;
		ByteArrayInputStream is;
		if (xml.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>")) {
			is = new ByteArrayInputStream(xml.getBytes());
		} else {
			is = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + xml).getBytes());
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(xpathStr);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0) {
				str = nodes.item(0).getNodeValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static NodeList getXPathNodeList(String xml, String xpathStr) {
		ByteArrayInputStream is;
		if (xml.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>")) {
			is = new ByteArrayInputStream(xml.getBytes());
		} else {
			is = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xml).getBytes());
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(xpathStr);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			return (NodeList) result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
