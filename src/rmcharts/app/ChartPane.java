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
    
    // The ChartBox
    ChartBox      _chartBox;
    
    // The TabView
    TabView       _tabView;
    
    // The Options button
    Button        _optionButton;

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
        _chartBox.setPadding(50,50,50,50); _chartView.setEffect(new ShadowEffect());
        getWindow().setMaximized(true);
    }
    
    // Disable ShowFull
    else {
        _tabView.setVisible(false); _tabView.setManaged(false); _tabView.setPickable(false);
        _chartBox.setPadding(0,0,0,0); _chartView.setEffect(null);
        getWindow().setMaximized(false);
    }
    
    _optionButton.setText(aValue? "Min" : "Max");
}

/**
 * Create UI.
 */
protected View createUI()
{
    // Create OptionButton
    _optionButton = new Button("Max"); _optionButton.setName("OptionButton");
    _optionButton.setManaged(false);
    
    // Create ChartView
    _chartView = new ChartView();
    _chartBox = new ChartBox();
    _chartBox.setGrowHeight(true);
    _chartBox.addChild(_optionButton);
    
    // Create PropsView
    PropsPane propsPane = new PropsPane(); propsPane._chartView = _chartView;
    View propsView = propsPane.getUI();
    
    // Create DataPane
    DataPane dataPane = new DataPane(_chartView);
    View dataPaneUI = dataPane.getUI();

    // Create TextView
    TextView textView = new TextView();
    textView.setDefaultStyle(textView.getDefaultStyle().copyFor(Font.Arial14));
    textView.setSource(WebURL.getURL(ChartPane.class, "Sample.json"));
    BoxView textBox = new BoxView(textView, true, true); textBox.setFill(new Color(.93)); textBox.setPadding(4,4,4,4);
    textBox.setGrowHeight(true); textBox.setPrefHeight(400);
    
    // Create TabView
    _tabView = new TabView(); _tabView.setPrefHeight(340); _tabView.setGrowHeight(true);
    _tabView.addTab("Chart Props", propsView);
    _tabView.addTab("Data Set", dataPaneUI);
    _tabView.addTab("JavaScript Embed", textBox);
    _tabView.setVisible(false); _tabView.setManaged(false); _tabView.setPickable(false);
    
    // Create ColView
    ColView col = new ColView(); col.setFillWidth(true); col.setGrowHeight(true); col.setFill(new Color(.93));
    col.setChildren(_chartBox, _tabView);
    return col;
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle OptionButton
    if(anEvent.equals("OptionButton"))
        setShowFull(!isShowFull());
}

/**
 * Performs an action.
 */
public void doAction(String anAction)
{
    String action = anAction.substring("Action:".length());

    if(action.equals("Playground"))
        setShowFull(true);
}

/**
 * A View class to hold chart.
 */
public class ChartBox extends BoxView {
    
    /** Create ChartBox. */
    public ChartBox()  { super(_chartView, true, true); }
    
    /** Override to position OptionsButton. */
    protected void layoutImpl()
    {
        super.layoutImpl();
        _optionButton.setSize(_optionButton.getPrefSize());
        _optionButton.setXY(getWidth() - _optionButton.getWidth() - 5, 4);
    }
}

}