package main;

import java.io.File;
import java.io.FileWriter;
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
 * Program which open a KNIME workflow archive, retrieve the parameters and write them properly in a CSV file
 */
public class Main {

	static String workflowPathAndName;
	static String outfilePathAndName;
	static String infoPathAndName;
	static LinkedHashMap<String, ArrayList<String>> parameters;
	static String author = new String("");
	static String date = new String("");
	static String comments = new String("");
	static Document document;
	static Element racine;

	public static void main(String[] args) {
		// RETRIEVE FILE NAME AND PATH
		if (args.length != 3) {
			System.out
					.println("This program needs only three arguments, one for the workflow file path, the second for the param outfile path, and the third one for the information outfile path");
			return;
		}
		workflowPathAndName = args[0];
		if (!new File(workflowPathAndName).exists()) { // If the workflow does
														// not exists
			System.out
					.println("The workflow file (first argument) does not exists !");
			return;
		}
		outfilePathAndName = args[1];
		try {
			new FileWriter(outfilePathAndName);
		} catch (Exception e) { // If the output file can not be created
			System.out
					.println("The output file (second argument) to store the parameters can not be created !");
			return;
		}
		infoPathAndName = args[2];
		try {
			new FileWriter(infoPathAndName);
		} catch (Exception e) { // If the output file can not be created
			System.out
					.println("The output file (third argument) to store the information can not be created !");
			return;
		}

		// PREPARE THE VARIABLE TO STORE THE PARAMETERS
		parameters = new LinkedHashMap<String, ArrayList<String>>();

		// RETRIEVE ZIP ARCHIVE AS RESOURCES
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(workflowPathAndName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// OPEN THE ZIP ARCHIVE TO RETRIEVE ENTRIES
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			
			// SEARCH THE FILE CONTAINING THE PARAMETERS
			if (entry.getName().equals(new File(workflowPathAndName).getName().substring(0, new File(workflowPathAndName).getName().lastIndexOf("."))+"/workflow.knime")) {

				// OPEN THE FILE AS XML FILE
				SAXBuilder sxb = new SAXBuilder();
				try {
					document = sxb.build(zipFile.getInputStream(entry));
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

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
								if (g.getAttributeValue("key")
										.equalsIgnoreCase("name")) { // Retrieve
																		// the
																		// name
									name = g.getAttributeValue("value");
								}
								if (g.getAttributeValue("key")
										.equalsIgnoreCase("class")) { // Retrieve
																		// the
																		// type
									type = g.getAttributeValue("value");
								}
								if (g.getAttributeValue("key")
										.equalsIgnoreCase("value")) { // Retrieve
																		// the
																		// default
																		// value
									value = g.getAttributeValue("value");
								}

								// STORE THE PARAMETERS
								ArrayList<String> temp = new ArrayList<String>();
								temp.add(type);
								temp.add(value);
								parameters.put(name, temp);
							}
						}
						break;
					}
				}
			break;
			}
		}

		// WRITE THE PARAMETERS INTO ANOTHER FILE
		try {
			FileWriter writer = new FileWriter(outfilePathAndName);
			for (String s : parameters.keySet()) {
				writer.append(s); // Name
				writer.append(":");
				writer.append(parameters.get(s).get(0)); // Type
				writer.append(":");
				writer.append(parameters.get(s).get(1)); // Default value
				writer.append("\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		// OPEN THE ZIP ARCHIVE A SECOND TIME TO RETRIEVE ENTRIES
		entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			
			// SEARCH THE FILE CONTAINING THE INFORMATION
			if (entry.getName().equals(new File(workflowPathAndName).getName().substring(0, new File(workflowPathAndName).getName().lastIndexOf("."))+"/workflowset.meta")) {

				// OPEN THE FILE AS XML FILE
				SAXBuilder sxb = new SAXBuilder();
				try {
					document = sxb.build(zipFile.getInputStream(entry));
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// PARSE THE XML FILE
				racine = document.getRootElement();
				List<Element> configs = racine.getChildren();
				for (Element e : configs) {
					// RETRIEVE THE ELEMENT
					if (e.getAttributeValue("name").equalsIgnoreCase("Author")) {
						author = e.getText();
					}
					else if (e.getAttributeValue("name").equalsIgnoreCase("Creation Date")) {
						date = e.getText();
					}
					else if (e.getAttributeValue("name").equalsIgnoreCase("Comments")) {
						comments = e.getText();
					}
				}
			break;
			}
		}

		// WRITE THE INFORMATION INTO ANOTHER FILE
		try {
			FileWriter writer = new FileWriter(infoPathAndName);
			if (author.equals("") || date.equals("") || comments.equals("")) {
				writer.append("There is no description available.");
			}
			else {
				writer.append(author); // Author
				writer.append("\n");
				writer.append(date);
				writer.append("\n");
				writer.append(comments);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		

	}

}
