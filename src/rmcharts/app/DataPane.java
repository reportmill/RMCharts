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

    // A Cell Action event listener to handle cell text changes
    EventListener           _cellEditLsnr;

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
    _tableView.setCellEditStart(c -> cellEditStart(c));
    _tableView.setCellEditEnd(c -> cellEditEnd(c));
    _tableView.addEventHandler(e -> tableViewDidMousePress(e), MousePress);
    _tableView.getHeaderCol().getHeader().setText("Series Name");
    _tableView.setShowHeaderCol(true);
    
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
    // Handle TableView
    if(anEvent.equals("TableView")) {
        //trimDataSet();
        //ListCell cell = _tableView.getSelCell();
        //if(cell!=null) {
        //    expandDataSet(cell.getRow(), cell.getCol());
        //    ViewUtils.runOnMouseUp(() -> _tableView.editCell(cell));
        //}
    }
    
    // Handle ClearButton
    if(anEvent.equals("ClearButton")) {
        DataSet dset = getDataSet();
        dset.clear();
        dset.addSeriesForNameAndValues(null, 0d);
    }
    
    // Handle SeriesSpinner
    if(anEvent.equals("SeriesSpinner")) {
        DataSet dset = getDataSet();
        dset.setSeriesCount(anEvent.getIntValue());
    }
    
    // Handle PointSpinner
    if(anEvent.equals("PointSpinner")) {
        DataSet dset = getDataSet();
        dset.setPointCount(anEvent.getIntValue());
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
        TableCol col = new TableCol(); col.setPrefWidth(80);
        Label header = col.getHeader(); header.setAlign(HPos.CENTER);
        _tableView.addCol(col);
    }
    
    // Configure last (empty) column to be zero size
    TableCol lastCol = _tableView.getCol(pointCount);
    lastCol.setPrefWidth(0); lastCol.setGrowWidth(true);
}

/**
 * Called when tableView gets mouse press to start cell edit.
 */
void tableViewDidMousePress(ViewEvent anEvent)
{
    // Get cell at event point (if none, just return)
    ListCell cell = _tableView.getCellAtXY(anEvent.getX(), anEvent.getY()); if(cell==null) return;

    // If row/col inside dataset bounds, edit
    int row = cell.getRow(), col = cell.getCol();
    if(row>=getDataSet().getSeriesCount() || col>=getDataSet().getPointCount()) {
        expandDataSet(row, col);
        resetLater();
        runLater(() -> _tableView.setSelCell(row, col));
    }
        
    // Start edit on MouseUp
    ViewUtils.runOnMouseUp(() -> {
        ListCell cell2 = _tableView.getSelCell();
        if(cell2!=null)
            _tableView.editCell(cell2);
        else System.err.println("tableViewMouseUP: WTF?");
    });
}

/**
 * Configures a table cell.
 */
void configureCell(ListCell <DataSeries> aCell)
{
    // Make sure empty cells are minimum size
    aCell.getStringView().setMinSize(40, Math.ceil(aCell.getFont().getLineHeight()));
    
    // Get DataSet and cell series
    DataSet dset = getDataSet();
    DataSeries series = aCell.getItem(); if(series==null) return;
    
    // Get column and column count
    int col = aCell.getCol(), colCount = dset.getPointCount();
    if(col<0) { aCell.setText(series.getName()); return; }
    if(col>=colCount) { aCell.setText(null); return;}
    
    // Get value
    Double val = series.getValue(col);
    aCell.setText(val!=null? StringUtils.toString(val) : null);
    aCell.setAlign(HPos.RIGHT);
}

/**
 * Called when cell starts editing.
 */
void cellEditStart(ListCell <DataSeries> aCell)
{
    aCell.setEditing(true);
}

/**
 * Called when cell stops editing.
 */
void cellEditEnd(ListCell <DataSeries> aCell)
{
    // Get new value and col
    String text = aCell.getText();
    Double newVal = text!=null && text.length()>0? SnapUtils.doubleValue(text) : null;
    DataSeries series = aCell.getItem();
    int row = aCell.getRow(), col = aCell.getCol();
    
    // If header column, set series name and return
    if(col<0)
        series.setName(text);
    
    // Get data point for series col and set value
    else {
        DataPoint dpoint = series.getPoint(col);
        dpoint.setValue(newVal);
        _tableView.updateItems(series);
    }
    
    // Trim DataSet
    trimDataSet();
}

/**
 * Updates DataSet Series count and Point count to include given row/col.
 */
void expandDataSet(int aRow, int aCol)
{
    DataSet dset = getDataSet();
    if(aRow>=dset.getSeriesCount())
        dset.setSeriesCount(aRow+1);
    if(aCol>=dset.getPointCount())
        dset.setPointCount(aCol+1);
}

/**
 * Removes empty series and slices.
 */
void trimDataSet()
{
    // While last series is clear, remove it
    DataSet dset = getDataSet();
    int sc = dset.getSeriesCount();
    while(sc>1 && dset.getSeries(sc-1).isClear())
        dset.removeSeries(--sc);
        
    // While last slice is empty, remove it
    int pc = dset.getPointCount();
    while(pc>1 && dset.isSliceEmpty(pc-1))
        dset.setPointCount(--pc);
}

}