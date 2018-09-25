package bitcoinviewver.bitcointrader;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author saban
 */
public class BitCoinValuer {

    public static String LAST = "last";
    public static String BID = "bid";
    public static String ASK = "ask";
    
    public static String CHANGES = "changes";
    public static String PERCENT = "percent";
    public static String PRICE = "price";
    
    public static String HOUR = "hour";
    public static String DAY = "day";
    public static String WEEK = "week";    
    public static String MONTH = "month";
    public static String MONTH3 = "month_3";
    public static String MONTH6 = "month_6";
    public static String YEAR = "year";
    public static String VOLUME_PERCENT = "volume_percent";
    public static String VOLUME = "volume";
    
    private final String API = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTCUSD";

    private JSONObject oggetto;
    
    public BitCoinValuer(){
       // init();
    }

    public float get(boolean update,String... tipo) throws JSONException, IOException{
        if(update)httpReq();
        
        JSONObject tmp = oggetto;
        
        for (int i = 0; i < tipo.length-1; i++) {
            tmp = (JSONObject) tmp.get(tipo[i]);
        }
        
        return Float.parseFloat(tmp.get(tipo[tipo.length-1])+"");//bug
        //gif
    }

    /**
     * deprecato
     * @param tipo
     * @param update
     * @return
     * @throws JSONException
     * @throws IOException 
     */
    public float get(String tipo,boolean update) throws JSONException, IOException{
        if(update)httpReq();
        return Float.parseFloat(oggetto.get(tipo)+"");
    }
    
    public static float toCash(float bicoin_value,float n_bitcoin){
        return bicoin_value * n_bitcoin;
    }
    
    public static float toBit(float bicoin_value,float money){
        return money/bicoin_value;
    }

    private void httpReq() throws IOException, JSONException {
        oggetto = JSonReader.readJSonDaUrl(API);
    }
}
