package rmcharts.app;
import snap.gfx.*;
import snap.view.*;

/**
 * A custom class.
 */
public class WrapView extends ParentView {
    
    // The content
    View      _content;

/**
 * Create WrapView for given view.
 */
public WrapView(View aView)
{
    _content = aView;
    addChild(aView);
}
    
/**
 * Calculates the preferred width.
 */
protected double getPrefWidthImpl(double aH)
{
    double w = _content.getPrefWidth(), h = _content.getPrefHeight();
    Rect bnds = _content.localToParent(new Rect(0,0,w,h)).getBounds();
    return bnds.width;
}

/**
 * Calculates the preferred height.
 */
protected double getPrefHeightImpl(double aW)
{
    double w = _content.getPrefWidth(), h = _content.getPrefHeight();
    Rect bnds = _content.localToParent(new Rect(0,0,w,h)).getBounds();
    return bnds.height;
}

/**
 * Actual method to layout children.
 */
protected void layoutImpl()
{
    double w = _content.getPrefWidth(), h = _content.getPrefHeight();
    _content.setSize(w,h);
    _content.setXY(getWidth()/2 - w/2, getHeight()/2 - h/2);
}

}