package bitcoinviewver;

import bitcoinviewver.bitcointrader.BitCoinBot;
import bitcoinviewver.bitcointrader.BitCoinValuer;
import bitcoinviewver.bitcointrader.Cambiamenti;
import bitcoinviewver.bitcointrader.ContoBancario;
import bitcoinviewver.readers.Lettore;
import bitcoinviewver.readers.LettoreHistoryData;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.transitions.JFXFillTransition;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.FloatBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONException;

/**
 *
 * @author saban
 */
public class FXMLDocumentController implements Initializable {

    private JFXListView<Label> list;

    private BitCoinValuer bcv;
    private ContoBancario cb;
    private BitCoinBot bcb;

    private final Calendar cal = Calendar.getInstance();

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        draggable();
        historyValue();
        updateTime();

        readHistoryData(chartValueBitcoin, new LettoreHistoryData(), "src\\bitcoinviewver\\data\\history_data.csv", false);

        //new GraficoZoomabile(zoom_real_time_line,lineChart,valori_line);
        lineChart.getData().addAll(valori_line, valori_line_bid, valori_line_ask);

        valori_line.setName("Value");
        valori_line_bid.setName("Bid");
        valori_line_ask.setName("Ask");

        scatterChart.getData().add(valori_scatter);

        bcv = new BitCoinValuer();
        cb = new ContoBancario(100f);
        bcb = new BitCoinBot(cb, bcv, this);

