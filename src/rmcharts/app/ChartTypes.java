package rmcharts.app;

/**
 * A class to hold the different types of charts.
 */
public class ChartTypes {
    
    // The ChartView
    ChartView             _chartView;

    // A Column Chart
    ChartAreaBar          _colChart;
    
    // A LineChart
    ChartAreaLine         _lineChart;

/**
 * Creates the ChartTypes.
 */
public ChartTypes(ChartView aCV)  { _chartView = aCV; }

/**
 * Returns the type for given string.
 */
public ChartArea getChart(String aType)
{
    switch(aType) {
        case ChartView.LINE_TYPE: return getLineChart();
        case ChartView.BAR_TYPE: return getColumnChart();
        default: return null;
    }
}

/**
 * Returns the column chart.
 */
public ChartAreaBar getColumnChart()
{
    if(_colChart!=null) return _colChart;
    _colChart = new ChartAreaBar(); _colChart._chartView = _chartView; return _colChart;
}

/**
 * Returns the line chart.
 */
public ChartAreaLine getLineChart()
{
    if(_lineChart!=null) return _lineChart;
    _lineChart = new ChartAreaLine(); _lineChart._chartView = _chartView; return _lineChart;
}

}