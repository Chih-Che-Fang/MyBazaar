package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AddressLookUp parse the information from config file to init neighbors.
 */
public class AddressLookUp {

	/** ArrayList to record all the neighbors **/
	public List<String> address = new ArrayList<>();


	/**
	 * Address lookup constructor
	 * @param config config file read from 'config.txt' that include all peer's ip address information
	 */
	public AddressLookUp(String config) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = reader.readLine();
			while (line != null) {
				address.add(line.split(",")[1]);//record address for the peer
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get peer's ip/port address
	 * @param id get the neighbor information by id.
	 * @return return string of the neighbor/client information.
	 */
	public String get(String id) {
		return address.get(Integer.parseInt(id));
	}

}
