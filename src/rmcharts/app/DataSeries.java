package rmcharts.app;
import java.util.*;

/**
 * A custom class.
 */
public class DataSeries {

    // The name
    String         _name;
    
    // The values
    List <Double>  _values = new ArrayList();
    
    // The index in data set
    int            _index;
    
    // Whether series is disabled
    boolean        _disabled;

/**
 * Returns the name.
 */
public String getName()  { return _name; }

/**
 * Sets the name.
 */
public void setName(String aValue)  { _name = aValue; }

/**
 * Returns the values.
 */
public List <Double> getValues()  { return _values; }

/**
 * Sets the values.
 */
public void setValues(double ... theVals)
{
    for(double v : theVals) _values.add(v);
}

/**
 * Adds a value.
 */
public void addValue(double aValue)
{
    _values.add(aValue);
}

/**
 * Returns the number values.
 */
public int getCount()  { return _values.size(); }

/**
 * Returns the value at given index.
 */
public double getValue(int anIndex)
{
    return anIndex<getCount()? _values.get(anIndex) : 0;
}

/**
 * Returns the index in dataset.
 */
public int getIndex()  { return _index; }

/**
 * Returns whether series is disabled.
 */
public boolean isDisabled()  { return _disabled; }

/**
 * Sets whether series is disabled.
 */
public void setDisabled(boolean aValue)  { _disabled = aValue; }

/**
 * Returns whether series is enabled.
 */
public boolean isEnabled()  { return !_disabled; }

}