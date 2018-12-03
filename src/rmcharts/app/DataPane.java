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
    _tableView.setShowHeader(true);
    _tableView.setCellConfigure(c -> configureCell(c));
    
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
    
    // Check column count
    if(_tableView.getColCount()!=dset.getPointCount()+1)
        resetTableColumns();
    
    // Reset headers
    DataSeries series = dset.getSeries(0);
    int pointCount = dset.getPointCount();
    for(int i=0;i<pointCount;i++) {
        DataPoint dpnt = series.getPoint(i);
        String hdrText = dpnt.getKeyString();
        TableCol col = _tableView.getCol(i);
        Label header = col.getHeader(); header.setText(hdrText);
    }
}

/**
 * Resets table columns.
 */
void resetTableColumns()
{
    // Clear columns
    while(_tableView.getColCount()>0) _tableView.removeCol(0);
    
    // Add column for number of values in series
    DataSet dset = getDataSet();
    int pointCount = dset.getPointCount();
    for(int i=0;i<=pointCount;i++) {
        TableCol col = new TableCol(); col.setPrefWidth(70);
        Label header = col.getHeader(); header.setAlign(HPos.CENTER);
        _tableView.addCol(col);
    }
    
    // Configure last (empty) column to be zero size
    TableCol lastCol = _tableView.getCol(pointCount);
    lastCol.setPrefWidth(0); lastCol.setGrowWidth(true);
}

/**
 * Configures a table cell.
 */
void configureCell(ListCell <DataSeries> aCell)
{
    DataSeries series = aCell.getItem(); if(series==null) return;
    DataSet dset = getDataSet();
    int col = aCell.getCol(), colCount = dset.getPointCount(); if(col>=colCount) { aCell.setText(null); return;}
    Double val = series.getValue(col);
    aCell.setText(val!=null? StringUtils.toString(val) : null);
    aCell.setAlign(HPos.RIGHT);
}

}