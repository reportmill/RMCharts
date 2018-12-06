package rmcharts.app;
import java.util.*;
import snap.util.MathUtils;

/**
 * A class to manage a list of DataSeries.
 */
public class DataSet {
    
    // The ChartView
    ChartView          _chartView;

    // The list of series
    List <DataSeries>  _series = new ArrayList();
    
    // The series start
    int                _seriesStart = 0;

    // The intervals
    Intervals          _intervals = new Intervals(0, 4, 100);
    
    // Hold a subset of enabled series in this dataset
    DataSet            _active;
    
    // The cached min/max values
    double             _minVal = Float.MAX_VALUE, _maxVal = -Float.MAX_VALUE;
    
/**
 * Creates a DataSet for given ChartView.
 */
public DataSet(ChartView aCV)  { _chartView = aCV; }

/**
 * Returns the series.
 */
public List <DataSeries> getSeries()  { return _series; }

/**
 * Returns whether series is empty.
 */
public boolean isEmpty()  { return _series.isEmpty(); }

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
    aSeries._dset = this;
    clearCache();
}

/**
 * Clears the series.
 */
public void clear()  { _series.clear(); }

/**
 * Returns the minimum value for active series.
 */
public double getMinValue()
{
    if(_minVal<Float.MAX_VALUE) return _minVal;
    double minVal = Float.MAX_VALUE;
    for(DataSeries s : getSeries()) { double mval = s.getMinValue(); if(mval<minVal) minVal = mval; }
    return _minVal = minVal;
}

/**
 * Returns the maximum value for active series.
 */
public double getMaxValue()
{
    if(_maxVal>-Float.MAX_VALUE) return _maxVal;
    double maxVal = -Float.MAX_VALUE;
    for(DataSeries s : getSeries()) { double mval = s.getMaxValue(); if(mval>maxVal) maxVal = mval; }
    return _maxVal = maxVal;
}

/**
 * Adds a new series for given name and values.
 */
public void addSeriesForNameAndValues(String aName, double ... theVals)
{
    DataSeries series = new DataSeries(); series.setName(aName);
    addSeries(series);
    series.setValues(theVals);
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
    _chartView.repaint();
}

/**
 * Returns the number of points in each series.
 */
public int getPointCount()  { return _series.get(0).getPointCount(); }

/**
 * Returns the intervals.
 */
public Intervals getIntervals()
{
    // Get chart min value, max value and height
    double minVal = getMinValue();
    double maxVal = getMaxValue();
    double height = _chartView._chartArea.getHeight() - _chartView._chartArea.getInsetsAll().getHeight();
    if(!_chartView.isShowPartialY() && minVal*maxVal>0) {
        if(minVal>0) minVal = 0; else maxVal = 0; }
    
    // If intervals are cached for current min, max and height, return them
    double seedMax = _intervals.getSeedValueMax(), seedMin = _intervals.getSeedValueMin();
    double seedHeight = _intervals.getSeedHeight();
    if(MathUtils.equals(seedMax, maxVal) && MathUtils.equals(seedMin, minVal) && MathUtils.equals(seedHeight, height))
        return _intervals;
    
    // Create new intervals and return
    _intervals = new Intervals(minVal, maxVal, height);
    return _intervals;
}

/**
 * Returns the active dataset.
 */
public DataSet getActiveSet()
{
    if(_active!=null) return _active;
    
    // If all series are enabled, return this dataset
    int ac = 0; for(DataSeries s : _series) if(s.isEnabled()) ac++;
    if(ac==getSeriesCount()) return _active = this;
    
    // Create new dataset and initialize
    DataSet active = new DataSet(_chartView);
    active._series = new ArrayList(ac); for(DataSeries s : _series) if(s.isEnabled()) active._series.add(s);
    active._seriesStart = _seriesStart;
    return _active = active;
}

/**
 * Returns the active series.
 */
public List <DataSeries> getActiveSeries()  { return getActiveSet().getSeries(); }

/**
 * Returns the intervals.
 */
public Intervals getActiveIntervals()  { return getActiveSet().getIntervals(); }

/**
 * Clears cached values.
 */
protected void clearCache()
{
    _active = null; _minVal = Float.MAX_VALUE; _maxVal = -Float.MAX_VALUE;
    _chartView.getChartArea().clearCache();
    _chartView.repaint();
}

}