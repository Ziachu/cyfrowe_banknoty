package Alice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Support.Loger;
import Support.Series;

/**
 * Created by Damian on 2015-12-04.
 */
public class Alice {

	public int no_identification_series; 
	public int length_of_series;
	
	public Series[] i_series;
	public Series[] r_series;
	public Series[] l_series;
	public Series[] t_series;
	public Series[] s_series;
	public Series[] c_series;
	public Series[] b_series;
	public Series[] w_series;
	public Series[] u_series;

	// Przerobiłem wszystko w taki sposób, że liczba ciągów identyfikujących Alice
	// oraz długość wszystkich pojedynczych ciągów zależy od tego, co wprowadzi użytkownik
	
	public Alice(int no_i_series, int length_of_series) {
		Loger.println("--- Creating new instance of Alice's class.");
		
		no_identification_series = no_i_series;
		this.length_of_series = length_of_series;

		Loger.println("--- Generating her identification series.");
		i_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.println("--- Drawing RIGHT part of her id_series.");
		r_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.println("--- XOR'ing LEFT part of her id_series.");
		l_series = Series.xorSeries(i_series, r_series);
		
		Loger.println("--- Drawing t_series.");
		t_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.println("--- Drawing s_series.");
		s_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.println("--- Drawing c_series.");
		c_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.println("--- Drawing b_series.");
		b_series = Series.createSeriesTable(no_i_series, length_of_series);
		
		Loger.println("--- Hashing t_series and c_series with r_series.");
		w_series = hashSeries(t_series, c_series, r_series);
		Loger.println("--- Hashing s_series and b_series with l_series.");
		u_series = hashSeries(s_series, b_series, l_series);
		
	}

	public static byte[] getMD5(byte[] input) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			
			byte[] thedigest = md.digest(input);
			Loger.println(" = " + thedigest.toString());

			return thedigest;
		} catch (NoSuchAlgorithmException e) {
			Loger.println("\t[err] Trouble with md5 hashing.");
			throw new RuntimeException(e);
		}
	}

	public Series[] hashSeries(Series[] series1, Series[] series2, Series[] series3) {
		Series[] table_of_hashes = new Series[no_identification_series];

		for (int i = 0; i < no_identification_series; i++) {
			String sum_help = series1[i].getValues().toString() 
							+ series2[i].getValues().toString()
							+ series3[i].getValues().toString();

			Loger.print("\t--- H(" + series1[i].getValues().toString() + " ," 
						+ series2[i].getValues().toString() + " ," 
						+ series3[i].getValues().toString() + ")");
			
			byte[] sum_bytes = getMD5(sum_help.getBytes());		
			table_of_hashes[i] = new Series(sum_bytes.length, sum_bytes);
		}

		return table_of_hashes;
	}

}
