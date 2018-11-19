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
 * Returns the number values.
 */
public int getCount()  { return _points.size(); }

/**
 * Returns the data point at given index.
 */
public DataPoint getPoint(int anIndex)
{
    return anIndex<getCount()? _points.get(anIndex) : null;
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
    dpnt._index = getCount();
    dpnt._y = aValue;
    _points.add(dpnt);
}

/**
 * Returns the value at given index.
 */
public double getValue(int anIndex)
{
    DataPoint dp = getPoint(anIndex); return dp!=null? dp.getValue() : 0;
}

/**
 * Returns the index in dataset.
 */
public int getIndex()  { return _index; }

/**
 * Returns the index in dataset active series.
 */
public int getActiveIndex()  { return _dset.getSeriesActive().indexOf(this); }

/**
 * Returns whether series is disabled.
 */
public boolean isDisabled()  { return _disabled; }

/**
 * Sets whether series is disabled.
 */
public void setDisabled(boolean aValue)  { _disabled = aValue; }

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

}