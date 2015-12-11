package Client;


import java.security.PublicKey;
import java.util.ArrayList;

import Support.HiddenBanknote;
import Support.Loger;

public class User {

    public String getPublicKey() { return null; }
    public void setPublicKey(PublicKey pub_key) {}
    public void setHiddenBanknotes(ArrayList<HiddenBanknote> hidden_banknotes) {
    	Loger.err("You shouldn't be here!");
    }
}
