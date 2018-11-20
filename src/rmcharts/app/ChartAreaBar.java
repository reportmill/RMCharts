package rmcharts.app;
import java.util.List;
import snap.gfx.*;

/**
 * A ChartArea subclass to display the contents of bar chart.
 */
public class ChartAreaBar extends ChartArea {
    
    // The ratio of a section used to pad a group of bars
    double             _groupPad = .2;
    
    // The ratio of a group used to pad a bar
    double             _barPad = .1;
    
    // Whether to use colors for each series value instead of series
    boolean            _colorValues;

    // The number of series and values to chart
    int                _seriesCount, _pointCount;
    
    // The width of a section (one for each series value)
    double             _sectionWidth;
    
    // The width of a group and the group pad in points
    double             _groupWidth, _groupPadWidth;
    
    // The width of a bar and the bar pad in points
    double             _barWidth, _barPadWidth;
    
    // The cached bars
    Bar                _bars[][];

/**
 * Creates a ChartAreaBar.
 */
public ChartAreaBar()
{
    setPadding(5,0,0,0); // Add top so top axis line isn't on edge
}

/**
 * Returns the group padding.
 */
public double getGroupPadding()  { return _groupPad; }

/**
 * Sets the group padding.
 */
public void setGroupPadding(double aValue)
{
    _groupPad = aValue;
    clearCache(); repaint();
}

/**
 * Returns the bar padding.
 */
public double getBarPadding()  { return _barPad; }

/**
 * Sets the bar padding.
 */
public void setBarPadding(double aValue)
{
    _barPad = aValue;
    clearCache(); repaint();
}

/**
 * Returns whether to use colors for each series value instead of series.
 */
public boolean isColorValues()  { return _colorValues; }

/**
 * Returns whether to use colors for each series value instead of series.
 */
public void setColorValues(boolean aValue)  { _colorValues = aValue; clearCache(); }

/**
 * Override to clear bar cache.
 */
public void setWidth(double aValue)  { super.setWidth(aValue); clearCache(); }

/**
 * Override to clear bar cache.
 */
public void setHeight(double aValue)  { super.setHeight(aValue); clearCache(); }

/**
 * Call to clear bar cache.
 */
protected void clearCache()  { _bars = null; }

/**
 * Override to recalculate group/point padding and width.
 */
protected Bar[][] getBars()
{
    // If recacl not needed, just return
    if(_bars!=null && _bars.length==getSeriesActive().size() && _bars[0].length==getPointCount()) return _bars;
    
    // Get ChartAreaBar info
    double cx = 0, cy = 0, cw = getWidth(), ch = getHeight();
    boolean colorSeries = !isColorValues();
    
    // Get number of series, points and section width
    List <DataSeries> seriesList = getSeriesActive();
    _seriesCount = getSeriesActive().size();
    _pointCount = getPointCount();
    _sectionWidth = getWidth()/_pointCount;

    // Get group widths
    double groupWidthRatio = 1 - _groupPad*2;
    _groupWidth = groupWidthRatio>=0? groupWidthRatio*_sectionWidth : 1;
    _groupPadWidth = (_sectionWidth - _groupWidth)/2;
    
    // Get width of individual bar (bar count + bar spaces + bar&space at either end)
    double barWidthRatio = 1 - _barPad*2;
    _barWidth = barWidthRatio>=0? barWidthRatio*_groupWidth/_seriesCount : 1;
    _barPadWidth = barWidthRatio>=0? _barPad*_groupWidth/_seriesCount : 1;
    
    // Create new bars array
    Bar bars[][] = new Bar[_seriesCount][_pointCount];
    
    // Iterate over sections
    for(int i=0;i<_pointCount;i++) {
        
        // Iterate over series
        for(int j=0;j<_seriesCount;j++) { DataSeries series = seriesList.get(j);
            DataPoint dataPoint = series.getPoint(i);
            double val = dataPoint.getValue();
            
            // Draw bar
            Color color = colorSeries? getColor(series.getIndex()) : getColor(i);
            double bx = cx + i*_sectionWidth + _groupPadWidth + (j*2+1)*_barPadWidth + j*_barWidth;
            double by = seriesToLocal(i, val).y, bh = cy + ch - by;
            bars[j][i] = new Bar(dataPoint, bx, by, _barWidth, bh, color);
        }
    }
    
    return _bars = bars;
}

/**
 * Paints chart.
 */
protected void paintChart(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get selected point index (section index)
    DataPoint dataPoint = _chartView.getSelDataPoint();
    int selIndex = dataPoint!=null? dataPoint.getIndex() : -1;
    
    double cx = 0, cy = 0, cw = getWidth(), ch = getHeight();
    Bar bars[][] = getBars();
    
    // If reveal is not full (1) then clip
    if(getReveal()<1) {
        aPntr.save(); aPntr.clipRect(0,getHeight()*(1-getReveal()),getWidth(),getHeight()*getReveal()); }
        
    // Iterate over sections
    for(int i=0;i<_pointCount;i++) {
        
        // If selected section, draw background
        if(i==selIndex) {
            aPntr.setColor(Color.get("#4488FF09")); aPntr.fillRect(cx + i*_sectionWidth, cy, _sectionWidth, ch); }
        
        // Iterate over series and draw bars
        for(int j=0;j<_seriesCount;j++) { Bar bar = bars[j][i];
            aPntr.setColor(bar.color); aPntr.fillRect(bar.x, bar.y, bar.w, bar.h - .5);
        }
    }
    
    // If reveal not full, resture gstate
    if(getReveal()<1) aPntr.restore();
}

/**
 * Override to return point above bar.
 */
public Point dataPointInLocal(DataPoint aDP)
{
    // Get data point info
    int seriesIndex = aDP.getSeriesActiveIndex();
    int pointIndex = aDP.getIndex();
    
    // Get bar for data point and return top-center point
    Bar bars[][] = getBars();
    Bar bar = bars[seriesIndex][pointIndex];
    return new Point(Math.round(bar.x + bar.w/2), Math.round(bar.y));
}

/**
 * Returns the data point best associated with given x/y (null if none).
 */
protected DataPoint getDataPointAt(double aX, double aY)
{
    // Get bars array
    Bar bars[][] = getBars();
        
    // Iterate over points (sections) and series and if bar contains point, return data point
    for(int i=0;i<_pointCount;i++) { for(int j=0;j<_seriesCount;j++) { Bar bar = bars[j][i];
        if(bar.contains(aX,aY))
            return bar.point;
    }}
    
    // Return null since bar not found for point
    return null;
}

/**
 * A class to hold bar information.
 */
private class Bar {
    
    // Points
    DataPoint point;
    double x, y, w, h;
    Color color;
    
    /** Creates a bar. */
    public Bar(DataPoint aDP, double aX, double aY, double aW, double aH, Color aColor)
    {
        point = aDP; x = aX; y = aY; w = aW; h = aH; color = aColor;
    }
    
    public boolean contains(double aX, double aY)  { return Rect.contains(x, y, w, h, aX, aY); }
}

}