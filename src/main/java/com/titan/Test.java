package com.c2.pandora;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.NodeList;

public class Test {

	public static void main(String[] args) {
		InputStream in = null;
		try {
			in = new URL("http://pandora-image.kingofcoders.com/pandora-image.xml").openStream();
			String xml = IOUtils.toString(in);
			NodeList list = PandoraCommonLib.getXPathNodeList(xml, "/images/image");
			for (int x = 0; x < list.getLength(); x++) {
				System.out.println(PandoraCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/author/text()"));

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}
