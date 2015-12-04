package Alice;

import Support.Series;

import java.security.*;
import java.security.MessageDigest;

/**
 * Created by Damian on 2015-12-04.
 */
public class Alice {

    private Series i_series;
    private Series r_series;
    private Series l_series;
    private Series t_series;
    private Series s_series;
    private Series c_series;
    private Series b_series;
    private Series w_series;
    private Series u_series;

    public Alice() {
    	i_series = new Series(100);//serie identyfikujace
        r_series = new Series(100);
        l_series = Series.xorSeries(i_series, r_series);
        t_series = new Series(100);
        s_series = new Series(100);
        c_series = new Series(100);
        b_series = new Series(100);
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
            throw new RuntimeException(e);
        }
    }

    public Series GenerateWandUSeries (Series t_series, Series c_series, Series r_series){
        Series w_series;

        String sum_help = t_series.getValues().toString() + c_series.getValues().toString() + r_series.getValues().toString();
        byte[] sum_bytes = sum_help.getBytes();
        sum_bytes = getMD5(sum_bytes);

        w_series = new Series(t_series.getLength()+c_series.getLength()+r_series.getLength(), sum_bytes);

        return w_series;
    }
}
