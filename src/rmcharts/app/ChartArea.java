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
 * Returns the XAxis View.
 */
public ChartXAxisView getXAxis()  { return _chartView._xaxis; }

/**
 * Returns the YAxis View.
 */
public ChartYAxisView getYAxis()  { return _chartView._yaxis; }

/**
 * Returns the data set.
 */
public DataSet getDataSet()  { return _chartView.getDataSet(); }

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
public int getValueCount()  { return getDataSet().getValueCount(); }

/**
 * Returns the intervals.
 */
public Intervals getIntervals()  { return _chartView.getIntervals(); }

/**
 * Returns the series color at index.
 */
public Color getSeriesColor(int anIndex)  { return _chartView.getSeriesColor(anIndex); }

/**
 * Returns the series shape at index.
 */
public Shape getSeriesShape(int anIndex)  { return _chartView.getSeriesShape(anIndex); }

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
    int count = getValueCount();
    double w = getWidth() - ins.getWidth();
    double dx = w/(count-1);
    double nx = ins.left + aX*dx;

    // Convert Y and return
    double axisMinVal = getIntervals().getMin();
    double axisMaxVal = getIntervals().getMax();
    double h = getHeight() - ins.getHeight();
    double ny = ins.top + h - (aY-axisMinVal)/(axisMaxVal-axisMinVal)*h;
    return new Point(nx, ny);
}

/**
 * Returns the given data point (series + value index) in local coords.
 */
public Point dataPointInLocal(DataSeries aSeries, int anIndex)
{
    return seriesToLocal(anIndex, aSeries.getValue(anIndex));
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
    aPntr.setColor(AXIS_LINES_COLOR);
    double lineWidth = 1;
    double dashes[] = getYAxis().getGridLineDashArray();
    Stroke stroke = dashes==null && lineWidth==1? Stroke.Stroke1 : new Stroke(lineWidth, dashes, 0);
    aPntr.setStroke(stroke);
    
    // Have YAxisView paint lines
    paintAxisY(aPntr, 0, ins.top, pw, h);
    
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
    int sectionCount = getValueCount();
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
protected void paintAxisY(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get number of interval lines and interval height
    int intervalCount = getIntervals().getCount();
    double ih = aH/(intervalCount-1);
    
    // Draw y axis lines
    for(int i=0;i<intervalCount;i++) {
        double y = aY + i*ih; y = Math.round(y);
        aPntr.drawLine(0, y, aW, y);
    }
    
    aPntr.setStroke(Stroke.Stroke1);
}

/**
 * Paints chart axis lines.
 */
protected void paintAxisXBar(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get number of data points and section width
    int sectionCount = getValueCount();
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
        updateToolTipForPoint(anEvent.getX(), anEvent.getY());
        
    // Handle MouseExit
    if(anEvent.isMouseExit())
        updateToolTipForPoint(-1,-1);
}

/**
 * Sets the datapoint based on the X/Y location.
 */
protected void updateToolTipForPoint(double aX, double aY)
{
    // Get ToolTipView
    ToolTipView toolTip = _chartView.getToolTipView();
    
    // If point out of bounds, clear tool tip
    if(aX<0 || aY<0) { toolTip.setDataPoint(null); return; }
    
    // Find new series and value index for point
    DataSeries selSeries = toolTip.getSeries();
    int selIndex = toolTip.getValueIndex();
    Point lastPointLocal = selSeries!=null? toolTip.getPointInChartArea() : new Point(2000,2000);
    double dist = Point.getDistance(aX, aY, lastPointLocal.x, lastPointLocal.y);
    
    List <DataSeries> seriesList = getSeriesActive();
    for(int i=0;i<seriesList.size();i++) { DataSeries series = seriesList.get(i);
        for(int j=0;j<getValueCount();j++) {
            Point pnt = seriesToLocal(j,series.getValue(j));
            double d = Point.getDistance(aX, aY, pnt.x, pnt.y);
            if(d<dist) { dist = d;
                selSeries = series; selIndex = j; }
        }
    }
    
    toolTip.setSeries(selSeries);
    toolTip.setValueIndex(selIndex);
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

}