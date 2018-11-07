package rmcharts.app;
import snap.gfx.*;
import snap.view.*;

/**
 * A view to paint Chart Y Axis.
 */
public class ChartYAxisView extends ParentView {
    
    // The ChartArea
    ChartArea      _chartArea;
    
    // The Title view
    StringView     _titleView;
    
    // The Title view wrapper (to allow rotation)
    WrapView       _titleViewBox;
    
    // Sizes
    double         _prefWidth = 40;
    
    // Constants
    static Color   AXIS_LABELS_COLOR = Color.GRAY;

/**
 * Creates the ChartYAxisView.
 */
public ChartYAxisView()
{
    enableEvents(MousePress);
    
    // Create configure YAxisTitleView
    _titleView = new StringView(); _titleView.setTextFill(Color.GRAY); _titleView.setRotate(-90);
    _titleView.setFont(Font.Arial12.getBold().deriveFont(13));
    _titleViewBox = new WrapView(_titleView); //_titleViewBox.setPrefWidth(22);
    addChild(_titleViewBox);
}

/**
 * Returns the YAxis title.
 */
public String getTitle()  { return _titleView.getText(); }

/**
 * Sets the YAxis title.
 */
public void setTitle(String aStr)
{
    _titleView.setText(aStr);
    double titlePad = aStr!=null && aStr.length()>0? 12 : 0;
    _titleViewBox.setPadding(0, titlePad, 0, 0);
}

/**
 * Paints chart y axis.
 */
protected void paintFront(Painter aPntr)
{
    Insets ins = _chartArea.getInsetsAll();
    double w = getWidth(), tw = _titleViewBox.getWidth(), aw = w - tw;
    paintAxis(aPntr, tw, ins.top, aw, getHeight() - ins.getHeight());
}

/**
 * Paints chart y axis.
 */
protected void paintAxis(Painter aPntr, double aX, double aY, double aW, double aH)
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
        double x = aX + aW - 5 - strBnds.width;
        double y = ly + fontDesc; y = Math.round(y);
        aPntr.drawString(str, x, y);
    }
}

/**
 * Calculates the preferred width.
 */
protected double getPrefWidthImpl(double aH)
{
    return _titleViewBox.getPrefWidth(aH) + getMaxLabelWidth() + 5;
}

/**
 * Actual method to layout children.
 */
protected void layoutImpl()
{
    double w = getWidth(), h = getHeight();
    _titleViewBox.setBounds(0,0,_titleViewBox.getPrefWidth(), h);
}

/**
 * Formats the label for a given line value.
 */
protected String getLabel(double aLineVal, double aDelta)
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
 * Returns the max label width.
 */
protected double getMaxLabelWidth()
{
    // Get intervals
    Intervals intervals = _chartArea.getIntervals();
    int lineCount = intervals.getCount(), sectionCount = lineCount - 1;
    double intervalDelta = intervals.getDelta(), intervalMax = intervals.getMax();
    
    // Get longest text
    String maxText = "";
    for(int i=0;i<lineCount;i++) {
        double lineVal = (intervalMax-i*intervalDelta);
        String str =  getLabel(lineVal, intervalDelta);
        if(str.length()>maxText.length())
            maxText = str;
    }
    
    return Font.Arial12.getStringAdvance(maxText);
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