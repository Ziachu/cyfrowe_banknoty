package Support;


import java.util.Random;

/**
 TODO: musi miec miejsca na IDBanknotu, Kwotę, 4 listy T-W, S-U po 100 obiektow typu series
 Generate_banknote(Y-to podaje ALICE, Xi-losowe(z series), I-liste 100 obiektow typu series(identyfikujacych) - otrzymuje po 1 elemencie
 z listy i sobie zczytuje do swojej listy te 100 banknotow w forze
 ALICE PODAJE Y i CIAGI IDENTYFIKUJACE(sama sobie je generuje z series zeby miec swoj komplet)
 */
public class Banknote {
    private int amount;
    private int banknote_id;
    private Series s_series;
    private Series u_series;
    private Series t_series;
    private Series w_series;

    public Banknote (int amount, int banknote_id, Series s_series, Series u_series, Series t_series, Series w_series) {
        this.amount = amount;
        this.banknote_id = banknote_id;
        this.s_series = s_series;
        this.u_series = u_series;
        this.t_series = t_series;
        this.w_series = w_series;
    }

//    public Banknote generateBanknote(){
//        todox tu beda getTseries itd zeby moc ustalic
//        Banknote banknote = new Banknote(int amount, int banknote_id, Series s_series, Series u_series, Series t_series, Series w_series)
//        getBanknote_id();
//        setBanknote_id();
//
//        return banknote;
//    }

//    public byte[] getTSeries(BanknoteGenerator.GenerateTSeries()) {
//        return Banknote.t_series[];
//    }

    public int getBanknoteId() { return banknote_id; }

    public void setBanknoteId(int id) { banknote_id = id; }

    public Series getSseries() { return s_series; }

    public void setSseries(Series series) { u_series = series; }

    public Series getUseries() {return u_series;}

    public void setUseries(Series series) { t_series = series; }

    public Series getTseries() {return t_series;}

    public void setTseries(Series series) { s_series = series; }

    public Series getWseries() {return w_series;}

    public void setWseries(Series series) { w_series = series; }

    public int getAmount() { return amount; }
    
    public void setAmount(int cash) { amount = cash; }

    public void generateBanknoteId(){
        Random rand = new Random();
        int id = rand.nextInt(Integer.MAX_VALUE);
       
        this.banknote_id = id; 
    }





}
