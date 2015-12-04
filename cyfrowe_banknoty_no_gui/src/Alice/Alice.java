package Alice;

import Support.Series;

/**
 * Created by Damian on 2015-12-04.
 */
public class Alice {

    private Series i_series;
    private Series r_series;
    private Series l_series;
    private Series t_series;
    private Series s_series;

    public Alice() {
    	i_series = new Series(100);
        r_series = new Series(100);
        l_series = Series.xorSeries(i_series, r_series);
        t_series = new Series(100);
        s_series = new Series(100);
    }
    
    public Series GenerateWandUSeries (Series t_series, Series c_series, Series r_series){
        Series w_series = new Series(t_series.getLength());
        return w_series;
    }
}
