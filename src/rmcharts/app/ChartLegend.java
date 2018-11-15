package rmcharts.app;
import java.util.List;
import snap.gfx.*;
import snap.util.ArrayUtils;
import snap.view.*;

/**
 * A view to display chart legend.
 */
public class ChartLegend extends ColView {
    
    // The ChartView
    ChartView    _chartView;
    
/**
 * Returns the ChartView.
 */
public ChartView getChartView()  { return _chartView!=null? _chartView : (_chartView=getParent(ChartView.class)); }

/**
 * Reloads legend contents.
 */
public void reloadContents()
{
    ChartView chart = getChartView();
    List <DataSeries> allSeries = chart.getSeries();
    removeChildren();

    for(int i=0; i<allSeries.size(); i++) { DataSeries series = allSeries.get(i);
        
        // Get marker Shape (if LineChart, add crossbar)
        Shape shp = chart.getMarkerShape(i); shp = shp.copyFor(new Transform(6, 6));
        if(chart.getType()==ChartView.LINE_TYPE) {
            Shape shp1 = new Rect(2,9,16,2);
            shp = Shape.add(shp, shp1);
        }
        
        // Create marker ShapeView
        ShapeView shpView = new ShapeView(shp); shpView.setPrefSize(20,20);
        
        // Set color
        shpView.setFill(chart.getColor(i));
        
        StringView sview = new StringView(); sview.setFont(Font.Arial12.deriveFont(13).getBold());
        sview.setText(series.getName());
        if(series.isDisabled()) { shpView.setFill(Color.LIGHTGRAY); sview.setTextFill(Color.LIGHTGRAY); }
        RowView row = new RowView(); row.addChild(shpView); row.addChild(sview);
        addChild(row);
        
        // Register row to enable/disable
        row.addEventHandler(e -> rowWasClicked(row), MouseRelease);
        //shpView.setPickable(false); sview.setPickable(false);
    }
}

/**
 * Called when legend row is clicked.
 */
void rowWasClicked(RowView aRow)
{
    // Get row/series index
    int index = ArrayUtils.indexOf(getChildren(), aRow);

    // Get series and disable
    ChartView chart = getParent(ChartView.class);
    DataSeries series = chart.getSeries(index);
    series.setDisabled(!series.isDisabled());
    
    // Redraw chart and reload legend
    chart.reloadContents();
}

}