package rmcharts.app;
import java.util.List;
import snap.gfx.HPos;
import snap.util.StringUtils;
import snap.view.*;

/**
 * A class to manage the datasets.
 */
public class DataPane extends ViewOwner {
    
    // The ChartView
    ChartView               _chartView;
    
    // The TableView
    TableView <DataSeries>  _tableView;

/**
 * Creates a DataPane for given ChartView.
 */
public DataPane(ChartView aCV)  { _chartView = aCV; }

/**
 * Returns the DataSet.
 */
public DataSet getDataSet()  { return _chartView.getDataSet(); }

/**
 * Create UI.
 */
protected View createUI()
{
    _tableView = new TableView(); _tableView.setGrowHeight(true);
    _tableView.setCellConfigure(c -> configureCell(c));
    
    // Add column for number of values in series
    DataSet dset = getDataSet();
    int valCount = dset.getValueCount();
    for(int i=0;i<=valCount+1;i++) {
        TableCol col = new TableCol(); col.setPrefWidth(70);
        _tableView.addCol(col);
    }
    
    // Create box and return
    ColView colView = new ColView(); colView.setPadding(25,5,5,5); colView.setFillWidth(true);
    colView.setChildren(_tableView);
    return colView;
}

/**
 * Resets the UI.
 */
protected void resetUI()
{
    DataSet dset = getDataSet();
    List <DataSeries> seriesList = dset.getSeries();
    _tableView.setItems(seriesList);
}

/**
 * Configures a table cell.
 */
void configureCell(ListCell <DataSeries> aCell)
{
    DataSeries series = aCell.getItem(); if(series==null) return;
    int col = aCell.getCol(), colCount = getDataSet().getValueCount(); if(col>=colCount) { aCell.setText(null); return;}
    Double val = series.getValue(col);
    aCell.setText(val!=null? StringUtils.toString(val) : null);
    aCell.setAlign(HPos.RIGHT);
}

}