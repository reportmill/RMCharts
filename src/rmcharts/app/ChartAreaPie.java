package rmcharts.app;
import snap.gfx.*;

/**
 * A ChartArea subclass to display the contents of pie chart.
 */
public class ChartAreaPie extends ChartArea {

/**
 * Creates a ChartAreaPie.
 */
public ChartAreaPie()
{
    setPadding(25,10,10,10);
}

/**
 * Paints chart.
 */
protected void paintChart(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get series
    DataSeries series = getSeries(0);
    int pointCount = getValueCount();
    
    double cw = getWidth(), ch = getHeight();
    Insets ins = getInsetsAll();
    double diam = ch - ins.getHeight();
    double px = ins.left + Math.round((cw - ins.getWidth() - diam)/2);
    double py = ins.top + Math.round((ch - ins.getHeight() - diam)/2);
    
    double vals[] = new double[pointCount]; for(int i=0;i<pointCount;i++) vals[i] = series.getValue(i);
    double total = 0; for(int i=0;i<pointCount;i++) total += vals[i];
    double angles[] = new double[pointCount]; for(int i=0;i<pointCount;i++) angles[i] = Math.round(vals[i]/total*360);
    double start = -90;
    
    for(int i=0; i<angles.length; i++) { double angle = angles[i];
        Arc arc = new Arc(px - (i>0?10:0), py - (i>0?10:0), diam, diam, start, angle);
        aPntr.setColor(_chartView.getColor(i));
        aPntr.fill(arc); start += angle;
    }
}

/**
 * Returns the data point best associated with given x/y (null if none).
 */
protected DataPoint getDataPointAt(double aX, double aY)
{
    // Get series
    DataSeries series = getSeries(0);
    int pointCount = getValueCount();
    
    double cw = getWidth(), ch = getHeight();
    Insets ins = getInsetsAll();
    double diam = ch - ins.getHeight();
    double px = ins.left + Math.round((cw - ins.getWidth() - diam)/2);
    double py = ins.top + Math.round((ch - ins.getHeight() - diam)/2);
    
    double vals[] = new double[pointCount]; for(int i=0;i<pointCount;i++) vals[i] = series.getValue(i);
    double total = 0; for(int i=0;i<pointCount;i++) total += vals[i];
    double angles[] = new double[pointCount]; for(int i=0;i<pointCount;i++) angles[i] = Math.round(vals[i]/total*360);
    double start = -90;
    
    for(int i=0; i<angles.length; i++) { double angle = angles[i];
        Arc arc = new Arc(px - (i>0?10:0), py - (i>0?10:0), diam, diam, start, angle);
        if(arc.contains(aX, aY))
            return series.getPoint(i);
        start += angle;
    }
    
    // Return null since bar not found for point
    return null;
}

/**
 * Called when chart is activated.
 */
public void activate()
{
    ChartXAxis xaxis = _chartView.getXAxis(); xaxis.setVisible(false); xaxis.setManaged(false);
    ChartYAxis yaxis = _chartView.getYAxis(); yaxis.setVisible(false); yaxis.setManaged(false);
}

/**
 * Called when chart is deactivated.
 */
public void deactivate()
{
    ChartXAxis xaxis = _chartView.getXAxis(); xaxis.setVisible(true); xaxis.setManaged(true);
    ChartYAxis yaxis = _chartView.getYAxis(); yaxis.setVisible(true); yaxis.setManaged(true);
}

}