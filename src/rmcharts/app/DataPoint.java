package rmcharts.app;
import snap.gfx.Point;

/**
 * A class to represent a data point.
 */
public class DataPoint {
    
    // The series this point belongs to
    DataSeries   _series;
    
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
 * Returns the index of this point in series.
 */
public int getIndex()  { return _index; }

/**
 * Return series name.
 */
public String getSeriesName()  { return _series!=null? _series.getName() : null; }

/**
 * Return series key.
 */
public double getSeriesKey()
{
    DataSet dset = _series._dset;
    return dset.getSeriesStart() + _index;
}

/**
 * Return series value.
 */
public double getValue()  { return _y; }

/**
 * Returns the DataPoint in local coords.
 */
public Point getDataPointLocal()
{
    ChartView cview = _series._dset._chartView;
    ChartArea carea = cview._chartArea;
    return carea.dataPointInLocal(_series, _index);
}

/** Standard equals implementation. */
public boolean equals(Object anObj)
{
    DataPoint other = anObj instanceof DataPoint? (DataPoint)anObj : null; if(other==null) return false;
    return other._series==_series && other._index==_index;
}

}