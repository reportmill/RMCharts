package rmcharts.app;
import java.util.List;
import snap.gfx.*;
import snap.util.ArrayUtils;
import snap.view.*;

/**
 * A custom class.
 */
public class ChartLegend extends ColView {
    
    // Colors
    static Color    COLORS[] = new Color[] { Color.get("#88B4E7"), Color.BLACK, Color.get("#A6EB8A"),
        Color.get("#EBA769"), Color.get("#8185E2") };
    
    // Shapes
    static Shape SHAPES[];
    

/**
 * Creates a ChartLegend.
 */
public ChartLegend()
{
    Shape shp0 = new Ellipse(0,0,8,8);
    Shape shp1 = new Polygon(4,0,8,4,4,8,0,4);
    Shape shp2 = new Rect(0,0,8,8);
    Shape shp3 = new Polygon(4,0,8,8,0,8);
    Shape shp4 = new Polygon(0,0,8,0,4,8);
    SHAPES = new Shape[] { shp0, shp1, shp2, shp3, shp4 };
}

/**
 * Updates.
 */
public void update(ChartArea aChart)
{
    ChartView chart = getParent(ChartView.class);
    List <DataSeries> allSeries = aChart.getSeries();
    removeChildren();

    for(int i=0; i<allSeries.size(); i++) { DataSeries series = allSeries.get(i);
        
        Shape shp0 = SHAPES[i]; shp0 = shp0.copyFor(new Transform(6, 6));
        Shape shp1 = new Rect(2,9,16,2);
        Shape shp3 = Shape.add(shp0, shp1);
        if(chart.getType()==ChartView.BAR_TYPE) shp3 = SHAPES[0].copyFor(new Transform(6, 6));
        ShapeView shpView = new ShapeView(shp3); shpView.setPrefSize(20,20); shpView.setFill(COLORS[i]);
        
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
    ChartView chart = getParent(ChartView.class);
    ChartArea chartArea = chart.getChartArea();
    int index = ArrayUtils.indexOf(getChildren(), aRow);
    DataSeries series = chartArea.getSeries(index);
    series.setDisabled(!series.isDisabled());
    chartArea.animate();
    update(chartArea);
}

}