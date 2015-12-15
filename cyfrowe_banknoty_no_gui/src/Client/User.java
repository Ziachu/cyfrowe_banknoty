package Client;


import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;

import Support.HiddenBanknote;
import Support.Series;

public class User {

    public String getPublicKey() { return null; }
    public void setPublicKey(PublicKey pub_key) {}
    
    public void setHiddenBanknotes(ArrayList<HiddenBanknote> hidden_banknotes) {}
    
    public void setPickedBanknote(int picked_banknote) {}
    public int getPickedBanknote() { return 0; }
    
    public void setSecrets(BigInteger[] secrets) {}
    public void setAliceSeries(Series[] s_series, Series[] b_series, Series[] l_series, 
			   				   Series[] t_series, Series[] c_series, Series[] w_series) {}
}
