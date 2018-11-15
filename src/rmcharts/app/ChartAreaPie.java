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
    double cw = getWidth(), ch = getHeight();
    Insets ins = getInsetsAll();
    double diam = ch - ins.getHeight();
    double px = ins.left + Math.round((cw - ins.getWidth() - diam)/2);
    double py = ins.top + Math.round((ch - ins.getHeight() - diam)/2);
    
    double angles[] = { 225, 135 }, start = -90;
    
    for(int i=0; i<angles.length; i++) { double angle = angles[i];
        Arc arc = new Arc(px - (i>0?10:0), py - (i>0?10:0), diam, diam, start, angle);
        aPntr.setColor(_chartView.getColor(i));
        aPntr.fill(arc); start += angle;
    }
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