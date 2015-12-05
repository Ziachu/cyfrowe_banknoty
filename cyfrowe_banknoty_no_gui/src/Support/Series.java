package Support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

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
//		TODO: send receiver
//		out.println(receiver);
//		TODO: send length to server
		out.println(this.length);
//		TODO: send values to server
		out.println(new String(this.values, "utf-8"));
	}
	
	public void receiveSeries(BufferedReader in) {
		String received = "";
		try {
			received = in.readLine();
			this.length = Integer.parseInt(received);
			this.values = in.readLine().getBytes();
		} catch (NumberFormatException e) {
			Loger.println("[err] received = " + received);
			e.printStackTrace();
		} catch (IOException e) {
			Loger.println("[err] I/O problem; Series class during receiving series.\n\t" + e.getMessage());
		}
	}
	
	public void visualizeSeries() {
		for (int i = 0; i < this.length; i++) {
			if (i > 0 && i % 10 == 0)
				Loger.println("");
			
			Loger.print(" " + this.values[i] + " ");
		}
		
		Loger.println("");
	}

    public static Series[] xorSeries (Series[] I, Series[] R){
        Series[] L = new Series[100];

		for (int j=0; j< 100; j++) {
			for (int i = 0; i < I.length; i++) {
				L[j].values[i] = I[j].values[i] ^= R[j].values[i];
			}
		}
        
        return L;
    }

	public static Series[] seriesTable (int length){
		Series[] L = new Series[100];

		for (int i=0; i<100; i++){
			L[i] = new Series(100);
		}
		return L;
	}

}
