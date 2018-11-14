package rmcharts.app;
import java.util.List;
import snap.gfx.*;

/**
 * A ChartArea subclass to display the contents of bar chart.
 */
public class ChartAreaBar extends ChartArea {

/**
 * Paints chart.
 */
protected void paintChart(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // For bar chart, render to full width
    aX = 0; aW = getWidth();
    
    // Get active series and count
    List <DataSeries> seriesList = getSeriesActive();
    int seriesCount = seriesList.size();
    
    // Get number of values
    int valCount = getValueCount();
    int selSection = _chartView.getToolTipView().getValueIndex();
    
    // If reveal is not full (1) then clip
    if(getReveal()<1) {
        aPntr.save(); aPntr.clipRect(0,getHeight()*(1-getReveal()),getWidth(),getHeight()*getReveal()); }
        
    // Get width of an individual section
    double sectionW = aW/valCount;
    
    // Get width of group
    double groupPadding = .2, groupWidthRatio = 1 - groupPadding*2;
    double groupW = groupWidthRatio>=0? groupWidthRatio*sectionW : 1;
    double groupPadW = (sectionW - groupW)/2;
    
    // Get width of individual bar (bar count + bar spaces + bar&space at either end)
    double barPadding = .1, barWidthRatio = 1 - barPadding*2;
    double barW = barWidthRatio>=0? barWidthRatio*groupW/seriesCount : 1;
    double barPadW = barWidthRatio>=0? barPadding*groupW/seriesCount : 1;
        
    // Iterate over sections
    for(int i=0;i<valCount;i++) {
        
        // If selected section, draw background
        if(i==selSection) {
            aPntr.setColor(Color.get("#4488FF09")); aPntr.fillRect(aX + i*sectionW, aY, sectionW, aH); }
        
        // Iterate over series
        for(int j=0;j<seriesCount;j++) { DataSeries series = seriesList.get(j);
        
            int sind = getSeries().indexOf(series);
            double val = series.getValue(i);
            
            // Draw bar
            aPntr.setColor(getSeriesColor(sind));
            double bx = aX + i*sectionW + groupPadW + (j*2+1)*barPadW + j*barW;
            double by = seriesToLocal(i, val).y, bh = aY + aH - by;
            aPntr.fillRect(bx,by,barW, bh - .5);
        }
    }
    
    // If reveal not full, resture gstate
    if(getReveal()<1) aPntr.restore();
}

/**
 * Override to return point above bar.
 */
public Point dataPointInLocal(DataSeries aSeries, int anIndex)
{
    // Get width of an individual section
    double width = getWidth(), height = getHeight();
    int seriesIndex = getSeriesActive().indexOf(aSeries);
    int seriesCount = getSeriesActive().size();
    int valueCount = getValueCount();
    double sectionW = width/valueCount;
    
    // Get width of group
    double groupPadding = .2, groupWidthRatio = 1 - groupPadding*2;
    double groupW = groupWidthRatio>=0? groupWidthRatio*sectionW : 1;
    double groupPadW = (sectionW - groupW)/2;
    
    // Get width of individual bar (bar count + bar spaces + bar&space at either end)
    double barPadding = .1, barWidthRatio = 1 - barPadding*2;
    double barW = barWidthRatio>=0? barWidthRatio*groupW/seriesCount : 1;
    double barPadW = barWidthRatio>=0? barPadding*groupW/seriesCount : 1;
    
    double val = aSeries.getValue(anIndex);
    
    double bx = anIndex*sectionW + groupPadW + (seriesIndex*2+1)*barPadW + seriesIndex*barW;
    double by = seriesToLocal(anIndex, val).y, bh = height - by;
    
    return new Point(Math.round(bx + barW/2), Math.round(by));
}

/**
 * Returns the data point best associated with given x/y (null if none).
 */
protected DataPoint getDataPointAt(double aX, double aY)
{
    // For bar chart, render to full width
    double cx = 0, cy = 0, cw = getWidth(), ch = getHeight();
    
    // Get active series and count
    List <DataSeries> seriesList = getSeriesActive();
    int seriesCount = seriesList.size();
    
    // Get width of an individual section
    int valCount = getValueCount();
    double sectionW = cw/valCount;
    
    // Get width of group
    double groupPadding = .2, groupWidthRatio = 1 - groupPadding*2;
    double groupW = groupWidthRatio>=0? groupWidthRatio*sectionW : 1;
    double groupPadW = (sectionW - groupW)/2;
    
    // Get width of individual bar (bar count + bar spaces + bar&space at either end)
    double barPadding = .1, barWidthRatio = 1 - barPadding*2;
    double barW = barWidthRatio>=0? barWidthRatio*groupW/seriesCount : 1;
    double barPadW = barWidthRatio>=0? barPadding*groupW/seriesCount : 1;
    Rect bnds = new Rect();
        
    // Iterate over sections
    for(int i=0;i<valCount;i++) {
        
        // Iterate over series
        for(int j=0;j<seriesCount;j++) { DataSeries series = seriesList.get(j);
        
            double val = series.getValue(i);
            
            // Draw bar
            double bx = cx + i*sectionW + groupPadW + (j*2+1)*barPadW + j*barW;
            double by = seriesToLocal(i, val).y, bh = aY + ch - by;
            bnds.setRect(bx-1,by,barW+2, bh);
            if(bnds.contains(aX, aY))
                return new DataPoint(_chartView, series, i);
        }
    }
    return null;
}

}