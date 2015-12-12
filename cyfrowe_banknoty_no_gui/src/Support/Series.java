package Support;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
		out.println(this.length);
		out.println(new String(this.values, "utf-8"));
	}
	
	public void receiveSeries(BufferedReader in) {
		
		Loger.println("Receiving some series.");
		
		try {
			String received = in.readLine();
			this.length = Integer.parseInt(received);
			this.values = in.readLine().getBytes();
		} catch (NumberFormatException e) {
			
			Loger.err("Couldn't parse received series's length to Integer.");
		} catch (IOException e) {
			
			Loger.err("I/O problem; Series class during receiving series.\n\t" + e.getMessage());
		}
	}
	
	public void visualizeSeries() {
		for (int i = 0; i < this.length; i++) {
			Loger.print(this.values[i] + "");
		}
		
		Loger.println("");
	}

	public static Series[] hashSeries(int no_id_series, Series[] series1, Series[] series2, Series[] series3) {
		Series[] table_of_hashes = new Series[no_id_series];

		for (int i = 0; i < no_id_series; i++) {
			String sum_help = series1[i].getValues().toString() 
							+ series2[i].getValues().toString()
							+ series3[i].getValues().toString();

			/*Loger.print("\t--- H(" + series1[i].getValues().toString() + " ," 
						+ series2[i].getValues().toString() + " ," 
						+ series3[i].getValues().toString() + ")");*/
			
			byte[] sum_bytes = getMD5(sum_help.getBytes());		
			table_of_hashes[i] = new Series(sum_bytes.length, sum_bytes);
		}

		return table_of_hashes;
	}
	
	public static byte[] getMD5(byte[] input) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			
			byte[] thedigest = md.digest(input);
			/*Loger.println(" = " + thedigest.toString());*/

			return thedigest;
		} catch (NoSuchAlgorithmException e) {
			Loger.err("Trouble with md5 hashing.");
			throw new RuntimeException(e);
		}
	}
	
    public static Series[] xorSeries (Series[] I, Series[] R){
    	if (I.length == R.length) {
        	int length = I.length;
        	Series[] xor_result = new Series[length];
        	        	
        	// Iteruję po każdym ciągu
			for (int j = 0; j < length; j++) {
				xor_result[j] = new Series(I[j].length);
				// Iteruję po każdym elemencie w ciągu
				for (int i = 0; i < I[j].length; i++) {
					xor_result[j].values[i] = (byte)(I[j].values[i] ^ R[j].values[i]);
				}				
			}
        	
        	return xor_result;
        	
        } else {
        	Loger.err("Couldn't XOR two series (two different lengths.");
        	throw new IllegalArgumentException();
        }
    }

	public static Series[] createSeriesTable (int no_series, int length_of_series){
		Series[] series_table = new Series[no_series];

		for (int i = 0; i < no_series; i++){
			series_table[i] = new Series(length_of_series);
		}
		
		return series_table;
	}

	public void writeToFile(FileOutputStream fos) {
		try {
			fos.write(getValues());
			fos.write("\n".getBytes());
		} catch (IOException e) {
			Loger.err("Couldn't write series to a file.\n\t" + e.getMessage());
		}
	}
	
	public void readFromFile(FileInputStream fis) {
		try {
			fis.read(this.values);
		} catch (IOException e) {
			Loger.err("Couldn't read series from file.\n\t" + e.getMessage());
		}
	}
}
