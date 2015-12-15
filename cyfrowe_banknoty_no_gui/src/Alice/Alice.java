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

public class Alice extends User {

	public int no_identification_series; 
	public int length_of_series;
	
	public Series[] i_series;
	public Series[] r_series;
	public Series[] l_series;
	public Series[] s_series;
	public Series[] b_series;
	public Series[] t_series;
	public Series[] c_series;
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
		Loger.mess("ALICE: Czesc, jestem Alice i wlasnie powstalam :)).");
		
		no_identification_series = no_i_series;
		this.length_of_series = length_of_series;

		banknotes = new ArrayList<Banknote>();
		hidden_banknotes = new ArrayList<HiddenBanknote>();
		secrets = new ArrayList<BigInteger>();
		
		Loger.debug("--- Generating her identification series.");
		Loger.mess("ALICE: Generujemy ciagi identyfikacyjne.");
		i_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- Drawing RIGHT part of her id_series.");
		Loger.mess("ALICE: Generujemy PRAWA czesc ciagow identyfikacyjnych.");
		r_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- XOR'ing LEFT part of her id_series.");
		Loger.mess("ALICE: Generujemy lewa czesc ciagow identyfikacyjnych za pomoca XOR.");
		l_series = Series.xorSeries(i_series, r_series);
		
		Loger.debug("--- Drawing t_series.");
		Loger.mess("ALICE: Generujemy ciag t.");
		t_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- Drawing s_series.");
		Loger.mess("ALICE: Genereujemy ciag s.");
		s_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- Drawing c_series.");
		Loger.mess("ALICE: Generujemy ciag c.");
		c_series = Series.createSeriesTable(no_i_series, length_of_series);
		Loger.debug("--- Drawing b_series.");
		Loger.mess("ALICE: Generujemy ciag b.");
		b_series = Series.createSeriesTable(no_i_series, length_of_series);
		
		Loger.debug("--- Hashing t_series and c_series with r_series.");
		Loger.mess("ALICE: Hashujemy ciag t i c wraz z ciagiem r");
		w_series = Series.hashSeries(no_identification_series, t_series, c_series, r_series);
		Loger.debug("--- Hashing s_series and b_series with l_series.");
		Loger.mess("ALICE: Hashujemy ciag s i b wraz z ciagiem l.");
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
		Loger.mess("ALICE: Bank wybral sobie banknot: (j = " + picked_banknote + ").");
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
				BigInteger secret = RSA.drawRandomSecret(bank_key);

				secrets.add(secret);
				hidden_banknotes.add(banknote.hideBanknote(bank_key, secret));
				
				index++;
				Loger.mess("ALICE: Zaslonie sobie teraz " + index + ". banknot.");
			}
		} else {
			Loger.err("ALICE: Przepraszam ale nie mam zadnego banknotu :( (null)");
		}
	}

	public HiddenBanknote getHiddenBanknote(int index){
		
		return hidden_banknotes.get(index);
	}
	
	public void sendSecrets(PrintWriter out) throws UnsupportedEncodingException {
		out.println(secrets.size() - 1);
	
		Loger.debug("Sending secrets (" + (secrets.size() - 1) + ")");
		for (int i = 0; i < secrets.size(); i++) {
			if (i != picked_banknote) {
				out.println(secrets.get(i));
				Loger.debug(i + ".:" + secrets.get(i));
			}
		}
	}
	
	public void sendIdSeries(PrintWriter out) throws UnsupportedEncodingException {
		out.println(no_identification_series);
		
		for (Series series : this.s_series) {
			series.visualizeSeries();
			series.sendSeries(out);
		}
		
		for (Series series : this.b_series) {
			series.visualizeSeries();
			series.sendSeries(out);
		}
		
		for (Series series : this.l_series) {
			series.visualizeSeries();
			series.sendSeries(out);
		}
		
		for (Series series : this.t_series) {
			series.visualizeSeries();
			series.sendSeries(out);
		}
		
		for (Series series : this.c_series) {
			series.visualizeSeries();
			series.sendSeries(out);
		}
		
		for (Series series : this.r_series) {
			series.visualizeSeries();
			series.sendSeries(out);
		}
		
		Loger.debug("Series sent.");
	}
}
