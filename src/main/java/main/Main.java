package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
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
		// RETRIEVE ZIP ARCHIVE AS RESOURCES
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(new Main().getClass()
					.getResource("/Exemple.zip").getFile());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// OPEN THE ZIP ARCHIVE TO RETRIEVE ENTRIES
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			// SEARCH THE FILE CONTAINING THE PARAMETERS
			if (entry.getName().contains("workflow.knime")) { 
				
				// OPEN THE FILE AS XML FILE
				SAXBuilder sxb = new SAXBuilder();
				try {
					document = sxb.build(zipFile.getInputStream(entry));
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// PREPARE THE VARIABLE TO STORE THE PARAMETERS
				LinkedHashMap<String, ArrayList<String>> parameters = new LinkedHashMap<String, ArrayList<String>>();

				// PARSE THE XML FILE
				racine = document.getRootElement();
				List<Element> configs = racine.getChildren();
				for (Element e : configs) {
					// RETRIEVE THE WORKFLOWS VARIABLES
					if (e.getAttributeValue("key").equalsIgnoreCase(
							"workflow_variables")) {
						List<Element> variables = e.getChildren();
						for (Element f : variables) {
							// RETRIEVE THE PARAMETERS COMPONENTS
							List<Element> values = f.getChildren();
							String name = new String();
							String type = new String();
							String value = new String();
							for (Element g : values) {
								if (g.getAttributeValue("key").equalsIgnoreCase("name")) {
									name = g.getAttributeValue("value");
								}
								if (g.getAttributeValue("key").equalsIgnoreCase("class")) {
									type = g.getAttributeValue("value");
								}
								if (g.getAttributeValue("key").equalsIgnoreCase("value")) {
									value = g.getAttributeValue("value");
								}
								
								ArrayList<String> temp = new ArrayList<String>();
								temp.add(type);
								temp.add(value);
								parameters.put(name, temp);
							}
						}
						break;
					}
				}
				System.out.println(parameters);
			}
			break;
		}

	}

}
