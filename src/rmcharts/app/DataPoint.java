package rmcharts.app;
import snap.gfx.Point;

/**
 * A class to represent a data point.
 */
public class DataPoint {
    
    // The ChartView
    ChartView    _chartView;

    // The series
    DataSeries   _series;
    
    // The index
    int          _index;

/**
 * Creates a DataPoint for ChartView, series and value index.
 */
public DataPoint(ChartView aCV, DataSeries aSeries, int anIndex)
{
    _chartView = aCV; _series = aSeries; _index = anIndex;
}

/**
 * Returns the series.
 */
public DataSeries getSeries()  { return _series; }

/**
 * Returns the value index.
 */
public int getValueIndex()  { return _index; }

/**
 * Return series name.
 */
public String getSeriesName()  { return _series!=null? _series.getName() : null; }

/**
 * Return series key.
 */
public double getSeriesKey()  { return _chartView.getSeriesStart() + _index; }

/**
 * Return series value.
 */
public double getSeriesValue()  { return _series!=null? _series.getValue(_index) : 0; }

/**
 * Returns the DataPoint in local coords.
 */
public Point getDataPointLocal()
{
    return _chartView.getChartArea().dataPointInLocal(_series, _index);
}

/** Standard equals implementation. */
public boolean equals(Object anObj)
{
    DataPoint other = anObj instanceof DataPoint? (DataPoint)anObj : null; if(other==null) return false;
    return other._series==_series && other._index==_index;
}

}