package bitcoinviewver.bitcointrader;
/**
 *
 * @author saban
 */
public class ContoBancario {
    
    private float usd = 0;
    private float bit = 0;

    public ContoBancario(float usd) {
        this.usd = usd;
    }

    public float prelievoUSD(float x) {
        if(usd-x<0)return usd;
        usd-=x;
        return x;
    }

    public void aggiuntaUSD(float x) {
        usd+=x;
    }
    
    public float prelievoBit(float x) {
        if(bit-x<0)return bit;
        bit-=x;
        return x;
    }

    public void aggiuntaBit(double x) {
        bit+=x;
    }

    public float getBitCoin() {
         return bit;
    }

    public float getCash() {
        return usd;
    }

    public float prelievoBit() {
        return prelievoBit(getBitCoin());
    }

    float prelievoUSD() {
        return prelievoUSD(getCash());
    }
}
