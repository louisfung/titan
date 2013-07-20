package com.titan;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.NodeList;

public class Test {

	public static void main(String[] args) {
		InputStream in = null;
		try {
			in = new URL("http://titan-image.kingofcoders.com/titan-image.xml").openStream();
			String xml = IOUtils.toString(in);
			NodeList list = TitanCommonLib.getXPathNodeList(xml, "/images/image");
			for (int x = 0; x < list.getLength(); x++) {
				System.out.println(TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/author/text()"));

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}
