package rmcharts.app;
import java.util.*;

/**
 * A class to represent a list of data points.
 */
public class DataSeries {
    
    // The dataset that owns the series
    DataSet           _dset;

    // The name
    String            _name;
    
    // The values
    List <DataPoint>  _points = new ArrayList();
    
    // The index in data set
    int               _index;
    
    // Whether series is disabled
    boolean           _disabled;
    
    // Cached array of values, ratios, total
    double            _vals[], _ratios[], _total;

/**
 * Returns the dataset.
 */
public DataSet getDataSet()  { return _dset; }

/**
 * Returns the name.
 */
public String getName()  { return _name; }

/**
 * Sets the name.
 */
public void setName(String aValue)  { _name = aValue; }

/**
 * Returns the data points list.
 */
public List <DataPoint> getPoints()  { return _points; }

/**
 * Returns the number of points.
 */
public int getPointCount()  { return _points.size(); }

/**
 * Returns the data point at given index.
 */
public DataPoint getPoint(int anIndex)
{
    return anIndex<getPointCount()? _points.get(anIndex) : null;
}

/**
 * Sets the values.
 */
public void setValues(double ... theVals)
{
    _points.clear();
    for(double v : theVals) addValue(null, v);
}

/**
 * Adds a value.
 */
public void addValue(String aName, double aValue)
{
    DataPoint dpnt = new DataPoint();
    dpnt._series = this;
    dpnt._name = aName;
    dpnt._index = getPointCount();
    dpnt._y = aValue;
    _points.add(dpnt);
    clearCache();
}

/**
 * Returns the value at given index.
 */
public double getValue(int anIndex)
{
    DataPoint dp = getPoint(anIndex); return dp!=null? dp.getValue() : 0;
}

/**
 * Returns the total of all values.
 */
public double getTotal()
{
    if(_vals==null) getValues();
    return _total;
}

/**
 * Returns an array of series values.
 */
public double[] getValues()
{
    if(_vals!=null) return _vals;
    int count = getPointCount(); _total = 0;
    double vals[] = new double[count]; for(int i=0;i<count;i++) { double v = getValue(i); vals[i] = v; _total += v; }
    return _vals = vals;
}

/**
 * Returns an array of series ratios.
 */
public double[] getRatios()
{
    if(_ratios!=null) return _ratios;
    double vals[] = getValues(), total = getTotal(); int count = vals.length;
    double ratios[] = new double[count]; for(int i=0;i<count;i++) ratios[i] = vals[i]/total;
    return _ratios = ratios;
}

/**
 * Returns the index in dataset.
 */
public int getIndex()  { return _index; }

/**
 * Returns the index in dataset active series.
 */
public int getActiveIndex()  { return _dset.getActiveSeries().indexOf(this); }

/**
 * Returns whether series is disabled.
 */
public boolean isDisabled()  { return _disabled; }

/**
 * Sets whether series is disabled.
 */
public void setDisabled(boolean aValue)
{
    if(aValue==isDisabled()) return;
    _disabled = aValue;
    _dset.clearCache();
    
    // if Pie chart, clear other 
    if((!aValue) && _dset._chartView.getChartArea() instanceof ChartAreaPie) {
        for(DataSeries s : _dset.getSeries())
            s.setDisabled(s!=this);
    }
}

/**
 * Returns whether series is enabled.
 */
public boolean isEnabled()  { return !_disabled; }

/**
 * Returns the minimum value in series.
 */
public double getMinValue()
{
    double minVal = Float.MAX_VALUE;
    for(DataPoint dp : _points) if(dp.getValue()<minVal) minVal = dp.getValue();
    return minVal;
}

/**
 * Returns the maximum value in series.
 */
public double getMaxValue()
{
    double maxVal = -Float.MAX_VALUE;
    for(DataPoint dp : _points) if(dp.getValue()>maxVal) maxVal = dp.getValue();
    return maxVal;
}

/**
 * Clears cached values.
 */
protected void clearCache()
{
    _vals = _ratios = null;
    if(_dset!=null) _dset.clearCache();
}

}