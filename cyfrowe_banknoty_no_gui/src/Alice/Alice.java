package Alice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Support.Loger;
import Support.Series;

/**
 * Created by Damian on 2015-12-04.
 */
public class Alice {

    private Series[] i_series;
    private Series[] r_series;
    private Series[] l_series;
    private Series[] t_series;
    private Series[] s_series;
    private Series[] c_series;
    private Series[] b_series;
    private Series[] w_series;
    private Series[] u_series;

    public Alice() {
    	// TODO: Tu tworzysz jeden ciąg identyfikujący, a Alice potrzebuje ich 100. ;)
        i_series = Series.seriesTable(100);
    	// TODO: Podobna sytuacja co wyżej.
        r_series = Series.seriesTable(100);
        l_series = Series.xorSeries(i_series, r_series);
        t_series = Series.seriesTable(100);
        s_series = Series.seriesTable(100);
        c_series = Series.seriesTable(100);
        b_series = Series.seriesTable(100);
        w_series = GenerateWandUSeries(t_series, c_series, r_series);
        u_series = GenerateWandUSeries(s_series, b_series, l_series);
    }

    public byte[] getMD5(byte[] input) {
        try {
            String md5 = "MD5";
            MessageDigest md;
            md = MessageDigest.getInstance(md5);
            byte[] thedigest = md.digest(input);
            return thedigest;
        } catch (NoSuchAlgorithmException e) {
        	Loger.println("\t[err] Trouble with md5 hashing.");
        	throw new RuntimeException(e);
        }
    }

    // TODO: Damiano, nazwa metody sugeuje zwracania dwóch ciągów U i W, czemu zwracasz tylko W?
    // TODO: Czy ta metoda tak w ogóle musi zwracać ten ciąg? Jeżeli klasa Alice ma pole "w_series"
    // 		 to, czy nie lepiej tam zapisać wynik?
    public Series[] GenerateWandUSeries (Series[] t_series, Series[] c_series, Series[] r_series){
        Series[] w_series = new Series[100];

        for (int i = 0; i<100; i++) {
            String sum_help = t_series[i].getValues().toString() + c_series[i].getValues().toString() + r_series[i].getValues().toString();
            int sum_length = t_series[i].getLength() + c_series[i].getLength() + r_series[i].getLength();
            w_series[i].setLength(sum_length);
            byte[] sum_bytes = getMD5(sum_help.getBytes());
            w_series[i] = new Series(sum_length, sum_bytes);
        }

        return w_series;
    }

}
