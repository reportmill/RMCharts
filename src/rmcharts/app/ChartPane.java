package rmcharts.app;
import snap.gfx.*;
import snap.view.*;
import snap.web.WebURL;

/**
 * A class to manage a ChartView.
 */
public class ChartPane extends ViewOwner {
    
    // The chartView
    ChartView     _chartView;

/**
 * Create UI.
 */
protected View createUI()
{
    // Toolbar
    ToggleButton btn0 = new ToggleButton("Line Chart"); btn0.setName("LineChartButton"); btn0.setGroup("BG0");
    btn0.setPosition(Pos.CENTER_LEFT); btn0.setPrefSize(70,22); btn0.setSelected(true);
    ToggleButton btn1 = new ToggleButton("Bar Chart"); btn1.setName("BarChartButton"); btn1.setGroup("BG0");
    btn1.setPosition(Pos.CENTER_RIGHT); btn1.setPrefSize(70,22);
    RowView toolBar = new RowView(); toolBar.setPadding(5,5,5,5); toolBar.setAlign(HPos.CENTER);
    toolBar.setChildren(btn0, btn1);
    
    // Create ChartView
    _chartView = new ChartView();
    _chartView.setEffect(new ShadowEffect());
    BoxView chartBox = new BoxView(_chartView, true, true); chartBox.setPadding(18,50,50,50);
    chartBox.setGrowHeight(true);

    // Create TextView
    TextView textView = new TextView(); textView.setMinHeight(300);
    textView.setDefaultStyle(textView.getDefaultStyle().copyFor(Font.Arial14));
    textView.setSource(WebURL.getURL(ChartPane.class, "Sample.json"));
    BoxView textBox = new BoxView(textView, true, true); textBox.setFill(new Color(.93)); textBox.setPadding(4,4,4,4);
    textBox.setGrowHeight(true); textBox.setPrefHeight(400);
    
    // Create TabView
    TabView tabView = new TabView(); tabView.setGrowHeight(true);
    tabView.addTab("JavaScript Embed", textBox);
    
    // Create ColView
    ColView col = new ColView(); col.setFillWidth(true); col.setGrowHeight(true); col.setFill(new Color(.93));
    col.setChildren(toolBar, chartBox, tabView);
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
}

}