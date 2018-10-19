package rmcharts.app;
import java.util.*;
import snap.gfx.*;
import snap.util.SnapUtils;
import snap.view.*;

/**
 * A view to display the actual contents of a chart.
 */
public class ChartArea extends View {
    
    // The ChartView that owns the area
    ChartView           _chartView;

    // The Data point
    DataPoint           _dataPoint;
    
    // The amount of the chart to show horizontally (0-1)
    double              _reveal = 1;
    
    // Constants
    public static String   Reveal_Prop = "Reveal";
    public static String   DataPoint_Prop = "DataPoint";
    static Color           AXIS_LINES_COLOR = Color.LIGHTGRAY;
    static Stroke          Stroke3 = new Stroke(3), Stroke4 = new Stroke(4), Stroke5 = new Stroke(5);

/**
 * Creates a ChartArea.
 */
public ChartArea()
{
    setGrowWidth(true); setPrefSize(600,350);
    enableEvents(MouseMove, MouseExit);
    setPadding(5,10,8,10);
}

/**
 * Returns the series.
 */
public List <DataSeries> getSeries()  { return _chartView.getSeries(); }

/**
 * Returns the number of series.
 */
public int getSeriesCount()  { return _chartView.getSeriesCount(); }

/**
 * Returns the individual series at given index.
 */
public DataSeries getSeries(int anIndex)  { return _chartView.getSeries(anIndex); }

/**
 * Returns the active series.
 */
public List <DataSeries> getSeriesActive()  { return _chartView.getSeriesActive(); }

/**
 * Returns the start of the series.
 */
public int getSeriesStart()  { return _chartView.getSeriesStart(); }

/**
 * Returns the length of the series.
 */
public int getSeriesLength()  { return _chartView.getSeriesLength(); }

/**
 * Returns the series color at index.
 */
public Color getSeriesColor(int anIndex)  { return _chartView.getSeriesColor(anIndex); }

/**
 * Returns the series shape at index.
 */
public Shape getSeriesShape(int anIndex)  { return _chartView.getSeriesShape(anIndex); }

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
    // Get insets
    Insets ins = getInsetsAll();
    
    // Convert X
    int count = getSeriesLength();
    double w = getWidth() - ins.getWidth();
    double dx = w/(count-1);
    double nx = ins.left + aX*dx;

    // Convert Y and return
    double h = getHeight() - ins.getHeight();
    double ny = ins.top + h - aY/200000d*h;
    return new Point(nx, ny);
}

/**
 * Paints chart axis lines.
 */
protected void paintFront(Painter aPntr)
{
    // Get insets and chart content width/height (minus insets)
    Insets ins = getInsetsAll();
    double pw = getWidth(), ph = getHeight();
    double w = pw - ins.getWidth();
    double h = ph - ins.getHeight();
    
    // Set axis line color and stroke
    aPntr.setColor(AXIS_LINES_COLOR); aPntr.setStroke(Stroke.Stroke1);
    
    // Get number of interval lines and interval height
    int icnt = 5;
    double ih = h/(icnt-1);
    
    // Draw y axis lines
    for(int i=0;i<5;i++) {
        double y = ins.top + i*ih;
        aPntr.drawLine(0, y, pw, y);
    }

    // Paint X Axis
    if(this instanceof ChartAreaBar) paintAxisXBar(aPntr, 0, ins.top, pw, h);
    else paintAxisX(aPntr, ins.left, ins.top, w, h);
    
    // Paint chart
    paintChart(aPntr, ins.left, ins.top, w, h);
}

/**
 * Paints chart axis lines.
 */
protected void paintAxisX(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get number of data points and section width
    int sectionCount = getSeriesLength();
    double sectionW = aW/(sectionCount-1);
    double parH = getHeight();
    
    // Draw x axis ticks
    for(int i=0;i<sectionCount;i++) {
        double x = aX + i*sectionW;
        aPntr.drawLine(x, aY + aH, x, parH);
    }
}

/**
 * Paints chart axis lines.
 */
protected void paintAxisXBar(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get number of data points and section width
    int sectionCount = getSeriesLength();
    double sectionW = aW/sectionCount;
    double parH = getHeight(), parW = aX + aW;
    
    // Draw x axis ticks
    for(int i=0;i<sectionCount+1;i++) {
        double x = aX + i*sectionW; if(x>=parW) x -= .5; else if(x<=0) x += .5;
        aPntr.drawLine(x, aY + aH, x, parH);
    }
}

/**
 * Paints chart content.
 */
protected void paintChart(Painter aPntr, double aX, double aY, double aW, double aH)  { }

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
    public double getSeriesKey()  { return getSeriesStart() + index; }
    
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

/**
 * A class to layout the ChartArea, ChartXAxis, ChartYAxis.
 */
public static class ChartAreaBox extends ParentView {
    
    // The X Axis view
    ChartXAxisView   _xaxis;
    
    // The Y Axis view
    ChartYAxisView   _yaxis;
    
    // The ChartArea
    ChartArea        _area;
    
    /**
     * Create ChartAreaBox.
     */
    public ChartAreaBox()
    {
        _xaxis = new ChartXAxisView();
        _yaxis = new ChartYAxisView();
        setChildren(_yaxis, _xaxis);
    }
    
    /** Sets the ChartArea. */
    public void setChartArea(ChartArea aCA)
    {
        if(_area!=null) removeChild(_area);
        addChild(_area = aCA, 1);
        _yaxis._chartArea = _xaxis._chartArea = aCA;
    }
    
    /** Calculates the preferred width. */
    protected double getPrefWidthImpl(double aH)
    {
        return _yaxis.getPrefWidth() + _area.getPrefWidth();
    }

    /** Calculates the preferred height. */
    protected double getPrefHeightImpl(double aW)
    {
        return _area.getPrefHeight() + _xaxis.getPrefHeight();
    }

    /** Actual method to layout children. */
    protected void layoutImpl()
    {
        double pw = getWidth(), ph = getHeight();
        double aw = _yaxis.getPrefWidth(), ah = _xaxis.getPrefHeight();
        double cw = pw - aw, ch = ph - ah;
        _yaxis.setBounds(0,0,aw,ch);
        _xaxis.setBounds(aw,ch,cw,ah);
        _area.setBounds(aw,0,cw,ch);
    }
}

}