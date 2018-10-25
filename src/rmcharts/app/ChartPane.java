package rmcharts.app;
import snap.gfx.*;
import snap.util.SnapUtils;
import snap.view.*;
import snap.web.WebURL;

/**
 * A class to manage a ChartView.
 */
public class ChartPane extends ViewOwner {
    
    // Whether to show full app
    boolean       _showFull;
    
    // The chartView
    ChartView     _chartView;
    
    // The TabView
    TabView       _tabView;

/**
 * Creates a ChartPane.
 */
public ChartPane()
{
    
}

/**
 * Returns whether to show app stuff.
 */
public boolean isShowFull()  { return _showFull; }

/**
 * Sets whether to show full app stuff.
 */
public void setShowFull(boolean aValue)
{
    // If already set, just return
    if(aValue==_showFull) return;
    _showFull = aValue;
    
    // Enable ShowFull
    if(aValue) {
        _tabView.setVisible(true); _tabView.setManaged(true); _tabView.setPickable(true);
        if(!SnapUtils.isTeaVM) {
            Size psize = getWindow().getPrefSize();
            Rect screenRect = ViewEnv.getEnv().getScreenBoundsInset();
            Rect maxRect = screenRect.getRectCenteredInside(psize.width, psize.height);
            getWindow().setMaximizedBounds(maxRect);
        }
        getWindow().setMaximized(true);
    }
    
    // Disable ShowFull
    else {
        _tabView.setVisible(false); _tabView.setManaged(false); _tabView.setPickable(false);
        getWindow().setMaximized(false);
    }
}

/**
 * Create UI.
 */
protected View createUI()
{
    // Toolbar
    Button btn2 = new Button("Max"); btn2.setName("MaximizeButton"); btn2.setLeanX(HPos.RIGHT);
    RowView toolBar = new RowView(); toolBar.setPadding(5,5,5,5); toolBar.setAlign(HPos.CENTER);
    toolBar.setChildren(btn2);
    
    // Create ChartView
    _chartView = new ChartView();
    _chartView.setEffect(new ShadowEffect());
    BoxView chartBox = new BoxView(_chartView, true, true); chartBox.setPadding(18,50,50,50);
    chartBox.setGrowHeight(true);
    
    // Create PropsView
    ToggleButton btn0 = new ToggleButton("Line Chart"); btn0.setName("LineChartButton"); btn0.setGroup("BG0");
    btn0.setPosition(Pos.CENTER_LEFT); btn0.setPrefSize(70,22); btn0.setSelected(true);
    ToggleButton btn1 = new ToggleButton("Bar Chart"); btn1.setName("BarChartButton"); btn1.setGroup("BG0");
    btn1.setPosition(Pos.CENTER_RIGHT); btn1.setPrefSize(70,22);
    RowView propsView = new RowView(); propsView.setPadding(5,5,5,5); propsView.setAlign(Pos.TOP_CENTER);
    propsView.setChildren(btn0, btn1);

    // Create TextView
    TextView textView = new TextView(); //textView.setMinHeight(300);
    textView.setDefaultStyle(textView.getDefaultStyle().copyFor(Font.Arial14));
    textView.setSource(WebURL.getURL(ChartPane.class, "Sample.json"));
    BoxView textBox = new BoxView(textView, true, true); textBox.setFill(new Color(.93)); textBox.setPadding(4,4,4,4);
    textBox.setGrowHeight(true); textBox.setPrefHeight(400);
    
    // Create TabView
    _tabView = new TabView(); _tabView.setPrefHeight(340); _tabView.setGrowHeight(true);
    _tabView.addTab("Chart Props", propsView);
    _tabView.addTab("JavaScript Embed", textBox);
    _tabView.setVisible(false); _tabView.setManaged(false); _tabView.setPickable(false);
    
    // Create ColView
    ColView col = new ColView(); col.setFillWidth(true); col.setGrowHeight(true); col.setFill(new Color(.93));
    col.setChildren(toolBar, chartBox, _tabView);
    return col;
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle LineChartButton, BarChartButton
    if(anEvent.equals("LineChartButton")) _chartView.setType(ChartView.LINE_TYPE);
    if(anEvent.equals("BarChartButton")) _chartView.setType(ChartView.BAR_TYPE);
    
    // Handle MaximizeButton
    if(anEvent.equals("MaximizeButton"))
        setShowFull(!isShowFull());
}

}