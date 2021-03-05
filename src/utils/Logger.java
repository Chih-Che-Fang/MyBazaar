package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
	public String output = "";
	
	public Logger(String output) {
		this.output = output;
	}
	
	public void log(String msg) {
		System.out.println(msg);
		
		FileWriter fstream;
		BufferedWriter out;
 		
		try {
			String outFile = String.format("output//%s.out", output);
			fstream =  new FileWriter(outFile, true);
			
			out = new BufferedWriter(fstream);
			out.write(msg);
			out.newLine();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
