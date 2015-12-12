package Alice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;

import Client.User;
import Support.Banknote;
import Support.HiddenBanknote;
import Support.Loger;
import Support.RSA;
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
	public ArrayList<HiddenBanknote> hidden_banknotes;
	public ArrayList<BigInteger> secrets;
	public int picked_banknote;

    public PublicKey bank_key;

	// Przerobiłem wszystko w taki sposób, że liczba ciągów identyfikujących Alice
	// oraz długość wszystkich pojedynczych ciągów zależy od tego, co wprowadzi użytkownik
	
	public Alice(int no_i_series, int length_of_series) {
		Loger.debug("--- Creating new instance of Alice's class.");
		
		no_identification_series = no_i_series;
		this.length_of_series = length_of_series;

		banknotes = new ArrayList<Banknote>();
		hidden_banknotes = new ArrayList<HiddenBanknote>();
		secrets = new ArrayList<BigInteger>();
		
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
		w_series = Series.hashSeries(no_identification_series, t_series, c_series, r_series);
		Loger.debug("--- Hashing s_series and b_series with l_series.");
		u_series = Series.hashSeries(no_identification_series, s_series, b_series, l_series);
	}

    public User getUser() {
        return this;
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
	}
	
	public void setPickedBanknote(int picked_banknote) {
		
		this.picked_banknote = picked_banknote;
		Loger.debug("Ok, I know which banknote was choosen by Bank (j = " + picked_banknote + ").");
	}

	public boolean havePublicKey() {
		
		return bank_key == null ? false : true;
	}

	public boolean haveBanknotes() {
		
		return banknotes.isEmpty() ? false : true;
	}
	
	public boolean haveHiddenBanknotes() {
		
		return hidden_banknotes.isEmpty() ? false : true;
	}
	
	public void hideBanknotes(){
		
		int index = 0;
		
		if (banknotes != null) {
			for (Banknote banknote : banknotes) {
				BigInteger secret= RSA.drawRandomSecret(bank_key);

				secrets.add(secret);
				hidden_banknotes.add(banknote.hideBanknote(bank_key, secret));
				
				index++;
				Loger.println("Hiding " + index + ". banknote");
			}
		} else {
			Loger.err("Alice doesn't have any banknotes. (null)");
		}
	}

	public HiddenBanknote getHiddenBanknote(int index){
		
		return hidden_banknotes.get(index);
	}
	
	public void revealBanknotes(PrintWriter out) throws UnsupportedEncodingException {
		// wyślij ciągi S, B, L oraz T, C, W
		// wyślij sekrety potrzebne do odkrycia banknotów
		
		out.println(no_identification_series);
		
		for (Series series : s_series) {
			series.sendSeries(out);
		}
		
		for (Series series : b_series) {
			series.sendSeries(out);
		}
		
		for (Series series : l_series) {
			series.sendSeries(out);
		}
		
		for (Series series : t_series) {
			series.sendSeries(out);
		}
		
		for (Series series : c_series) {
			series.sendSeries(out);
		}
		
		for (Series series : w_series) {
			series.sendSeries(out);
		}
		
		out.println(secrets.size());
		
		for (BigInteger secret : secrets) {
			out.println(secret);
		}
	}
	
	
}
