package rmcharts.app;
import java.text.DecimalFormat;
import snap.gfx.*;
import snap.view.*;
import snap.web.WebURL;

/**
 * A view to render a chart.
 */
public class ChartView extends ColView {
    
    // The title
    StringView     _titleView;
    
    // The subtitle
    StringView     _subtitleView;
    
    // The row view for content
    RowView        _rowView;
    
    // The Y axis title View
    StringView     _yAxisTitleView;
    
    // The ChartArea
    ChartArea      _chartArea;
    
    // The Legend
    ChartLegend    _legend;
    
    // The chart type
    String         _type = LINE_TYPE;
    
    // The view for the current datapoint
    ColView        _dataPointView;
    
    // Constants
    public static final String LINE_TYPE = "Line";
    public static final String BAR_TYPE = "Bar";
    
    // Shared
    static DecimalFormat _fmt = new DecimalFormat("#,###.##");
    
/**
 * Creates a ChartView.
 */
public ChartView()
{
    // Configure this view
    setPadding(10,10,10,10); setAlign(Pos.CENTER); setSpacing(8); setGrowWidth(true);
    setFill(Color.WHITE);
    
    // Create configure TitleView
    _titleView = new StringView();
    _titleView.setFont(Font.Arial14.getBold().deriveFont(20));
    addChild(_titleView);
    
    // Create configure SubtitleView
    _subtitleView = new StringView(); _subtitleView.setTextFill(Color.GRAY);
    _subtitleView.setFont(Font.Arial12.getBold());
    addChild(_subtitleView);
    
    // Create RowView
    _rowView = new RowView(); _rowView.setAlign(Pos.CENTER_LEFT); _rowView.setSpacing(8);
    addChild(_rowView);
    
    // Create configure SubtitleView
    _yAxisTitleView = new StringView(); _yAxisTitleView.setTextFill(Color.GRAY); _yAxisTitleView.setRotate(-90);
    _yAxisTitleView.setFont(Font.Arial12.getBold().deriveFont(13));
    WrapView wrap = new WrapView(_yAxisTitleView); wrap.setPrefWidth(22);
    _rowView.addChild(wrap);
    
    // Create configure ChartArea
    _chartArea = new ChartAreaLine();
    _chartArea.addEventHandler(e -> _chartArea.animate(), MouseRelease);
    _rowView.addChild(_chartArea);
    
    // Create/configure ChartLegend
    _legend = new ChartLegend();
    _rowView.addChild(_legend);
    
    // Create/configure DataPoint view
    _dataPointView = new ColView(); _dataPointView.setPadding(7,7,15,7); _dataPointView.setSpacing(5);
    _dataPointView.setManaged(false); _dataPointView.setPickable(false);
    StringView sview = new StringView(); sview.setFont(Font.Arial10);
    _dataPointView.addChild(sview);
    RowView rview = new RowView(); rview.setSpacing(5);
    _dataPointView.addChild(rview);
    rview.addChild(new ShapeView(new Ellipse(0,0,5,5)));
    StringView sview2 = new StringView(); sview2.setFont(Font.Arial12); rview.addChild(sview2);
    StringView sview3 = new StringView(); sview3.setFont(Font.Arial12.deriveFont(13).getBold()); rview.addChild(sview3);
    
    
    // Set values
    //setTitle("Solar Employment Growth by Sector, 2010-2016");
    //setSubtitle("Source: thesolarfoundation.com");
    //setYAxisTitle("Number of Employees");
    
    //_chartArea.addSeriesForNameAndValues("Installation", 43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175);
    //_chartArea.addSeriesForNameAndValues("Manufacturing", 24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434);
    //_chartArea.addSeriesForNameAndValues("Sales & Distribution", 11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387);
    //_chartArea.addSeriesForNameAndValues("Project Development", 7988, 7988, 7988, 12169, 15112, 22452, 34400, 34227);
    //_chartArea.addSeriesForNameAndValues("Other", 12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111);
    
    ChartParser parser = new ChartParser(this);
    String chartJSONText = WebURL.getURL(getClass(), "Sample.json").getText();
    parser.parseString(chartJSONText);
    
    // Update chart area
    _legend.update(_chartArea);
    _chartArea.animate();
}

/**
 * Returns the ChartArea.
 */
public ChartArea getChartArea()  { return _chartArea; }

/**
 * Returns the title.
 */
public String getTitle()  { return _titleView.getText(); }

/**
 * Sets the title.
 */
public void setTitle(String aStr)  { _titleView.setText(aStr); }

/**
 * Returns the subtitle.
 */
public String getSubtitle()  { return _subtitleView.getText(); }

/**
 * Sets the subtitle.
 */
public void setSubtitle(String aStr)  { _subtitleView.setText(aStr); }

/**
 * Returns the YAxisTitle.
 */
public String getYAxisTitle()  { return _yAxisTitleView.getText(); }

/**
 * Sets the YAxisTitle.
 */
public void setYAxisTitle(String aStr)  { _yAxisTitleView.setText(aStr); }

/**
 * Returns the type.
 */
public String getType()  { return _type; }

/**
 * Sets the type.
 */
public void setType(String aType)
{
    _type = aType;
    
    ChartArea newChartArea = aType==LINE_TYPE? new ChartAreaLine() : new ChartAreaBar();
    newChartArea._series = _chartArea._series; newChartArea._seriesStart = _chartArea._seriesStart;
    newChartArea.addEventHandler(e -> newChartArea.animate(), MouseRelease);
    ViewUtils.replaceView(_chartArea, _chartArea = newChartArea);
    
    _legend.update(newChartArea);
    newChartArea.animate();
}

}