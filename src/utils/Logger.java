package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Logger class for printing output logs of testing case
 */
public class Logger {
	/* output information */
	public String output = "";
	
	/*
	 * Logger constructor and initialization
	 * @param output file name
	 */
	public Logger(String output) {
		this.output = output;
	}

	/**
	 * Write testing log to output file for validation purpose
	 * @param msg logging out message output
	 */
	public void log(String msg) {
		System.out.println(msg);
		
		FileWriter fstream;
		BufferedWriter out;
 		
		//Write peer message to output file
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
