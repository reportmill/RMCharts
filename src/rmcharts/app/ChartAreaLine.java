package rmcharts.app;
import java.util.*;
import snap.gfx.*;
import snap.view.*;

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
    DataPoint dpnt = getDataPoint();
    DataSeries dps = dpnt!=null? dpnt.series : null;
    
    // If reveal is not full (1) then clip
    if(getReveal()<1) {
        aPntr.save(); aPntr.clipRect(0,0,getWidth()*getReveal(),getHeight()); }
        
    // Draw series paths
    List <Path> paths = getSeriesPaths();
    for(int i=0;i<paths.size();i++) { Path path = paths.get(i); DataSeries series = seriesList.get(i);
        aPntr.setColor(getSeriesColor(series.getIndex()));
        aPntr.setStroke(Stroke.Stroke2); if(series==dps) aPntr.setStroke(Stroke3);
        aPntr.draw(path);
    }
    
    // Draw series points
    for(int i=0; i<scount;i++) { DataSeries series = seriesList.get(i);
    
        // Iterate over values
        for(int j=0;j<count;j++) { double val = series.getValue(j);
        
            Point p = seriesToLocal(j, val);
            
            Shape marker = getSeriesShape(series.getIndex()).copyFor(new Transform(p.x-4,p.y-4));
            Color c = getSeriesColor(series.getIndex());
            
            if(series==dps && j==dpnt.index) {
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
    _dataPointView.setPadding(7,7,15,7); _dataPointView.setSpacing(5);
     
    // Get series and index
    DataSeries series = dataPoint.series; int index = dataPoint.index;
    
    // Set KeyLabel string
    StringView keyLabel = new StringView(); keyLabel.setFont(Font.Arial10); _dataPointView.addChild(keyLabel);
    String key = String.valueOf(getSeriesStart() + getDataPoint().index);
    keyLabel.setText(key);
    
    // Create RowView: BulletView
    Color color = getSeriesColor(series.getIndex());
    ShapeView bulletView = new ShapeView(new Ellipse(0,0,5,5)); bulletView.setFill(color);
    
    // Create RowView: NameLabel, ValLabel
    StringView nameLabel = new StringView(); nameLabel.setFont(Font.Arial12);
    nameLabel.setText(series.getName() + ":");
    StringView valLabel = new StringView(); valLabel.setFont(Font.Arial12.deriveFont(13).getBold());
    valLabel.setText(ChartView._fmt.format(dataPoint.getSeriesValue()));
    
    // Create RowView and add BulletView, NameLabel and ValLabel
    RowView rview = new RowView(); rview.setSpacing(5);
    rview.setChildren(bulletView, nameLabel, valLabel);
    _dataPointView.addChild(rview);
    
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
    shpView.setFill(Color.get("#F8F8F8DD")); shpView.setBorder(color,1); //shpView.setEffect(new ShadowEffect());
    _dataPointView.addChild(shpView, 0);
    
    // Colculate new location
    Point pnt = dataPoint.getDataPointLocal(); pnt = localToParent(pnt.x, pnt.y, chartView);
    double nx = pnt.x - _dataPointView.getWidth()/2;
    double ny = pnt.y - _dataPointView.getHeight() - 8;
    
    // If not onscreen, add and return
    if(_dataPointView.getParent()==null) {
        _dataPointView.setXY(nx, ny); chartView.addChild(_dataPointView); return; }
        
    // Otherwise animate move
    _dataPointView.getAnimCleared(300).setX(nx).setY(ny).play();
}

}