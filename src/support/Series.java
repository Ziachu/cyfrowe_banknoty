package support;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class Series {
	public int length;
	public byte[] values;
	
	Series(int length, byte[]values) {
		this.length = length;
		this.values = values;
	}
	
	Series(int length) {
		this.length = length;
	}
	
	@SuppressWarnings("unused")
	private void generateRandomSeries(int length) {
		// TODO: create series of given length with random values
	}
	
	public void sendSeries(PrintWriter out) {
		// TODO: send length to server
		// TODO: send values to server
	}
	
	public static Series receiveSeries(BufferedReader in) {
		int length = 0;
		byte[] values = null;
		
		// TODO: receive length from server
		// TODO: receive values from server
		
		// TODO: create new Series

		return new Series(length, values);
	}
}
