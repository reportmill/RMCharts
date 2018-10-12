package rmcharts.app;
import java.util.*;
import snap.gfx.*;
import snap.util.SnapUtils;
import snap.view.*;

/**
 * A class to draw actual view.
 */
public class ChartArea extends View {

    // The list of series
    List <DataSeries>   _series = new ArrayList();
    
    // The series start
    int                 _seriesStart = 2010;
    
    // The Data point
    DataPoint           _dataPoint;
    
    // The amount of the chart to show horizontally (0-1)
    double              _reveal = 1;
    
    // Constants
    public static String   Reveal_Prop = "Reveal";
    public static String   DataPoint_Prop = "DataPoint";
    static Color           AXIS_LINES_COLOR = Color.LIGHTGRAY;
    static Color           AXIS_LABELS_COLOR = Color.GRAY;
    static Stroke          Stroke3 = new Stroke(3), Stroke4 = new Stroke(4), Stroke5 = new Stroke(5);

/**
 * Creates a ChartArea.
 */
public ChartArea()
{
    setGrowWidth(true); setPrefSize(600,350);
    enableEvents(MouseMove, MouseExit);
}

/**
 * Returns the series.
 */
public List <DataSeries> getSeries()  { return _series; }

/**
 * Returns the number of series.
 */
public int getSeriesCount()  { return _series.size(); }

/**
 * Returns the individual series at given index.
 */
public DataSeries getSeries(int anIndex)  { return _series.get(anIndex); }

/**
 * Adds a new series.
 */
public void addSeries(DataSeries aSeries)
{
    aSeries._index = _series.size();
    _series.add(aSeries);
}

/**
 * Adds a new series for given name and values.
 */
public void addSeriesForNameAndValues(String aName, double ... theVals)
{
    DataSeries series = new DataSeries(); series.setName(aName); series.setValues(theVals);
    addSeries(series);
}

/**
 * Returns the active series.
 */
public List <DataSeries> getSeriesActive()
{
    List series = new ArrayList();
    for(DataSeries s : _series) if(s.isEnabled()) series.add(s);
    return series;
}

/**
 * Returns the start of the series.
 */
public int getSeriesStart()  { return _seriesStart; }

/**
 * Sets the start of the series.
 */
public void setSeriesStart(int aValue)
{
    _seriesStart = aValue;
    repaint();
}

/**
 * Returns the length of the series.
 */
public int getSeriesLength()  { return _series.get(0).getCount(); }

/**
 * Returns the currently highlighted datapoint.
 */
public DataPoint getDataPoint()  { return _dataPoint; }

/**
 * Sets the currently highlighted datapoint.
 */
public void setDataPoint(DataPoint aDP)
{
    if(SnapUtils.equals(aDP, _dataPoint)) return;
    firePropChange(DataPoint_Prop, _dataPoint, _dataPoint = aDP);
    dataPointChanged();
    repaint();
}

/**
 * Sets the datapoint based on the X/Y location.
 */
public void setDataPointAtPoint(double aX, double aY)
{
    DataPoint dataPoint = _dataPoint;
    Point lastPointLocal = dataPoint!=null? dataPoint.getDataPointLocal() : new Point(1000,1000);
    double dist = Point.getDistance(aX, aY, lastPointLocal.x, lastPointLocal.y);
    
    List <DataSeries> seriesList = getSeriesActive();
    for(int i=0;i<seriesList.size();i++) { DataSeries series = seriesList.get(i);
        for(int j=0;j<getSeriesLength();j++) {
            Point pnt = seriesToLocal(j,series.getValue(j));
            double d = Point.getDistance(aX, aY, pnt.x, pnt.y);
            if(d<dist) { dist = d;
                dataPoint = new DataPoint(); dataPoint.series = series; dataPoint.index = j; }
        }
    }
    
    setDataPoint(dataPoint);
}

/**
 * Return the ratio of the chart to show horizontally.
 */
public double getReveal()  { return _reveal; }

/**
 * Sets the reation of the chart to show horizontally.
 */
public void setReveal(double aValue)
{
    _reveal = aValue;
    repaint();
}

/**
 * Registers for animation.
 */
public void animate()
{
    setReveal(0);
    getAnimCleared(1000).setValue(Reveal_Prop,1).setInterpolator(snap.util.Interpolator.LINEAR).play();
}

/**
 * Converts a point from series to local.
 */
public Point seriesToLocal(double aX, double aY)
{
    // Convert X
    int count = getSeriesLength();
    double w = getWidth() - 40 - 10;
    double dx = w/(count-1);
    double nx = 40 + aX*dx;

    // Convert Y and return
    double h = getHeight() - 4;
    double ny = h - aY/200000d*h;
    return new Point(nx, ny);
}

/**
 * Returns the list of paths for each series.
 */
public List <Path> getSeriesPaths()
{
    // Get series paths
    List <Path> paths = new ArrayList();
    int count = getSeriesLength();
    
    // Iterate over series
    for(int i=0; i<_series.size();i++) { DataSeries series = _series.get(i); if(series.isDisabled()) continue;
    
        Path path = new Path(); paths.add(path);
        
        // Iterate over values
        for(int j=0;j<count;j++) { double val = series.getValue(j);
            Point p = seriesToLocal(j, val);
            if(j==0) path.moveTo(p.x,p.y); else path.lineTo(p.x,p.y);
        }
    }
    return paths;
}

/**
 * Paints chart.
 */
protected void paintFront(Painter aPntr)
{
    // Get Series list
    List <DataSeries> seriesList = getSeriesActive();
    int scount = seriesList.size();
    
    double h = getHeight() - 4, w = getWidth() - 40;
    int count = getSeriesLength();
    DataPoint dpnt = getDataPoint();
    DataSeries dps = dpnt!=null? dpnt.series : null;
    
    // Draw axis
    for(int i=0;i<5;i++) {
        
        // Draw lines
        aPntr.setColor(AXIS_LINES_COLOR); aPntr.setStroke(Stroke.Stroke1);
        double y = h/4*i; if(y>=getHeight()) y--;
        aPntr.drawLine(40, y, getWidth(), y);
        
        // Draw labels
        aPntr.setFont(Font.Arial12); aPntr.setColor(AXIS_LABELS_COLOR);
        aPntr.drawString((200-i*50) + "k", 0, y + 4);
    }
    
    // If reveal is not full (1) then clip
    if(getReveal()<1) {
        aPntr.save(); aPntr.clipRect(0,0,getWidth()*getReveal(),getHeight()); }
        
    // Draw series paths
    List <Path> paths = getSeriesPaths();
    for(int i=0;i<paths.size();i++) { Path path = paths.get(i); DataSeries series = seriesList.get(i);
        aPntr.setColor(ChartLegend.COLORS[series.getIndex()]);
        aPntr.setStroke(Stroke.Stroke2); if(series==dps) aPntr.setStroke(Stroke3);
        aPntr.draw(path);
    }
    
    // Draw series points
    for(int i=0; i<scount;i++) { DataSeries series = seriesList.get(i);
    
        // Iterate over values
        for(int j=0;j<count;j++) { double val = series.getValue(j);
        
            Point p = seriesToLocal(j, val);
            
            Shape marker = ChartLegend.SHAPES[series.getIndex()].copyFor(new Transform(p.x-4,p.y-4));
            Color c = ChartLegend.COLORS[series.getIndex()];
            
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
 * Handle events.
 */
protected void processEvent(ViewEvent anEvent)
{
    // Handle MouseMove
    if(anEvent.isMouseMove() || anEvent.isMouseClick())
        setDataPointAtPoint(anEvent.getX(), anEvent.getY());
        
    // Handle MouseExit
    if(anEvent.isMouseExit())
        setDataPoint(null);
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
    DataSeries series = dataPoint.series; int index = dataPoint.index;
    
    // Remove ShapeView
    if(_dataPointView.getChild(0) instanceof ShapeView) _dataPointView.removeChild(0);
    _dataPointView.setOpacity(1);
    
    // Set KeyLabel string
    StringView keyLabel = (StringView)_dataPointView.getChild(0);
    String key = String.valueOf(getSeriesStart() + getDataPoint().index);
    keyLabel.setText(key);
    
    // Remove row views
    while(_dataPointView.getChildCount()>2) _dataPointView.removeChild(2);
    
    // Set border and bullet color
    RowView rview = (RowView)_dataPointView.getChild(1);
    Color color = ChartLegend.COLORS[series.getIndex()];
    rview.getChild(0).setFill(color);
    
    // Set NameLabel string
    StringView nameLabel = (StringView)rview.getChild(1);
    nameLabel.setText(series.getName() + ":");
    StringView valLabel = (StringView)rview.getChild(2);
    valLabel.setText(ChartView._fmt.format(dataPoint.getSeriesValue()));
    
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

/**
 * Returns the value for given key.
 */
public Object getValue(String aPropName)
{
    if(aPropName.equals(Reveal_Prop)) return getReveal();
    return super.getValue(aPropName);
}

/**
 * Sets the value for given key.
 */
public void setValue(String aPropName, Object aValue)
{
    if(aPropName.equals(Reveal_Prop)) setReveal(SnapUtils.doubleValue(aValue));
    else super.setValue(aPropName, aValue);
}

/**
 * A class to hold a data point.
 */
public class DataPoint {
    
    // The series
    DataSeries   series;
    
    // The index
    int          index;
    
    /** Return series name. */
    public String getSeriesName()  { return series.getName(); }
    
    /** Return series key. */
    public double getSeriesKey()  { return _seriesStart + index; }
    
    /** Return series value. */
    public double getSeriesValue()  { return series.getValue(index); }
    
    /** Returns the DataPoint in local coords. */
    public Point getDataPointLocal()  { return seriesToLocal(index, getSeriesValue()); }
    
    /** Standard equals implementation. */
    public boolean equals(Object anObj)
    {
        DataPoint other = anObj instanceof DataPoint? (DataPoint)anObj : null; if(other==null) return false;
        return other.series==series && other.index==index;
    }
}

}