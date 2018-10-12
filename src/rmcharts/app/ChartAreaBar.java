package rmcharts.app;
import java.util.List;
import rmcharts.app.ChartArea.DataPoint;
import snap.gfx.*;
import snap.view.*;

/**
 * A custom class.
 */
public class ChartAreaBar extends ChartArea {

/**
 * Pains chart.
 */
protected void paintFront(Painter aPntr)
{
    // Do normal version to paint axis
    super.paintFront(aPntr);
    
    // Get active series and count
    List <DataSeries> seriesList = getSeriesActive();
    int scount = seriesList.size();
    
    double h = getHeight() - 4, w = getWidth() - 40;
    int dcount = getSeriesLength();
    DataPoint dpnt = getDataPoint();
    
    // If reveal is not full (1) then clip
    if(getReveal()<1) {
        aPntr.save(); aPntr.clipRect(0,getHeight()*(1-getReveal()),getWidth(),getHeight()*getReveal()); }
        
    // Get width of an individual section
    double sw = w/dcount;
    
    // Get width of individual bar (bar count + bar spaces + bar&space at either end)
    double barW = sw/(scount + (scount-1) + 4);
        
    // Iterate over sections
    for(int i=0;i<dcount;i++) {
        
        // If selected section, draw background
        if(dpnt!=null && i==dpnt.index) {
            aPntr.setColor(Color.get("#4488FF09")); aPntr.fillRect(i*sw+40,0,sw,h); }
        
        // Iterate over series
        for(int j=0;j<scount;j++) { DataSeries series = seriesList.get(j);
        
            int sind = getSeries().indexOf(series);
            double val = series.getValue(i);
            
            // Draw bar
            aPntr.setColor(ChartLegend.COLORS[sind]);
            double bx = i*sw + 40 + (j+1)*2*barW, by = seriesToLocal(i, val).y, bh = h - by;
            aPntr.fillRect(bx,by,barW, bh);
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
    
    double w = getWidth() - 40;
    int dcount = getSeriesLength();
    double sw = w/dcount;
    int section = (int)((aX-40)/sw);
        
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
        
    // Get series and index
    int index = dataPoint.index;
    
    // Remove ShapeView
    if(_dataPointView.getChild(0) instanceof ShapeView) _dataPointView.removeChild(0);
    _dataPointView.setOpacity(1);
    
    // Set KeyLabel string
    StringView keyLabel = (StringView)_dataPointView.getChild(0);
    String key = String.valueOf(getSeriesStart() + getDataPoint().index);
    keyLabel.setText(key);
    
    // Remove row views
    while(_dataPointView.getChildCount()>1) _dataPointView.removeChild(1);
    
    // Iterate over series
    List <DataSeries> seriesList = getSeriesActive();
    for(int i=0;i<seriesList.size();i++) { DataSeries series = seriesList.get(i);
    
        RowView rview = new RowView(); rview.setSpacing(5);
        _dataPointView.addChild(rview);
        rview.addChild(new ShapeView(new Ellipse(0,0,5,5)));
        StringView sview2 = new StringView(); sview2.setFont(Font.Arial12); rview.addChild(sview2);
        StringView sview3 = new StringView(); sview3.setFont(Font.Arial12.deriveFont(13).getBold()); rview.addChild(sview3);
    
        // Set border and bullet color
        Color color = ChartLegend.COLORS[series.getIndex()];
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
    double w = getWidth() - 40; int dcount = getSeriesLength(); double sw = w/dcount;
    double px = index*sw + 40 + sw/2;
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