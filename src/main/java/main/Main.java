package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 
 * @author pidupuis
 *
 */
public class Main {

	public static void main(String[] args) {
		ZipFile zipFile;
		
		InputStream in = new Main().getClass().getResourceAsStream("/Exemple.zip");
		ZipInputStream stream = new ZipInputStream(in);
		
		ZipEntry entry;
        try {
			while((entry = stream.getNextEntry())!=null) {
				System.out.println(entry);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