        new Thread(() -> {
            try {
                bcb.bot(true, 3000);
            } catch (JSONException ex ) {
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

    }

    private void draggable() {
        drag_panel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = drag_panel.getScene().getWindow().getX() - event.getScreenX();
                yOffset = drag_panel.getScene().getWindow().getY() - event.getScreenY();
            }
        });
        drag_panel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drag_panel.getScene().getWindow().setX(event.getScreenX() + xOffset);
                drag_panel.getScene().getWindow().setY(event.getScreenY() + yOffset);
            }
        });
    }

    @FXML
    public void exit() {
        System.exit(0);
    }

    @FXML
    public void iconized() {
        ((Stage) drag_panel.getScene().getWindow()).setIconified(true);
    }

    private void historyValue() {
        list = new JFXListView<>();

        list.getStylesheets().add(getClass().getResource("css/stile.css").toExternalForm());
        list.setOrientation(Orientation.HORIZONTAL);

        final StackPane container = new StackPane(list);

        history_scroll.setContent(container);
        history_scroll.getTopBar().getChildren().add(container);

    }

    public void update(float valore, float bid, float ask, float volumePerc, float volume, Cambiamenti changes) {
        Platform.runLater(() -> {

            updateValue(valore);

            updateGraficoLinea(valore, bid, ask);
            updateGraficoScatter(valore);
            updateGraficoTorta(volume, volumePerc);

            updateTime();

            updateChanges(changes);

        });
    }

    ///////////////// INIZIALIZZIAMO FXML VARS //////////////////
    @FXML
    public Pane drag_panel;

    @FXML
    public JFXScrollPane history_scroll;

    @FXML
    public Label value_label;

    @FXML
    public Label data_label;

    @FXML
    public Label delay_label;

    @FXML
    public JFXSlider slider_opacity;
    @FXML
    public JFXSlider delay_opacity;

    @FXML
    public AreaChart<?, ?> chartValueBitcoin;

    @FXML
    public VBox changes_perc;
    @FXML
    public VBox changes_price;

    @FXML
    public ScatterChart<?, ?> scatterChart;
    private XYChart.Series valori_scatter = new XYChart.Series();
    private float min_valore_scatter = Float.MAX_VALUE;
    private float max_valore_scatter = -1;

    @FXML
    public AnchorPane zoom_group_value;

    @FXML
    public AnchorPane zoom_real_time_line;

    @FXML
    public Pane delayPane;
    @FXML
    public AnchorPane homePane;
    @FXML
    public AnchorPane notePane;
    @FXML
    public AnchorPane profilePane;
    
    @FXML
    public PieChart pieChart;

    @FXML
    public Label volume;
    @FXML
    public Label volumePerc;

    @FXML
    public LineChart<?, ?> lineChart;
    private XYChart.Series valori_line = new XYChart.Series();
    private XYChart.Series valori_line_bid = new XYChart.Series();
    private XYChart.Series valori_line_ask = new XYChart.Series();
    private float min_valore_line = Float.MAX_VALUE;
    private float max_valore_line = -1;

    private final double gap_grafici = 1;

    @FXML
    public void opacitySlider() throws Exception {
        ((Stage) drag_panel.getScene().getWindow()).setOpacity(slider_opacity.getValue() / 100);
    }

    @FXML
    public void delaySlider() throws Exception {
        int v = (int) Math.round(delay_opacity.getValue()) * 1000;
        bcb.setDelay(v);
        delay_label.setText(v + "");
    }

    @FXML
    public void toast() {
        JFXSnackbar dialog = new JFXSnackbar(delayPane);
        dialog.setStyle("-fx-background-color : red;");

        dialog.show("Delay changed to " + (Math.round(delay_opacity.getValue())) + " seconds", 3000);
    }

    private void readHistoryData(XYChart<?, ?> chart, Lettore lettore, String path, boolean daCentrare) {

        try {
            lettore.leggi(path, chart, zoom_group_value);
        } catch (IOException ex) {
            System.err.println("file inesistente");
        }

        if (daCentrare) {
            AnchorPane.setTopAnchor(chart, 0.0);
            AnchorPane.setRightAnchor(chart, 0.0);
            AnchorPane.setLeftAnchor(chart, 0.0);
            AnchorPane.setBottomAnchor(chart, 0.0);
        }

    }

    private void updateGraficoLinea(float valore, float bid, float ask) {
        list.getItems().add(new Label(valore + ""));

        cal.setTime(new Date());
        String time = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

        valori_line.getData().add(new XYChart.Data(time, valore));
        valori_line_bid.getData().add(new XYChart.Data(time, bid));
        valori_line_ask.getData().add(new XYChart.Data(time, ask));

        float v = Math.min(valore, Math.min(bid, ask));
        float m = Math.max(valore, Math.max(bid, ask));

        if (v < min_valore_line) {
            min_valore_line = v;
            NumberAxis yAxis = ((NumberAxis) lineChart.getYAxis());
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(min_valore_line - gap_grafici);
        }
        if (m > max_valore_line) {
            max_valore_line = m;
            NumberAxis yAxis = ((NumberAxis) lineChart.getYAxis());
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(max_valore_line + gap_grafici);
        }
    }

    private void updateValue(float valore) {
        value_label.setText(valore + " USD");
    }

    private void updateTime() {
        cal.setTime(new Date());
        data_label.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + "\t"
                + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
    }

    private void updateGraficoScatter(float valore) {

        cal.setTime(new Date());
        String time = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        valori_scatter.getData().add(new XYChart.Data(time, valore));

        if (valore < min_valore_scatter) {
            min_valore_scatter = valore;
            NumberAxis yAxis = ((NumberAxis) scatterChart.getYAxis());
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(min_valore_scatter - gap_grafici);
        }
        if (valore > max_valore_scatter) {
            max_valore_scatter = valore;
            NumberAxis yAxis = ((NumberAxis) scatterChart.getYAxis());
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(max_valore_scatter + gap_grafici);
        }
    }

    private void updateChanges(Cambiamenti changes) {

        Color verde = Color.rgb(14, 150, 84);
        Color rosso = Color.rgb(243, 98, 45);
        
        for (int i = 0; i < changes_perc.getChildren().size(); i++) {
            HBox h = (HBox) changes_perc.getChildren().get(i);
            Label l = ((Label) (h.getChildren().get(1)));
            float val = changes.getPerc(i);

            String segno = "+";
            Color col = verde;

            if (val < 0) {
                segno = "";
                col = rosso;
            }

            String text = segno + "" + val + " %";

            if (!text.equals(l.getText())) {
                l.setText(text);
                l.setTextFill(col);
                /*
                JFXFillTransition t = new JFXFillTransition();
                
                t.setDuration(Duration.millis(400));
                t.setRegion(h);
                
                t.setFromValue(Color.rgb(32, 34, 37));  //non cambiare!
                t.setFromValue(col);
                
                t.play();
                 */

            }
        }

        for (int i = 0; i < changes_price.getChildren().size(); i++) {
            HBox h = (HBox) changes_price.getChildren().get(i);
            Label l = ((Label) (h.getChildren().get(1)));
            float val = changes.getPrice(i);

            String segno = "+";

            if (val < 0) {
                segno = "";
                l.setTextFill(rosso);
            } else {
                l.setTextFill(verde);
            }

            l.setText(segno + "" + val + " $");

        }
    }

    private void updateGraficoTorta(float volumePerc_value, float volume_value) {

        ObservableList<Data> answer = FXCollections.observableArrayList();
        answer.addAll(new PieChart.Data("Traded", volumePerc_value), new PieChart.Data("Kept", 100 - volumePerc_value));
        pieChart.setData(answer);
        
        volumePerc.setText(volumePerc_value+" %");
        volume.setText(volume_value+" $");
    }
    
    
    @FXML
    public void openSettings(){
        chiudiTutti(homePane,notePane,profilePane);
        delayPane.setVisible(true);
    }
        @FXML
    public void openHome(){
        chiudiTutti(delayPane,notePane,profilePane);
        homePane.setVisible(true);
    }
        @FXML
    public void openProfile(){
        chiudiTutti(homePane,notePane,delayPane);
        profilePane.setVisible(true);
    }
        @FXML
    public void openNote(){
        chiudiTutti(homePane,delayPane,profilePane);
        notePane.setVisible(true);
    }

    private void chiudiTutti(Pane... panes) {
        for (int i = 0; i < panes.length; i++) {
            panes[i].setVisible(false);
        }
    }

}
