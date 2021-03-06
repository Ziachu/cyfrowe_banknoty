package support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import listeners.CommandListener;

public class Series {
	private int length;
	private byte[] values;
	
	public Series() {}
	
	public Series(int length, byte[]values) {
		this.length = length;
		this.values = values;
	}
	
	public Series(int length) {
		this.length = length;
		this.values = new byte[length];
		generateRandomSeries(this.length);
	}
	
	public Series(Series series) {
		this.length = series.length;
		this.values = series.values;
	}
	
	public void generateRandomSeries(int length) {
		// TODO: create series of given length with random values
		Random rand = new Random();
		for (int i = 0; i < length; i++) {
			this.values[i] = (byte) (rand.nextBoolean() ? 1 : 0);
		}
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public void setValues(byte[] values) {
		this.values = new byte[values.length];
		this.values = values;
	}
	
	public byte[] getValues() {
		return this.values;
	}
	
	public void sendSeries(PrintWriter out) throws UnsupportedEncodingException {
		// TODO: send receiver
		//out.println("Bob");
		// TODO: send length to server
		out.println(this.length);
		// TODO: send values to server
		out.println(new String(this.values, "utf-8"));
	}
	
	public void receiveSeries(BufferedReader in) {
		// TODO: receive length from server
		String received = "";
		try {
			received = in.readLine();
			this.length = Integer.parseInt(received);
			this.values = in.readLine().getBytes();
		} catch (NumberFormatException e) {
			Log.err("received = " + received);
			e.printStackTrace();
		} catch (IOException e) {
			Log.err("I/O problem; Series class during receiving series.");
		}
	}
	
	public void visualizeSeries(CommandListener cmd_listener) {
		for (int i = 0; i < this.length; i++) {
			if (i > 0 && i % 10 == 0)
				cmd_listener.CommandEmitted("", true);
			
			cmd_listener.CommandEmitted(" " + this.values[i] + " ", false);
		}
		
		cmd_listener.CommandEmitted("", true);
	}
}
