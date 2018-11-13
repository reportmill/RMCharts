package rmcharts.app;
import java.util.List;
import snap.gfx.*;
import snap.view.View;

/**
 * A view to paint Chart X Axis.
 */
public class ChartXAxisView extends View {

    // The ChartArea
    ChartArea         _chartArea;
    
    // The categories
    List <String>     _categories;
    
    // The length of the vertical tick lines drawn from the X axis down twards it's labels and title
    double            _tickLength = 10;

    // Constants
    static Color           AXIS_LABELS_COLOR = Color.GRAY;

/**
 * Creates the ChartXAxisView.
 */
public ChartXAxisView()
{
    setPrefHeight(18);
}

/**
 * Returns the categories.
 */
public List <String> getCategories()  { return _categories; }

/**
 * Sets the categories.
 */
public void setCategories(List <String> theStrings)
{
    _categories = theStrings;
}

/**
 * Returns the length of the vertical tick lines drawn from the X axis down twards it's labels and title.
 */
public double getTickLength()  { return _tickLength; }

/**
 * Sets the length of the vertical tick lines drawn from the X axis down twards it's labels and title.
 */
public void setTickLength(double aValue)  { _tickLength = aValue; }

/**
 * Returns the label string at given index.
 */
public String getLabel(int anIndex)
{
    // If categories exist, return the category string at index
    if(_categories!=null && anIndex<_categories.size())
        return _categories.get(anIndex);
        
    // Otherwise, return string for start value and index
    int val = _chartArea.getSeriesStart() + anIndex;
    return String.valueOf(val);
}

/**
 * Paints chart x axis.
 */
protected void paintFront(Painter aPntr)
{
    Insets ins = _chartArea.getInsetsAll();
    paintAxis(aPntr, ins.left, getWidth() - ins.getWidth(), getHeight());
}

/**
 * Paints chart x axis.
 */
protected void paintAxis(Painter aPntr, double aX, double aW, double aH)
{
    // If Bar chart, go there instead
    if(_chartArea instanceof ChartAreaBar) { paintAxisBar(aPntr, 0, getWidth(), aH); return; }
    
    // Set font, color
    aPntr.setFont(Font.Arial12); aPntr.setColor(AXIS_LABELS_COLOR);
    double fontHeight = Math.ceil(Font.Arial12.getAscent());
    
    // Get number of data points
    int dpc = _chartArea.getValueCount();
    double sw = aW/(dpc-1);
    
    // Draw axis
    for(int i=0;i<dpc;i++) {
        
        // Get line x
        double lx = aX + sw*i;
        
        // Draw labels
        String str = getLabel(i);
        Rect strBnds = aPntr.getStringBounds(str);
        double x = lx - Math.round(strBnds.getMidX());
        double y = fontHeight;
        aPntr.drawString(str, x, y);
    }
}

/**
 * Paints chart x axis.
 */
protected void paintAxisBar(Painter aPntr, double aX, double aW, double aH)
{
    // Set font, color
    aPntr.setFont(Font.Arial12); aPntr.setColor(AXIS_LABELS_COLOR);
    double fontHeight = Math.ceil(Font.Arial12.getAscent());
    
    // Get number of data points
    int sectionCount = _chartArea.getValueCount();
    double sectionW = aW/sectionCount;
    
    // Draw axis
    for(int i=0;i<sectionCount;i++) {
        
        // Get line x
        double lx = aX + sectionW*i + sectionW/2;
        
        // Draw labels
        String str = getLabel(i);
        Rect strBnds = aPntr.getStringBounds(str);
        double x = lx - strBnds.getMidX(); x = Math.round(x);
        double y = fontHeight;
        aPntr.drawString(str, x, y);
    }
}

}