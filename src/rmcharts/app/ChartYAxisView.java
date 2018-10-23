package rmcharts.app;
import snap.gfx.*;
import snap.util.MathUtils;
import snap.view.*;

/**
 * A view to paint Chart Y Axis.
 */
public class ChartYAxisView extends View {
    
    // The ChartArea
    ChartArea      _chartArea;
    
    // The intervals
    Intervals      _intervals = new Intervals(0, 4, 100);
    
    // Constants
    static Color   AXIS_LABELS_COLOR = Color.GRAY;

/**
 * Creates the ChartYAxisView.
 */
public ChartYAxisView()
{
    setPrefWidth(40);
}

/**
 * Returns the intervals.
 */
public Intervals getIntervals()
{
    // If intervals have been cached for current max value and height, return them
    double minVal = _chartArea.getSeriesActiveMinValue();
    double maxVal = _chartArea.getSeriesActiveMaxValue();
    double height = _chartArea.getHeight() - _chartArea.getInsetsAll().getHeight();
    double seedMax = _intervals.getSeedValueMax(), seedMin = _intervals.getSeedValueMin();
    double seedHeight = _intervals.getSeedHeight();
    if(_intervals!=null && MathUtils.equals(seedMax, maxVal) && MathUtils.equals(seedMin, minVal) &&
        MathUtils.equals(seedHeight, height))
        return _intervals;
    
    // Create new intervals and return
    _intervals = new Intervals(minVal, maxVal, height);
    return _intervals;
}

/**
 * Paints chart y axis.
 */
protected void paintFront(Painter aPntr)
{
    Insets ins = _chartArea.getInsetsAll();
    paintAxis(aPntr, ins.top, getWidth(), getHeight() - ins.getHeight());
}

/**
 * Paints chart y axis.
 */
protected void paintAxis(Painter aPntr, double aY, double aW, double aH)
{
    // Set font, color
    aPntr.setFont(Font.Arial12); aPntr.setColor(AXIS_LABELS_COLOR);
    double fontDesc = Font.Arial12.getDescent();
    
    // Get intervals
    Intervals intervals = getIntervals();
    int lineCount = intervals.getCount(), sectionCount = lineCount - 1;
    double intervalDelta = intervals.getDelta(), intervalMax = intervals.getMax();
    
    // Draw axis
    for(int i=0;i<lineCount;i++) {
        
        // Get line y
        double ly = aY + aH/sectionCount*i;
        
        // Draw labels
        double lineVal = (intervalMax-i*intervalDelta);
        String str =  getLabel(lineVal, intervalDelta);
        Rect strBnds = aPntr.getStringBounds(str);
        double x = aW - 5 - strBnds.width;
        double y = ly + fontDesc; y = Math.round(y);
        aPntr.drawString(str, x, y);
    }
}

/**
 * Formats the label for a given line value.
 */
public String getLabel(double aLineVal, double aDelta)
{
    // Handle case where delta is in the billions
    if(aDelta>=1000000000 && aDelta/1000000000==((int)aDelta)/1000000000) {
        int val = (int)Math.round(aLineVal/1000000000);
        return val + "b";
    }
    
    // Handle case where delta is in the millions
    if(aDelta>=1000000 && aDelta/1000000==((int)aDelta)/1000000) {
        int val = (int)Math.round(aLineVal/1000000);
        return val + "m";
    }
    
    // Handle case where delta is in the thousands
    if(aDelta>=1000 && aDelta/1000==((int)aDelta)/1000) {
        int val = (int)Math.round(aLineVal/1000);
        return val + "k";
    }
    
    // Handle  case where delta is integer
    if(aDelta==(int)aDelta)
        return String.valueOf((int)aLineVal);
        
    return String.valueOf(aLineVal);
}

}