package rmcharts.app;
import snap.gfx.*;
import snap.view.*;

/**
 * A view to show tooltip.
 */
public class ToolTipView extends ColView {
    
    // The ChartView
    ChartView      _chartView;
    
    // The series being tipped
    DataSeries     _series;
    
    // The value index being tipped
    int            _valIndex = -1;
    
    // A runnable to reload contents
    Runnable       _reloadLater, _reloadRun = () -> { reloadContentsNow(); _reloadLater = null; };

/**
 * Creates a ToolTipView.
 */
public ToolTipView(ChartView aCV)
{
    _chartView = aCV;
    setManaged(false); setPickable(false);
}

/**
 * Returns the series under tip.
 */
public DataSeries getSeries()  { return _series; }

/**
 * Sets the series under tip.
 */
public void setSeries(DataSeries aSeries)
{
    if(aSeries==getSeries()) return;
    _series = aSeries; if(aSeries==null) _valIndex = -1;
    reloadContents();
}

/**
 * Return series name.
 */
public String getSeriesName()  { return _series!=null? _series.getName() : null; }

/**
 * Return series key.
 */
public double getSeriesKey()  { return _chartView.getSeriesStart() + _valIndex; }

/**
 * Return series value.
 */
public double getSeriesValue()  { return _series!=null? _series.getValue(_valIndex) : 0; }

/**
 * Returns the x,y of data point in chart area coords.
 */
public Point getPointInChartArea()
{
    ChartArea chartArea = _chartView.getChartArea();
    return chartArea.dataPointInLocal(_series, _valIndex);
}
    
/**
 * Returns the x,y of data point in chart view coords.
 */
public Point getPointInChartView()
{
    Point pnt = getPointInChartArea();
    ChartArea chartArea = _chartView.getChartArea();
    return chartArea.localToParent(pnt.x, pnt.y, _chartView);
}
    
/**
 * Returns the value index under tip.
 */
public int getValueIndex()  { return _valIndex; }

/**
 * Sets the value index under tip.
 */
public void setValueIndex(int anIndex)
{
    if(anIndex==_valIndex) return;
    _valIndex = anIndex;
    reloadContents();
}

/**
 * Returns the tooltip series and value index as a DataPoint.
 */
public DataPoint getDataPoint()  { return _series!=null && _valIndex>=0? _series.getPoint(_valIndex) : null; }

/**
 * Sets the tooltip series and value index from DataPoint.
 */
public void setDataPoint(DataPoint aDP)
{
    if(aDP==null) { setSeries(null); setValueIndex(-1); }
    else { setSeries(aDP.getSeries()); setValueIndex(aDP.getIndex()); }
}

/**
 * Called when tooltip contents changed.
 */
public void reloadContents()
{
    if(_reloadLater==null)
        getEnv().runLater(_reloadLater = _reloadRun);
}

/**
 * Called when tooltip contents changed.
 */
protected void reloadContentsNow()
{
    // Go ahead and repaint chartView
    _chartView.repaint();
    
    // Get DataPoint - if null - remove view
    DataSet dset = _chartView.getDataSet();
    DataSeries series = getSeries();
    int valIndex = getValueIndex();
    if(series==null) {
        getAnimCleared(1000).setOpacity(0).setOnFinish(a -> _chartView.removeChild(this)).play(); return; }
        
    // Remove children and reset opacity, padding and spacing
    removeChildren(); setOpacity(1);
    setPadding(7,7,15,7); setSpacing(5);
    
    // Set KeyLabel string
    StringView keyLabel = new StringView(); keyLabel.setFont(Font.Arial10); addChild(keyLabel);
    String key = String.valueOf(dset.getSeriesStart() + valIndex);
    keyLabel.setText(key);
    
    // Create RowView: BulletView
    Color color = _chartView.getColor(series.getIndex());
    ShapeView bulletView = new ShapeView(new Ellipse(0,0,5,5)); bulletView.setFill(color);
    
    // Create RowView: NameLabel, ValLabel
    StringView nameLabel = new StringView(); nameLabel.setFont(Font.Arial12);
    nameLabel.setText(series.getName() + ":");
    StringView valLabel = new StringView(); valLabel.setFont(Font.Arial12.deriveFont(13).getBold());
    valLabel.setText(ChartView._fmt.format(getSeriesValue()));
    
    // Create RowView and add BulletView, NameLabel and ValLabel
    RowView rview = new RowView(); rview.setSpacing(5);
    rview.setChildren(bulletView, nameLabel, valLabel);
    addChild(rview);
    
    // Calculate and set new size, keeping same center
    double oldWidth = getWidth(), oldHeight = getHeight();
    double newWidth = getPrefWidth(), newHeight = getPrefHeight();
    setSize(newWidth, newHeight);
    setX(getX() - (newWidth/2 - oldWidth/2));
    setY(getY() - (newHeight/2 - oldHeight/2));
    
    // Create background shape
    RoundRect shp0 = new RoundRect(1,1,newWidth-2,newHeight-8,3); double midx = shp0.getMidX();
    Shape shp1 = new Polygon(midx-6,newHeight-8,midx+6,newHeight-8,midx,newHeight-2);
    Shape shp2 = Shape.add(shp0,shp1);
    
    // Create background shape view and add
    ShapeView shpView = new ShapeView(shp2); shpView.setManaged(false); shpView.setPrefSize(newWidth,newHeight+10);
    shpView.setFill(Color.get("#F8F8F8DD")); shpView.setBorder(color,1); //shpView.setEffect(new ShadowEffect());
    addChild(shpView, 0);
    
    // Colculate new location
    Point pnt = getPointInChartView();
    double nx = pnt.x - getWidth()/2;
    double ny = pnt.y - getHeight() - 8;
    
    // If not onscreen, add and return
    if(getParent()==null) {
        setXY(nx, ny); _chartView.addChild(this); return; }
        
    // Otherwise animate move
    getAnimCleared(300).setX(nx).setY(ny).play();
}

}