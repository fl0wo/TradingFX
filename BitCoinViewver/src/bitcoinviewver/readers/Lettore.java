package bitcoinviewver.readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

/**
 *
 * @author saban
 */
public interface Lettore {
    public void leggi(String filePath,XYChart<?, ?> grafico,Pane padre) throws FileNotFoundException, IOException;
}
