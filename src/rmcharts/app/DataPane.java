package rmcharts.app;
import java.util.List;
import snap.gfx.*;
import snap.util.*;
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
protected void initUI()
{
    _tableView = getView("TableView", TableView.class);
    _tableView.setShowHeader(true); _tableView.setEditable(true); _tableView.setCellPadding(new Insets(6));
    _tableView.setCellConfigure(c -> configureCell(c));
    _tableView.setCellConfigureEdit(c -> configureCellEdit(c));
    
    // Create box and return
    //ColView colView = new ColView(); colView.setPadding(25,5,5,5); colView.setFillWidth(true);
    //colView.setChildren(_tableView);
}

/**
 * Resets the UI.
 */
protected void resetUI()
{
    DataSet dset = getDataSet();
    List <DataSeries> seriesList = dset.getSeries();
    
    // Update SeriesSpinner, PointSpinner
    setViewValue("SeriesSpinner", seriesList.size());
    setViewValue("PointSpinner", dset.getPointCount());
    
    // Set TableView items
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
 * Resets the UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    
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
        TableCol col = new TableCol(); col.setPrefWidth(80);
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

/**
 * Called when cell is edited.
 */
void configureCellEdit(ListCell <DataSeries> aCell)
{
    aCell.setEditing(true);
    aCell.addEventHandler(e -> cellFiredAction(aCell), Action);
}

/**
 * Called when cell is edited and fires action.
 */
void cellFiredAction(ListCell <DataSeries> aCell)
{
    // Get new value and col
    double newVal = SnapUtils.doubleValue(aCell.getText());
    int col = aCell.getCol();
    
    // Get data point for series col and set value
    DataSeries series = aCell.getItem();
    DataPoint dpoint = series.getPoint(col);
    dpoint.setValue(newVal);
    runLater(() -> aCell.getEventAdapter().clear());
}

}