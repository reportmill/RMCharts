package rmcharts.app;
import java.util.List;
import snap.gfx.*;

/**
 * A ChartArea subclass to display the contents of bar chart.
 */
public class ChartAreaBar extends ChartArea {
    
    // The number of series and values to chart
    int                _seriesCount, _valCount;
    
    // The width of a section (one for each series value)
    double             _sectionWidth;
    
    // The ratio of a section used to pad a group of bars
    double             _groupPad = .2;
    
    // The width of a group and the group pad in points
    double             _groupWidth, _groupPadWidth;
    
    // The ratio of a group used to pad a bar
    double             _barPad = .1;
    
    // The width of a bar and the bar pad in points
    double             _barWidth, _barPadWidth;
    
    // Whether to use colors for each series value instead of series
    boolean            _colorValues;

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
    clearSizes(); repaint();
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
    clearSizes(); repaint();
}

/**
 * Returns whether to use colors for each series value instead of series.
 */
public boolean isColorValues()  { return _colorValues; }

/**
 * Returns whether to use colors for each series value instead of series.
 */
public void setColorValues(boolean aValue)  { _colorValues = aValue; }

/**
 * Override to recalculate group/point padding and width.
 */
public void setWidth(double aValue)  { super.setWidth(aValue); clearSizes(); }

/**
 * Override to recalculate group/point padding and width.
 */
protected void calcSizes()
{
    // If recacl not needed, just return
    if(_seriesCount==getSeriesActive().size() && _valCount==getValueCount()) return;
    
    // Get number of values and section width
    _seriesCount = getSeriesActive().size();
    _valCount = getValueCount();
    _sectionWidth = getWidth()/_valCount;

    // Get group widths
    double groupWidthRatio = 1 - _groupPad*2;
    _groupWidth = groupWidthRatio>=0? groupWidthRatio*_sectionWidth : 1;
    _groupPadWidth = (_sectionWidth - _groupWidth)/2;
    
    // Get width of individual bar (bar count + bar spaces + bar&space at either end)
    double barWidthRatio = 1 - _barPad*2;
    _barWidth = barWidthRatio>=0? barWidthRatio*_groupWidth/_seriesCount : 1;
    _barPadWidth = barWidthRatio>=0? _barPad*_groupWidth/_seriesCount : 1;
}

/**
 * Override to recalculate group/point padding and width.
 */
protected void clearSizes()  { _seriesCount = -1; }

/**
 * Paints chart.
 */
protected void paintChart(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get chart area bounds and recalc sizes
    double cx = aX = 0, cw = aW = getWidth();
    calcSizes();
    
    // Get active series and selected section
    List <DataSeries> seriesList = getSeriesActive();
    int selSection = _chartView.getToolTipView().getValueIndex();
    boolean colorSeries = !isColorValues();
    
    // If reveal is not full (1) then clip
    if(getReveal()<1) {
        aPntr.save(); aPntr.clipRect(0,getHeight()*(1-getReveal()),getWidth(),getHeight()*getReveal()); }
        
    // Iterate over sections
    for(int i=0;i<_valCount;i++) {
        
        // If selected section, draw background
        if(i==selSection) {
            aPntr.setColor(Color.get("#4488FF09")); aPntr.fillRect(cx + i*_sectionWidth, aY, _sectionWidth, aH); }
        
        // Iterate over series
        for(int j=0;j<_seriesCount;j++) { DataSeries series = seriesList.get(j);
        
            int sind = getSeries().indexOf(series);
            double val = series.getValue(i);
            
            // Draw bar
            Color color = colorSeries? getColor(sind) : getColor(i); aPntr.setColor(color);
            double bx = cx + i*_sectionWidth + _groupPadWidth + (j*2+1)*_barPadWidth + j*_barWidth;
            double by = seriesToLocal(i, val).y, bh = aY + aH - by;
            aPntr.fillRect(bx, by, _barWidth, bh - .5);
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
    // Get chart area bounds and recalc sizes
    double cx = 0, ch = getHeight(); calcSizes();
    
    // Caclulate bar bounds for data point
    int seriesIndex = getSeriesActive().indexOf(aSeries);
    double val = aSeries.getValue(anIndex);
    double bx = anIndex*_sectionWidth + _groupPadWidth + (seriesIndex*2+1)*_barPadWidth + seriesIndex*_barWidth;
    double by = seriesToLocal(anIndex, val).y, bh = ch - by;
    return new Point(Math.round(bx + _barWidth/2), Math.round(by));
}

/**
 * Returns the data point best associated with given x/y (null if none).
 */
protected DataPoint getDataPointAt(double aX, double aY)
{
    // Get chart area bounds and recalc sizes
    double cx = 0, ch = getHeight(); calcSizes();
    
    // Get active series list and count
    List <DataSeries> seriesList = getSeriesActive();
        
    // Iterate over sections
    for(int i=0;i<_valCount;i++) {
        
        // Iterate over series and if bar contains point, return data point
        for(int j=0;j<_seriesCount;j++) { DataSeries series = seriesList.get(j);
            double val = series.getValue(i);
            double bx = cx + i*_sectionWidth + _groupPadWidth + (j*2+1)*_barPadWidth + j*_barWidth;
            double by = seriesToLocal(i, val).y, bh = aY + ch - by;
            if(Rect.contains(bx-1, by, _barWidth + 2, bh, aX, aY))
                return new DataPoint(_chartView, series, i);
        }
    }
    
    // Return null since bar not found for point
    return null;
}

}