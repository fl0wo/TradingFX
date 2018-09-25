/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitcoinviewver.readers;

import bitcoinviewver.grafici.GraficoZoomabile;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.event.EventHandler;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

/**
 *
 * @author saban
 */
public class LettoreHistoryData implements Lettore {

    @Override
    public void leggi(String filePath, XYChart<?, ?> grafico,Pane padre) throws FileNotFoundException, IOException{
        
        InputStream br = new FileInputStream(filePath);  // InputStream
        InputStreamReader insr = new InputStreamReader(br);  // InputStreamReader
        BufferedReader buff = new BufferedReader(insr); // Bufferizziamo per efficenza

        XYChart.Series valori = new XYChart.Series();

        String str, data, prezzo;

        float min = Float.MAX_VALUE,max = -1,prezzo_float;
        
        while ((str = buff.readLine()) != null) {
            int id = str.indexOf(',') + 1;

            data = str.substring(1, 11);
            prezzo = str.substring(id, str.length());

            prezzo_float = Float.parseFloat(prezzo);
            min = Math.min(min,prezzo_float);
            max = Math.max(max, prezzo_float);
            
            valori.getData().add(new XYChart.Data(data, prezzo_float));

            for (int i = 0; i < 30; i++) {
                buff.readLine();
            }
        }
        valori.setName("History Data");
        
        grafico.getData().add(valori);
        
        NumberAxis axis = (NumberAxis) grafico.getYAxis();
        
        axis.setAutoRanging(false);
        
        axis.setLowerBound(min);
        axis.setUpperBound(max);
        
        final double lowerX = axis.getLowerBound();
        final double upperX = axis.getUpperBound();
        
        grafico.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {
                
                System.out.println("diomerda");
                
                final double minX = axis.getLowerBound();
                final double maxX = axis.getUpperBound();
                double threshold = minX + (maxX - minX) / 2d;
                double x = event.getY();
                double value = axis.getValueForDisplay(x).doubleValue();
                double direction = event.getDeltaX();
               
                
                if (direction > 0) {
                    if (maxX - minX <= 1) {
                        return;
                    }
                    if (value > threshold) {
                        axis.setLowerBound(minX + 1);
                    } else {
                        axis.setUpperBound(maxX - 1);
                    }
                } else {
                    if (value < threshold) {
                        double nextBound = Math.max(lowerX, minX - 1);
                        axis.setLowerBound(nextBound);
                    } else {
                        double nextBound = Math.min(upperX, maxX + 1);
                        axis.setUpperBound(nextBound);
                    }
                }

            }
        });

        
        //new GraficoZoomabile(padre,grafico,valori);
    }
    
}
