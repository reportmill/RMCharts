package rmcharts.app;
import java.util.*;
import java.util.function.Consumer;
import snap.gfx.*;
import snap.view.*;

/**
 * A TableView subclass to emulate a spreadsheet.
 */
public class SheetView extends TableView <Object> {

    // The min row/col counts
    int               _minRowCount, _minColCount;
    
    // The extra row/col counts
    int               _extraRowCount = 1, _extraColCount = 1;
    
    // The extra column width
    double            _extraColWidth = 80;
    
    // The Cell Configure method
    Consumer <Label>  _headerConf;
    
    // The Rebuild run
    Runnable          _rebuildRun, _rebuildRunCached = () -> { rebuildNow(); _rebuildRun = null; };
    
/**
 * Creates a SheetView.
 */
public SheetView()
{
    setShowHeader(true); setEditable(true); setCellPadding(new Insets(6));
    setShowHeaderCol(true);
    getHeaderCol().setPrefWidth(35);
}

/**
 * Returns the min row count.
 */
public int getMinRowCount()  { return _minRowCount; }

/**
 * Sets the min row count.
 */
public void setMinRowCount(int aValue)
{
    _minRowCount = aValue;
    rebuild();
}

/**
 * Returns the min col count.
 */
public int getMinColCount()  { return _minColCount; }

/**
 * Sets the min col count.
 */
public void setMinColCount(int aValue)
{
    _minColCount = aValue;
    rebuild();
}

/**
 * Returns the extra row count.
 */
public int getExtraRowCount()  { return _extraRowCount; }

/**
 * Sets the extra row count.
 */
public void setExtraRowCount(int aValue)
{
    _extraRowCount = aValue;
    rebuild();
}

/**
 * Returns the extra col count.
 */
public int getExtraColCount()  { return _extraColCount; }

/**
 * Sets the extra col count.
 */
public void setExtraColCount(int aValue)
{
    _extraColCount = aValue;
    rebuild();
}

/**
 * Returns the extra col count.
 */
public double getExtraColWidth()  { return _extraColWidth; }

/**
 * Sets the extra col count.
 */
public void setExtraColWidth(double aValue)
{
    _extraColWidth = aValue;
    rebuild();
}

/**
 * Returns method to configure header labels.
 */
public Consumer <Label> getHeaderConfigure()  { return _headerConf; }

/**
 * Sets method to configure header labels.
 */
public void setHeaderConfigure(Consumer<Label> aHC)  { _headerConf = aHC; }

/**
 * Return column at index, adding if needed.
 */
public TableCol getColForce(int anIndex)
{
    // If column count too low, add columns
    if(anIndex>=getColCount()) { double colWidth = getExtraColWidth();
        while(anIndex>=getColCount()) {
            TableCol col = new TableCol(); col.setPrefWidth(colWidth); col.setWidth(colWidth);
            Label header = col.getHeader(); header.setAlign(HPos.CENTER);
            addCol(col);
        }
    }
    
    // Return column
    return getCol(anIndex);
}

/**
 * Rebuilds the sheet.
 */
protected void rebuild()  { if(_rebuildRun==null) getEnv().runLater(_rebuildRun=_rebuildRunCached); }

/**
 * Rebuilds the sheet now.
 */
protected void rebuildNow()
{
    // Calculate column count
    int colCount = getMinColCount() + getExtraColCount();
    
    // If too few columns, add more
    while(getColCount()<colCount)
        getColForce(getColCount());
    
    // If still extra space, add more extra columns
    double totalWidth = 0; for(TableCol col : getCols()) totalWidth += col.getWidth() + 2;
    if(totalWidth<getWidth()) while(totalWidth<getWidth()) {
        TableCol col = getColForce(getColCount());
        totalWidth += col.getWidth() + 2;
    }
    
    // If too many columns, remove them
    else {
        while(getColCount()>colCount && totalWidth>getWidth()) {
            TableCol lastCol = getCol(getColCount()-1);
            if(totalWidth - lastCol.getWidth() - 2>getWidth())
                removeCol(getColCount()-1);
            else break;
        }
    }
    
    // Reset headers
    resetHeaders();
    
    // Calculate row count
    int rowCount = getMinRowCount() + getExtraRowCount();
    double rowHeight = getRowHeight();
    
    // If too few rows, add more
    List items = new ArrayList(getItems());
    while(items.size()<rowCount)
        items.add(items.size());
    
    // If still extra height, add more extra rows
    double totalHeight = items.size()*rowHeight, tableHeight = getScrollView().getHeight();
    if(totalHeight<tableHeight) while(totalHeight<tableHeight) {
        items.add(items.size()); totalHeight += rowHeight; }
    
    // If too many columns, remove them
    else {
        while(items.size()>rowCount && totalHeight>tableHeight) {
            if(totalHeight - rowHeight>getHeight())
                items.remove(items.size()-1);
            else break;
        }
    }
    
    // Reset items and update all
    setItems(items);
    updateItems();
}

/**
 * Called to reset headers.
 */
protected void resetHeaders()
{
    Consumer <Label> hdrConf = getHeaderConfigure();
    for(int i=0;i<getMinColCount();i++) {
        TableCol col = getCol(i);
        Label header = col.getHeader();
        if(hdrConf!=null) hdrConf.accept(header);
        else header.setText(String.valueOf((char)('A' + i)));
    }
}

/**
 * Override to handle header col special.
 */
protected void configureCell(TableCol <Object> aCol, ListCell <Object> aCell)
{
    // Handle Header Col special
    if(aCell.getCol()<0) {
        aCell.setText(String.valueOf(aCell.getRow())); aCell.setAlign(HPos.CENTER); return; }
        
    // Do normal version
    super.configureCell(aCol, aCell);
}

/**
 * Override to rebuild.
 */
public void setWidth(double aValue)  { if(aValue==getWidth()) return; super.setWidth(aValue); rebuild(); }

/**
 * Override to rebuild.
 */
public void setHeight(double aValue)  { if(aValue==getHeight()) return; super.setHeight(aValue); rebuild(); }

}