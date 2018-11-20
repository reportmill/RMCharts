package rmcharts.app;
import java.text.DecimalFormat;
import snap.gfx.*;
import snap.view.ViewEvent;

/**
 * A ChartArea subclass to display the contents of pie chart.
 */
public class ChartAreaPie extends ChartArea {
    
    // The cached wedges
    Wedge        _wedges[];
    
    // The pie center point
    double       _pieX, _pieY;
    
    // The pie radius and diameter
    double       _pieR, _pieD;
    
    // Whether legend was showing
    boolean      _showLegend;
    
    // The format
    DecimalFormat _fmt = new DecimalFormat("#.# %");
    
    // Constants
    static double LABEL_MARGIN = 30;
    static double LABEL_PAD = 3;
    static double PAD_TOP = 30, PAD_BOTTOM = 20, PAD_BOTTOM_MAX = 40;

/**
 * Creates a ChartAreaPie.
 */
public ChartAreaPie()
{
    setPadding(PAD_TOP, 10, PAD_BOTTOM, 10);
    setFont(Font.Arial12.getBold());
}

/**
 * Returns the pie wedges.
 */
protected Wedge[] getWedges()
{
    // If wedges cached, just return
    if(_wedges!=null && _wedges.length==getPointCount()) return _wedges;
    
    // Get series and point count
    DataSeries series = getSeries(0);
    int pointCount = getPointCount();
    
    // Get values and angles
    double vals[] = new double[pointCount]; for(int i=0;i<pointCount;i++) vals[i] = series.getValue(i);
    double total = 0; for(int i=0;i<pointCount;i++) total += vals[i];
    double angles[] = new double[pointCount]; for(int i=0;i<pointCount;i++) angles[i] = Math.round(vals[i]/total*360);
    
    // Fix padding to accommodate bottom label, if needed
    fixPaddingForBottomLabelIfNeeded(angles);

    // Get chart size and insets and calculate pie radius, diameter and center x/y
    double cw = getWidth(), ch = getHeight(); Insets ins = getInsetsAll();
    _pieD = ch - ins.getHeight(); _pieR = _pieD/2;
    _pieX = ins.left + Math.round((cw - ins.getWidth() - _pieD)/2);
    _pieY = ins.top + Math.round((ch - ins.getHeight() - _pieD)/2);
        
    // Iterate over angles and create/configure wedges
    Wedge wedges[] = new Wedge[pointCount]; double start = 0;
    for(int i=0; i<angles.length; i++) { double angle = angles[i];
        Wedge wedge = wedges[i] = new Wedge(); wedge._start = start; wedge._angle = angle;
        String name = series.getPoint(i).getName();
        if(name!=null && name.length()>0)
            wedge._text = name + ": " + _fmt.format(vals[i]/total);
        start += angle;
    }
    
    // Set label points so that they don't obscure each other
    setLabelPoints(wedges);
    
    // Return wedges
    return _wedges = wedges;
}

/**
 * Sets label points.
 */
protected void setLabelPoints(Wedge wedges[])
{
    // Set first and last wedge points
    if(wedges.length==0) return;
    wedges[0].getLabelPoint(); wedges[wedges.length-1].getLabelPoint();
    
    // Set label points for pie right side
    for(int i=1; i<wedges.length; i++) { Wedge wedge = wedges[i], wedge2 = wedges[i-1];
        if(wedge.getAngleMid()>90) break;
        double angle = Math.max(wedge.getAngleMid(), wedge2._textAngle+2);
        wedge.getLabelPoint(angle);
        while(wedge.labelIntersects(wedge2))
            wedge.getLabelPoint(angle+=1);
    }
    
    // Set label points for pie left side
    for(int i=wedges.length-2; i>=0; i--) { Wedge wedge = wedges[i], wedge2 = wedges[i+1];
        if(wedge.getAngleMid()<90) break;
        double angle = Math.min(wedge.getAngleMid(), wedge2._textAngle-2);
        wedge.getLabelPoint(angle);
        while(wedge.labelIntersects(wedge2))
            wedge.getLabelPoint(angle-=1);
    }
}

/** Changes padding to have an extra 20 points on bottom if label needed there. */
void fixPaddingForBottomLabelIfNeeded(double angles[])
{
    boolean hasBottomWedge = false; Insets ins = getPadding();
    double start = -90;
    for(double a : angles) { double ang = start + a/2;
        if(ang>60 && ang<120) { hasBottomWedge = true; break; } start += a; }
    if(hasBottomWedge && ins.bottom!=PAD_BOTTOM_MAX)
        setPadding(PAD_TOP, 10, PAD_BOTTOM_MAX, 10);
    else if(!hasBottomWedge && ins.bottom!=PAD_BOTTOM)
        setPadding(PAD_TOP, 10, PAD_BOTTOM, 10);
}

/**
 * Paints chart.
 */
protected void paintChart(Painter aPntr, double aX, double aY, double aW, double aH)
{
    // Get wedges and other paint info
    Wedge wedges[] = getWedges();
    DataPoint selPoint = _chartView.getSelDataPoint();
    int selIndex = selPoint!=null? selPoint.getIndex() : -1;
    double reveal = getReveal();
    
    // Set font
    aPntr.setFont(getFont());
    
    // Iterate over wedges and paint wedge
    for(int i=0; i<wedges.length; i++) { Wedge wedge = wedges[i];
        Arc arc = wedge.getArc(reveal, i==selIndex);
        aPntr.setColor(_chartView.getColor(i)); aPntr.fill(arc);
        aPntr.setColor(Color.WHITE); aPntr.setStroke(Stroke.Stroke1); aPntr.draw(arc);
    }
    
    // Iterate over wedges and paint wedge label
    if(reveal>=1)
    for(int i=0; i<wedges.length; i++) { Wedge wedge = wedges[i];
        String text = wedge._text;
        if(text!=null && text.length()>0) {
            Point pnt = wedge.getLabelPoint();
            aPntr.setColor(Color.BLACK); aPntr.drawString(text, pnt.x, pnt.y);
        }
    }
}

/**
 * Returns the data point best associated with given x/y (null if none).
 */
protected DataPoint getDataPointAt(double aX, double aY)
{
    // Iterate over wedges and return point for wedge that contains given x/y
    Wedge wedges[] = getWedges();
    for(int i=0; i<wedges.length; i++) { Wedge wedge = wedges[i];
        Arc arc = wedge.getArc();
        if(arc.contains(aX, aY))
            return getSeries(0).getPoint(i);
    }
    
    // Return null since no wedge contains point
    return null;
}

/**
 * Override to return current mouse point.
 */
public Point dataPointInLocal(DataPoint aDP) { return _pnt; }
private Point _pnt;

/**
 * Handle events.
 */
protected void processEvent(ViewEvent anEvent)
{
    // Handle MouseMove
    if(anEvent.isMouseMove() || anEvent.isMouseClick()) {
        _chartView.getToolTipView().setXYInChartArea(_pnt = anEvent.getPoint().clone()); }
    super.processEvent(anEvent);
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

/**
 * Clears the wedges cache.
 */
protected void clearCache()  { _wedges = null; }

/**
 * Override to clear wedge cache.
 */
public void setWidth(double aValue)  { super.setWidth(aValue); clearCache(); }

/**
 * Override to clear wedge cache.
 */
public void setHeight(double aValue)  { super.setHeight(aValue); clearCache(); }

/**
 * A class to hold cached wedge data.
 */
private class Wedge {
    
    // The start and sweep angles
    double _start, _angle, _textAngle;
    
    // Label text
    String _text;
    
    // Cached Arc and label point
    Arc    _arc; Point  _textPoint; double _tw, _th;
    
    /** Returns the basic arc. */
    public Arc getArc()
    {
        return _arc!=null? _arc : (_arc = new Arc(_pieX, _pieY, _pieD, _pieD, getAngleStart(), _angle));
    }
    
    /** Returns the arc with given reveal or selection status. */
    public Arc getArc(double aReveal, boolean isSel)
    {
        // If no reveal or selection, return normal arc
        if(aReveal>=1 && !isSel) return getArc();
        
        // Get arc start/sweep angles, x/y points and diameter
        double start = -90 + _start*aReveal, angle = _angle*aReveal;
        double px = _pieX, py = _pieY, diam = _pieD;
        
        // If selected, move x/y by 10 points from center of wedge
        if(isSel) {
            double ang2 = Math.toRadians(start + angle/2);
            px += 10*Math.cos(ang2); py += 10*Math.sin(ang2);
        }
        
        // If reveal, modify diameter and move to new center
        if(aReveal<1) {
            diam *= aReveal; px += _pieR*(1-aReveal); py += _pieR*(1-aReveal); }
        
        // Create arc and return
        return new Arc(px, py, diam, diam, start, angle);
    }
    
    /** Returns the mid angle. */
    public double getAngleStart()  { return -90 + _start; }
    public double getAngleMid()  { return getAngleStart() + _angle/2; }
    
    /** Returns the label point. */
    public Point getLabelPoint()
    {
        if(_textPoint!=null) return _textPoint;
        return _textPoint = getLabelPoint(getAngleMid());
    }
    
    /** Returns the label point. */
    public Point getLabelPoint(double anAngle)
    {
        _textAngle = anAngle;
        double angRad = Math.toRadians(anAngle); Font font = getFont();
        double px = _pieX + _pieR + (_pieR+LABEL_MARGIN)*Math.cos(angRad);
        double py = _pieY + _pieR + (_pieR+LABEL_MARGIN)*Math.sin(angRad) + font.getAscent();
        Rect bnds = _text!=null? font.getStringBounds(_text) : new Rect(); _tw = bnds.width; _th = bnds.height;
        if(anAngle>90) px -= bnds.width;
        px = Math.round(px); py = Math.round(py);
        return _textPoint = new Point(px, py);
    }
    
    public boolean labelIntersects(Wedge aWedge)
    {
        Font font = getFont();
        Rect bnds0 = new Rect(_textPoint.x, _textPoint.y - font.getAscent(), _tw, _th);
        Rect bnds1 = new Rect(aWedge._textPoint.x, aWedge._textPoint.y - font.getAscent(), aWedge._tw, aWedge._th);
        bnds0.inset(-LABEL_PAD); bnds1.inset(-LABEL_PAD);
        return bnds0.intersects(bnds1);
    }
}

}