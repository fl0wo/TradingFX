package bitcoinviewver.bitcointrader;

/**
 *
 * @author saban
 */
public class Cambiamenti {

    private float[] price = new float[7];
    private float[] perc = new float[7];
    // H D W M M3 M6 Y

    private final static int H = 0,
            D = 1,
            W = 2,
            M = 3,
            M3 = 4,
            M6 = 5,
            Y = 6;

    public Cambiamenti() {

    }

    public void setPerc_change_hour(float perc_change_hour) {
        perc[H] = perc_change_hour;
    }

    public void setPerc_change_day(float perc_change_day) {
        perc[D]= perc_change_day;
    }

    public void setPerc_change_week(float perc_change_week) {
        perc[W] = perc_change_week;
    }

    public void setPerc_change_month(float perc_change_month) {
        perc[M] = perc_change_month;
    }

    public void setPerc_change_3month(float perc_change_3month) {
        perc[M3] = perc_change_3month;
    }

    public void setPerc_change_6month(float perc_change_6month) {
        perc[M6] = perc_change_6month;
    }

    public void setPerc_change_1year(float perc_change_1year) {
        perc[Y] = perc_change_1year;
    }

    public void setPrice_change_hour(float price_change_hour) {
        price[H] = price_change_hour;
    }

    public void setPrice_change_day(float price_change_day) {
        price[D] = price_change_day;
    }

    public void setPrice_change_week(float price_change_week) {
        price[W] = price_change_week;
    }

    public void setPrice_change_month(float price_change_month) {
        price[M] = price_change_month;
    }

    public void setPrice_change_3month(float price_change_3month) {
        price[M3] = price_change_3month;
    }

    public void setPrice_change_6month(float price_change_6month) {
        price[M6] = price_change_6month;
    }

    public void setPrice_change_1year(float price_change_1year) {
        price[Y] = price_change_1year;
    }

    public float getPerc(int i) {
        return perc[i];
    }
    
    public float getPrice(int i) {
        return price[i];
    }

}
