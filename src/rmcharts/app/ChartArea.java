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
 * Paints chart.
 */
protected void paintFront(Painter aPntr)
{
    double h = getHeight() - 4;
    
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
public void dataPointChanged()  { }

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