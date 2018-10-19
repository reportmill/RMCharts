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
    static Color           AXIS_LABELS_COLOR = Color.GRAY;

/**
 * Creates the ChartYAxisView.
 */
public ChartYAxisView()
{
    setPrefWidth(40);
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
    
    // Draw axis
    for(int i=0;i<5;i++) {
        
        // Get line y
        double ly = aY + aH/4*i;
        
        // Draw labels
        String str = (200-i*50) + "k";
        Rect strBnds = aPntr.getStringBounds(str);
        double x = aW - 5 - strBnds.width;
        double y = ly + fontDesc;
        aPntr.drawString(str, x, y);
    }
}

}