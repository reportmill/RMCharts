package rmcharts.app;
import java.util.List;
import rmcharts.app.ChartArea.DataPoint;
import snap.gfx.*;
import snap.view.*;

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
    int valueCount = getSeriesLength();
    DataPoint dpnt = getDataPoint();
    
    // If reveal is not full (1) then clip
    if(getReveal()<1) {
        aPntr.save(); aPntr.clipRect(0,getHeight()*(1-getReveal()),getWidth(),getHeight()*getReveal()); }
        
    // Get width of an individual section
    double sectionW = aW/valueCount;
    
    // Get width of group
    double groupPadding = .2, groupWidthRatio = 1 - groupPadding*2;
    double groupW = groupWidthRatio>=0? groupWidthRatio*sectionW : 1;
    double groupPadW = (sectionW - groupW)/2;
    
    // Get width of individual bar (bar count + bar spaces + bar&space at either end)
    double barPadding = .1, barWidthRatio = 1 - barPadding*2;
    double barW = barWidthRatio>=0? barWidthRatio*groupW/seriesCount : 1;
    double barPadW = barWidthRatio>=0? barPadding*groupW/seriesCount : 1;
        
    // Iterate over sections
    for(int i=0;i<valueCount;i++) {
        
        // If selected section, draw background
        if(dpnt!=null && i==dpnt.index) {
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
 * Sets the datapoint based on the X/Y location.
 */
public void setDataPointAtPoint(double aX, double aY)
{
    if(aX<0 || aX>getWidth() || aY<0 || aY>getHeight()) { setDataPoint(null); return; }
    
    Insets ins = getInsetsAll();
    double w = getWidth() - ins.getWidth();
    int dcount = getSeriesLength();
    double sw = w/dcount;
    int section = (int)((aX-ins.left)/sw);
        
    DataPoint dataPoint = _dataPoint;
    if(dataPoint==null || dataPoint.index!=section) {
        dataPoint = new DataPoint(); dataPoint.series = getSeries(0); dataPoint.index = section; }
    
    setDataPoint(dataPoint);
}

/**
 * Called when ChartArea DataPoint changed.
 */
public void dataPointChanged()
{
    // Get chartView
    ChartView chartView = getParent(ChartView.class);
    ColView _dataPointView = chartView._dataPointView;
    
    // Get DataPoint - if null - remove view
    ChartArea.DataPoint dataPoint = getDataPoint();
    if(dataPoint==null) {
        _dataPointView.getAnimCleared(1000).setOpacity(0).setOnFinish(a -> chartView.removeChild(_dataPointView)).play();
        return;
    }
        
    // Remove children and reset opacity, padding and spacing
    _dataPointView.removeChildren(); _dataPointView.setOpacity(1);
    _dataPointView.setPadding(7,7,15,7); _dataPointView.setSpacing(2);
    
    // Set KeyLabel string
    StringView keyLabel = new StringView(); keyLabel.setFont(Font.Arial10); _dataPointView.addChild(keyLabel);
    String key = String.valueOf(getSeriesStart() + getDataPoint().index);
    keyLabel.setText(key);
    
    // Iterate over series
    List <DataSeries> seriesList = getSeriesActive();
    for(int i=0;i<seriesList.size();i++) { DataSeries series = seriesList.get(i);
    
        RowView rview = new RowView(); rview.setSpacing(5);
        _dataPointView.addChild(rview);
        rview.addChild(new ShapeView(new Ellipse(0,0,5,5)));
        StringView sview2 = new StringView(); sview2.setFont(Font.Arial12); rview.addChild(sview2);
        StringView sview3 = new StringView(); sview3.setFont(Font.Arial12.deriveFont(13).getBold()); rview.addChild(sview3);
    
        // Set border and bullet color
        Color color = getSeriesColor(series.getIndex());
        rview.getChild(0).setFill(color);
        
        // Set NameLabel string
        StringView nameLabel = (StringView)rview.getChild(1);
        nameLabel.setText(series.getName() + ":");
        StringView valLabel = (StringView)rview.getChild(2);
        valLabel.setText(ChartView._fmt.format(dataPoint.getSeriesValue()));
    }
    
    // Calculate and set new size, keeping same center
    double oldWidth = _dataPointView.getWidth(), oldHeight = _dataPointView.getHeight();
    double newWidth = _dataPointView.getPrefWidth(), newHeight = _dataPointView.getPrefHeight();
    _dataPointView.setSize(newWidth, newHeight);
    _dataPointView.setX(_dataPointView.getX() - (newWidth/2 - oldWidth/2));
    _dataPointView.setY(_dataPointView.getY() - (newHeight/2 - oldHeight/2));
    
    // Create background shape
    RoundRect shp0 = new RoundRect(1,1,newWidth-2,newHeight-8,3); double midx = shp0.getMidX();
    Shape shp1 = new Polygon(midx-6,newHeight-8,midx+6,newHeight-8,midx,newHeight-2);
    Shape shp2 = Shape.add(shp0,shp1);
    
    // Create background shape view and add
    ShapeView shpView = new ShapeView(shp2); shpView.setManaged(false); shpView.setPrefSize(newWidth,newHeight+10);
    shpView.setFill(Color.get("#F8F8F8DD")); shpView.setBorder(Color.LIGHTBLUE,1); //shpView.setEffect(new ShadowEffect());
    _dataPointView.addChild(shpView, 0);
    
    // Colculate new location
    //Insets ins = getInsetsAll(); double aX = ins.left, aW = getWidth() - ins.getWidth();
    double aX = 0, aW = getWidth();
    double w = aW; int dcount = getSeriesLength(); double sw = w/dcount;
    double px = aX + dataPoint.index*sw + sw/2;
    Point pnt = dataPoint.getDataPointLocal(); pnt = localToParent(px, pnt.y, chartView);
    double nx = pnt.x - _dataPointView.getWidth()/2;
    double ny = pnt.y - _dataPointView.getHeight() - 8;
    
    // If not onscreen, add and return
    if(_dataPointView.getParent()==null) {
        _dataPointView.setXY(nx, ny); chartView.addChild(_dataPointView); return; }
        
    // Otherwise animate move
    _dataPointView.getAnimCleared(300).setX(nx).setY(ny).play();
}

}