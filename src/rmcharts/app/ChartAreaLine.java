package rmcharts.app;
import java.util.*;
import snap.gfx.*;

/**
 * A ChartArea subclass to display the contents of line chart.
 */
public class ChartAreaLine extends ChartArea {

/**
 * Returns the list of paths for each series.
 */
public List <Path> getSeriesPaths()
{
    // Get series paths
    List <Path> paths = new ArrayList();
    int scount = getSeriesCount();
    int slen = getValueCount();
    
    // Iterate over series
    for(int i=0; i<scount;i++) { DataSeries series = getSeries(i); if(series.isDisabled()) continue;
    
        Path path = new Path(); paths.add(path);
        
        // Iterate over values
        for(int j=0;j<slen;j++) { double val = series.getValue(j);
            Point p = seriesToLocal(j, val);
            if(j==0) path.moveTo(p.x,p.y); else path.lineTo(p.x,p.y);
        }
    }
    return paths;
}

/**
 * Paints chart.
 */
protected void paintChart(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get Series list
    List <DataSeries> seriesList = getSeriesActive();
    int scount = seriesList.size();
    
    int count = getValueCount();
    DataSeries selSeries = _chartView.getToolTipView().getSeries();
    int selValIndex = _chartView.getToolTipView().getValueIndex();
    
    // If reveal is not full (1) then clip
    if(getReveal()<1) {
        aPntr.save(); aPntr.clipRect(0,0,getWidth()*getReveal(),getHeight()); }
        
    // Draw series paths
    List <Path> paths = getSeriesPaths();
    for(int i=0;i<paths.size();i++) { Path path = paths.get(i); DataSeries series = seriesList.get(i);
        aPntr.setColor(getSeriesColor(series.getIndex()));
        aPntr.setStroke(Stroke.Stroke2); if(series==selSeries) aPntr.setStroke(Stroke3);
        aPntr.draw(path);
    }
    
    // Draw series points
    for(int i=0; i<scount;i++) { DataSeries series = seriesList.get(i);
    
        // Iterate over values
        for(int j=0;j<count;j++) { double val = series.getValue(j);
        
            Point p = seriesToLocal(j, val);
            
            Shape marker = getSeriesShape(series.getIndex()).copyFor(new Transform(p.x-4,p.y-4));
            Color c = getSeriesColor(series.getIndex());
            
            if(series==selSeries && j==selValIndex) {
                aPntr.setColor(c.blend(Color.CLEARWHITE, .5));
                aPntr.fill(new Ellipse(p.x-10,p.y-10,20,20));
                aPntr.setStroke(Stroke5); aPntr.setColor(Color.WHITE); aPntr.draw(marker);
                aPntr.setStroke(Stroke3); aPntr.setColor(c); aPntr.draw(marker);
            }
            aPntr.setColor(c); aPntr.fill(marker);
        }
    }
    
    // If reveal not full, resture gstate
    if(getReveal()<1) aPntr.restore();
}

}