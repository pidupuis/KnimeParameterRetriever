package main;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * 
 * @author pidupuis
 * 
 */
public class Main {

	static Document document;
	static Element racine;

	public static void main(String[] args) {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(new Main().getClass()
					.getResource("/Exemple.zip").getFile());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();

			if (entry.getName().contains("workflow.knime")) {
				// System.out.println(entry);
				SAXBuilder sxb = new SAXBuilder();
				try {
					document = sxb.build(zipFile.getInputStream(entry));
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				racine = document.getRootElement();

				List<Element> configs = racine.getChildren();
				for (Element e : configs) {
					if (e.getAttributeValue("key").equalsIgnoreCase(
							"workflow_variables")) {
						System.out.println(e.getAttributeValue("key"));
						List<Element> variables = e.getChildren();
						for (Element f : variables) {
							System.out.println("###");
							System.out.println(f.getAttributeValue("key"));
							List<Element> values = f.getChildren();
							for (Element g : values) {
								System.out.println(g.getAttributeValue("key"));
								System.out.println(g.getAttributeValue("type"));
								System.out.println(g.getAttributeValue("value")
										+ "\n");
							}
						}
					}
				}

			}
		}

	}

}
