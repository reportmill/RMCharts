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
    Insets ins = getInsetsAll();
    double w = _content.getPrefWidth(), h = _content.getPrefHeight();
    Rect bnds = _content.localToParent(new Rect(0,0,w,h)).getBounds();
    return bnds.width + ins.getWidth();
}

/**
 * Calculates the preferred height.
 */
protected double getPrefHeightImpl(double aW)
{
    Insets ins = getInsetsAll();
    double w = _content.getPrefWidth(), h = _content.getPrefHeight();
    Rect bnds = _content.localToParent(new Rect(0,0,w,h)).getBounds();
    return bnds.height + ins.getHeight();
}

/**
 * Actual method to layout children.
 */
protected void layoutImpl()
{
    Insets ins = getInsetsAll();
    double pw = getWidth(), ph = getHeight();
    double cw = _content.getPrefWidth(), ch = _content.getPrefHeight();
    _content.setSize(cw,ch);
    _content.setXY(ins.left + (pw - ins.getWidth() - cw)/2, ins.top + (ph - ins.getHeight() - ch)/2);
}

}