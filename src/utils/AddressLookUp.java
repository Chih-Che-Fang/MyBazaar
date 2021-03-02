package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddressLookUp {
	
	public List<String> address = new ArrayList<>();
	
	public AddressLookUp(String config) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = reader.readLine();
			while (line != null) {
				address.add(line.split(",")[1]);
				//System.out.println(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String get(String id) {
		return address.get(Integer.parseInt(id));
	}

}
