package com.uniba.mining.tasks.exportdiag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWriter {
	
	public static void writeToFile(StringBuffer content, File outputFile) {
		try {
			// Write the StringBuffer to file
			FileOutputStream fout = new FileOutputStream(outputFile);
			fout.write(content.toString().getBytes());
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public static void writeToFile(StringBuilder content, File outputFile) {
		try {
			// Write the StringBuilder to file
			FileOutputStream fout = new FileOutputStream(outputFile, true);
			fout.write(content.toString().getBytes());
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
