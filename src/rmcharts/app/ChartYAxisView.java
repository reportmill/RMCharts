package rmcharts.app;
import snap.gfx.*;
import snap.view.*;

/**
 * A view to paint Chart Y Axis.
 */
public class ChartYAxisView extends View {
    
    // The ChartArea
    ChartArea      _chartArea;
    
    // Constants
    static Color   AXIS_LABELS_COLOR = Color.GRAY;

/**
 * Creates the ChartYAxisView.
 */
public ChartYAxisView()
{
    setPrefWidth(40);
    enableEvents(MousePress);
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
    Intervals intervals = _chartArea.getIntervals();
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

/**
 * Handle events.
 */
protected void processEvent(ViewEvent anEvent)
{
    // Handle MousePress
    if(anEvent.isMousePress())
        _chartArea._chartView.setShowPartialY(!_chartArea._chartView.isShowPartialY());
}

}