package rmcharts.app;
import java.text.DecimalFormat;
import snap.gfx.*;

/**
 * A ChartArea subclass to display the contents of pie chart.
 */
public class ChartAreaPie extends ChartArea {
    
    // Whether legend was showing
    boolean      _showLegend;
    
    // The format
    DecimalFormat _fmt = new DecimalFormat("#.# %");

/**
 * Creates a ChartAreaPie.
 */
public ChartAreaPie()
{
    setPadding(25,10,20,10);
    setFont(Font.Arial12.getBold());
}

/**
 * Paints chart.
 */
protected void paintChart(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get series
    DataSeries series = getSeries(0);
    DataPoint selPoint = _chartView.getSelDataPoint();
    int selIndex = selPoint!=null? selPoint.getIndex() : -1;
    int pointCount = getPointCount();
    Font font = getFont(); aPntr.setFont(font);
    
    double cw = getWidth(), ch = getHeight();
    Insets ins = getInsetsAll();
    double diam = ch - ins.getHeight(), rad = diam/2;
    double px = ins.left + Math.round((cw - ins.getWidth() - diam)/2);
    double py = ins.top + Math.round((ch - ins.getHeight() - diam)/2);
    
    double vals[] = new double[pointCount]; for(int i=0;i<pointCount;i++) vals[i] = series.getValue(i);
    double total = 0; for(int i=0;i<pointCount;i++) total += vals[i];
    double reveal = getReveal(), max = 360*reveal;
    double angles[] = new double[pointCount]; for(int i=0;i<pointCount;i++) angles[i] = Math.round(vals[i]/total*max);
    double start = -90;
    
    // Iterate over angles and paint wedges
    for(int i=0; i<angles.length; i++) { double angle = angles[i];
    
        // Get offset point (adjusted if wedge selected)
        double px2 = px, py2 = py; if(i==selIndex) {
            double ang2 = Math.toRadians(start + angle/2);
            px2 = px + 10*Math.cos(ang2); py2 = py + 10*Math.sin(ang2);
        }
        
        // Get diameter (adjusted, along with offsets, for reveal)
        double diam2 = diam; if(reveal<1) { diam2 = diam2*reveal; px2 += rad*(1-reveal); py2 += rad*(1-reveal); }
        
        // Create arc and fill
        Arc arc = new Arc(px2, py2, diam2, diam2, start, angle);
        aPntr.setColor(_chartView.getColor(i));
        aPntr.fill(arc);
        
        // Paint label
        String name = series.getPoint(i).getName();
        if(reveal>=1 && name!=null && name.length()>0) {
            String label = name + ": " + _fmt.format(vals[i]/total);
            double ang2 = start + angle/2, ang2Rad = Math.toRadians(ang2);
            double px3 = px2 + rad + (rad+20)*Math.cos(ang2Rad);
            double py3 = py2 + rad + (rad+20)*Math.sin(ang2Rad) + font.getAscent();
            Rect bnds = font.getStringBounds(label);
            if(ang2>90) px3 -= bnds.width;
            px3 = Math.round(px3); py3 = Math.round(py3);
            aPntr.setColor(Color.BLACK); aPntr.drawString(label, px3, py3);
        }
        
        // Increment start
        start += angle;
    }
}

/**
 * Returns the data point best associated with given x/y (null if none).
 */
protected DataPoint getDataPointAt(double aX, double aY)
{
    // Get series
    DataSeries series = getSeries(0);
    int pointCount = getPointCount();
    
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
    _showLegend = _chartView.isShowLegend(); _chartView.setShowLegend(false);
}

/**
 * Called when chart is deactivated.
 */
public void deactivate()
{
    ChartXAxis xaxis = _chartView.getXAxis(); xaxis.setVisible(true); xaxis.setManaged(true);
    ChartYAxis yaxis = _chartView.getYAxis(); yaxis.setVisible(true); yaxis.setManaged(true);
    _chartView.setShowLegend(_showLegend);
}

}