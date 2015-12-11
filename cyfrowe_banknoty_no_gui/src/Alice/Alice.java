package Alice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;

import Client.User;
import Support.Banknote;
import Support.Loger;
import Support.Series;

/**
 * Created by Damian on 2015-12-04.
 */
public class Alice extends User {

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
	
	public ArrayList<Banknote> banknotes;
	public ArrayList<Banknote> hidden_banknotes;

    public Key bank_key;

	// Przerobiłem wszystko w taki sposób, że liczba ciągów identyfikujących Alice
	// oraz długość wszystkich pojedynczych ciągów zależy od tego, co wprowadzi użytkownik
	
	public Alice(int no_i_series, int length_of_series) {
		Loger.debug("--- Creating new instance of Alice's class.");
		
		no_identification_series = no_i_series;
		this.length_of_series = length_of_series;

		banknotes = new ArrayList<Banknote>();
		
		Loger.debug("--- Generating her identification series.");
		i_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- Drawing RIGHT part of her id_series.");
		r_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- XOR'ing LEFT part of her id_series.");
		l_series = Series.xorSeries(i_series, r_series);
		
		Loger.debug("--- Drawing t_series.");
		t_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- Drawing s_series.");
		s_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- Drawing c_series.");
		c_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- Drawing b_series.");
		b_series = Series.createSeriesTable(no_i_series, length_of_series);
		
		Loger.debug("--- Hashing t_series and c_series with r_series.");
		w_series = hashSeries(t_series, c_series, r_series);
		Loger.debug("--- Hashing s_series and b_series with l_series.");
		u_series = hashSeries(s_series, b_series, l_series);
		
	}

    public User getUser() {
        return this;
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

	public Series[] hashSeries(Series[] series1, Series[] series2, Series[] series3) {
		Series[] table_of_hashes = new Series[no_identification_series];

		for (int i = 0; i < no_identification_series; i++) {
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
	
	public void generateBanknote(double cash_amount) {
		Banknote banknote = new Banknote(cash_amount, 1, s_series, u_series, t_series, w_series);
		banknote.generateBanknoteId();
		banknotes.add(banknote);
	}


	// testowo wypycha tylko jeden ciąg
	public void exportIdToFile() {
		File file = new File("id_series.txt");
		Loger.debug("File opened.");
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			Loger.debug("FileOutputStream created.");
			i_series[0].writeToFile(fos);
			Loger.debug("id_series saved to file.");
			fos.close();
		} catch (FileNotFoundException e) {
			Loger.err("Couldn't open file \"id_series.txt\"");
		} catch (IOException e) {
			Loger.err("Couldn't close FileOutputStream for \"id_series.txt\"");
		}
	}
	
	// testowo zciąga tylko jeden ciąg
	public void importIdFromFile() {
		File file = new File("id_series.txt");
		Loger.debug("File opened.");
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			Loger.debug("FileInputStream created.");
			i_series[0].readFromFile(fis);
			Loger.debug("id_series read from file.");
			fis.close();
		} catch (FileNotFoundException e) {
			Loger.err("Couldn't open file \"id_series.txt\"");
		} catch (IOException e) {
			Loger.err("Couldn't close FileInputStream for \"id_series.txt\"");
		}
	}
	public void setPublicKey(PublicKey public_key){
		this.bank_key = public_key;
		Loger.debug("Public key restored successfully");
	}

	public void hideBanknotes(){

	}


}
