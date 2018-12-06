package rmcharts.app;
import java.util.List;
import snap.gfx.Point;
import snap.util.SnapUtils;

/**
 * A class to represent a data point.
 */
public class DataPoint {
    
    // The series this point belongs to
    DataSeries   _series;
    
    // The data point name
    String       _name;
    
    // The data point x value (usually the index)
    Double       _x;
    
    // The data point y value
    double       _y;
    
    // The index
    int          _index;
    
/**
 * Returns the series.
 */
public DataSeries getSeries()  { return _series; }

/**
 * Returns the name.
 */
public String getName()  { return _name; }

/**
 * Returns the X value.
 */
public double getX()
{
    if(_x!=null) return _x;
    _x = (double)(_series._dset.getSeriesStart() + _index);
    return _x;
}

/**
 * Returns the Y value.
 */
public double getY()  { return _y; }

/**
 * Sets the Y value.
 */
public void setY(double aValue)
{
    if(aValue==getY()) return;
    _y = aValue;
    _series._dset.clearCache();
}

/**
 * Returns the index of this point in series.
 */
public int getIndex()  { return _index; }

/**
 * Return series name.
 */
public String getSeriesName()  { return _series.getName(); }

/**
 * Return index of data point series.
 */
public int getSeriesIndex()  { return _series.getIndex(); }

/**
 * Return index of data point series in dataset active series.
 */
public int getSeriesActiveIndex()  { return _series.getActiveIndex(); }

/**
 * Return data point key - either the name or the x value.
 */
public Object getKey()
{
    // If name, just return it
    if(_name!=null) return _name;
    
    // If categories, return that
    List <String> cats = _series._dset._chartView.getXAxis().getCategories();
    if(cats!=null && getIndex()<cats.size())
        return cats.get(getIndex());
    
    // Otherwise return x val (as int, if whole number)
    double kval = getX();
    return kval==(int)kval? (int)kval : kval;
}

/**
 * Return data point key as a string.
 */
public String getKeyString()
{
    Object key = getKey();
    return SnapUtils.stringValue(key);
}

/**
 * Return series value.
 */
public double getValue()  { return _y; }

/**
 * Sets series value.
 */
public void setValue(double aValue)  { setY(aValue); }

/**
 * Returns the DataPoint in chart area coords.
 */
public Point getPointInChartArea()
{
    ChartView cview = _series._dset._chartView;
    ChartArea carea = cview._chartArea;
    return carea.dataPointInLocal(this);
}

/**
 * Returns the DataPoint in chart view coords.
 */
public Point getPointInChartView()
{
    ChartView cview = _series._dset._chartView;
    ChartArea carea = cview._chartArea;
    Point pnt = carea.dataPointInLocal(this);
    return carea.localToParent(pnt.x, pnt.y, cview);
}

/** Standard equals implementation. */
public boolean equals(Object anObj)
{
    DataPoint other = anObj instanceof DataPoint? (DataPoint)anObj : null; if(other==null) return false;
    return other._series==_series && other._index==_index;
}

}