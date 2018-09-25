package bitcoinviewver.bitcointrader;

import bitcoinviewver.FXMLDocumentController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 *
 * @author saban
 */
public class BitCoinBot {

    private BitCoinValuer bcv;
    private ContoBancario bankAccount;

    private FXMLDocumentController controller;
    private int delay;

    public BitCoinBot(ContoBancario bankAccount, BitCoinValuer bcv, FXMLDocumentController controller) {
        this.bcv = bcv;
        this.bankAccount = bankAccount;

        this.controller = controller;
    }

    public void bot(boolean valoriUguali, int delayMs) throws JSONException, IOException {

        setDelay(delayMs);

        Cambiamenti c = new Cambiamenti();

        float valoreIniziale = bcv.get(BitCoinValuer.LAST, true);
        float bid = bcv.get(BitCoinValuer.BID, false);
        float ask = bcv.get(BitCoinValuer.ASK, false);
        float volume = bcv.get(BitCoinValuer.VOLUME, false);
        float volume_percent = bcv.get(BitCoinValuer.VOLUME_PERCENT, false);

        setCambiamenti(c);

        float valore;

        update(valoreIniziale, bid, ask, volume, volume_percent, c);

        dormi(delay);

        valore = bcv.get(BitCoinValuer.LAST, true);

        while (valoreIniziale == valore && !valoriUguali) {

            valore = bcv.get(BitCoinValuer.LAST, true);
            bid = bcv.get(BitCoinValuer.BID, false);
            ask = bcv.get(BitCoinValuer.ASK, false);
            volume = bcv.get(BitCoinValuer.VOLUME, false);
            volume_percent = bcv.get(BitCoinValuer.VOLUME_PERCENT, false);

            dormi(delay);
        }

        update(valore, bid, ask, volume, volume_percent, c);

        while (true) {
            float valore2;

            valore2 = bcv.get(BitCoinValuer.LAST, true);
            bid = bcv.get(BitCoinValuer.BID, false);
            ask = bcv.get(BitCoinValuer.ASK, false);
            volume = bcv.get(BitCoinValuer.VOLUME, false);
            volume_percent = bcv.get(BitCoinValuer.VOLUME_PERCENT, false);

            dormi(delay);

            while (valore == valore2 && !valoriUguali) {
                valore2 = bcv.get(BitCoinValuer.LAST, true);
                bid = bcv.get(BitCoinValuer.BID, false);
                ask = bcv.get(BitCoinValuer.ASK, false);
                volume = bcv.get(BitCoinValuer.VOLUME, false);
                volume_percent = bcv.get(BitCoinValuer.VOLUME_PERCENT, false);

                dormi(delay);
            }

            update(valore2, bid, ask, volume, volume_percent, c);

            if (valoreIniziale - valore < 0 && valore > valore2) {
                if (bankAccount.getBitCoin() != 0) {
                    bankAccount.aggiuntaUSD(BitCoinValuer.toCash(valore2, bankAccount.prelievoBit()));
                }
            } else if (valoreIniziale - valore > 0 && valore < valore2) {
                if (bankAccount.getCash() != 0) {
                    bankAccount.aggiuntaBit(BitCoinValuer.toBit(valore2, bankAccount.prelievoUSD()));
                }
            }

            valoreIniziale = valore;
            valore = valore2;

            setCambiamenti(c);
        }

    }

    private void update(float valore, float bid, float ask, float volume, float volume_perc, Cambiamenti c) {
        controller.update(valore, bid, ask, volume, volume_perc, c);
    }

    private void dormi(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(BitCoinBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDelay(int d) {
        this.delay = d;
    }

    private void setCambiamenti(Cambiamenti c) throws JSONException, IOException {
        c.setPerc_change_1year(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PERCENT, BitCoinValuer.YEAR));
        c.setPerc_change_3month(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PERCENT, BitCoinValuer.MONTH3));
        c.setPerc_change_6month(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PERCENT, BitCoinValuer.MONTH6));
        c.setPerc_change_day(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PERCENT, BitCoinValuer.DAY));
        c.setPerc_change_hour(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PERCENT, BitCoinValuer.HOUR));
        c.setPerc_change_month(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PERCENT, BitCoinValuer.MONTH));
        c.setPerc_change_week(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PERCENT, BitCoinValuer.WEEK));

        c.setPrice_change_1year(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PRICE, BitCoinValuer.YEAR));
        c.setPrice_change_3month(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PRICE, BitCoinValuer.MONTH3));
        c.setPrice_change_6month(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PRICE, BitCoinValuer.MONTH6));
        c.setPrice_change_day(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PRICE, BitCoinValuer.DAY));
        c.setPrice_change_hour(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PRICE, BitCoinValuer.HOUR));
        c.setPrice_change_month(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PRICE, BitCoinValuer.MONTH));
        c.setPrice_change_week(bcv.get(false, BitCoinValuer.CHANGES, BitCoinValuer.PRICE, BitCoinValuer.WEEK));
    }
}
