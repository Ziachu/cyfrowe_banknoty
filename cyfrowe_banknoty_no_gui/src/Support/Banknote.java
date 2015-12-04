package Support;

import java.util.Random;

/**
 TODO: musi miec miejsca na IDBanknotu, Kwotę, 4 listy T-W, S-U po 100 obiektow typu series
 Generate_banknote(Y-to podaje ALICE, Xi-losowe(z series), I-liste 100 obiektow typu series(identyfikujacych) - otrzymuje po 1 elemencie
 z listy i sobie zczytuje do swojej listy te 100 banknotow w forze
 ALICE PODAJE Y i CIAGI IDENTYFIKUJACE(sama sobie je generuje z series zeby miec swoj komplet)
 */
public class Banknote {
    private static int amount;
    private static int banknote_id;
    private static Series s_series;
    private static Series u_series;
    private static Series t_series;
    private static Series w_series;

    public Banknote () {}

    public Banknote (int amount, int banknote_id, Series s_series, Series u_series, Series t_series, Series w_series) {
        this.amount = amount;
        this.banknote_id = banknote_id;
        this.s_series = s_series;
        this.u_series = u_series;
        this.t_series = t_series;
        this.w_series = w_series;

    }

//    public Banknote generateBanknote(){
        //todox tu beda getTseries itd zeby moc ustalic
        //Banknote banknote = new Banknote(int amount, int banknote_id, Series s_series, Series u_series, Series t_series, Series w_series)
//        getBanknote_id();
//        setBanknote_id();


//        return banknote;
//    }

   /* public byte[] getTSeries(BanknoteGenerator.GenerateTSeries()) {
        return Banknote.t_series[];
    } */

    public int getBanknote_id(){
        return generateBanknoteId();
    }

    public void setBanknote_id(){
        int id = getBanknote_id();
        this.banknote_id = id;
    }

    public int get_amount(int am){
        this.amount = am;
        return am;
    }
    public void setAmount(int cash){
        this.amount = cash;
    }


    public static int generateBanknoteId(){

        Random rand = new Random();
        int banknote_id = rand.nextInt(Integer.MAX_VALUE);
        return banknote_id;
    }







}
