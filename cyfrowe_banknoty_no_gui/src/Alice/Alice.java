package Alice;

import Support.Series;

/**
 * Created by Damian on 2015-12-04.
 */
public class Alice {

    private Series i_series = new Series(100);
    private Series r_series = new Series(100);
    private Series l_series = Series.xorSeries(i_series, r_series);
    private Series t_series = new Series(100);
    private Series s_series = new Series(100);

    public Series GenerateWandUSeries (Series t_series, Series c_series, Series r_series){
        Series w_series = new Series(t_series.getLength());
        return w_series;
    }


    public int amount;


}
