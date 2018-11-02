package rmcharts.app;
import snap.util.*;

/**
 * A class to load chart parameters from JSON.
 */
public class ChartParser {
    
    // The chart view
    ChartView     _chartView;

/**
 * Create ChartParser for given ChartView.
 */
public ChartParser(ChartView aGV)  { _chartView = aGV; }

/**
 * Parse given JSON string.
 */
public void parseString(String aStr)
{
    JSONNode json = new JSONParser().readString(aStr);
    
    if(json.isObject())
        parseChart(json);
}

/**
 * Parse given JSON object.
 */
protected void parseChart(JSONNode aNode)
{
    for(int i=0; i<aNode.getNodeCount(); i++) { String key = aNode.getKey(i);
        JSONNode node = aNode.getNode(i);
        
        switch(key.toLowerCase()) {
            case "chart": parseChartNode(node); break;
            case "legend": parseLegend(node); break;
            case "plotoptions": parsePlotOptions(node); break;
            case "series": parseSeries(node); break;
            case "subtitle": parseSubtitle(node); break;
            case "title": parseTitle(node); break;
            case "yaxis": parseYAxis(node); break;
            default: System.out.println("Unsupported node: " + key);
        }
    }
}

/**
 * Parse a chart node.
 */
protected void parseChartNode(JSONNode aNode)
{
    String type = aNode.getNodeString("type");
    if(type!=null) switch(type) {
        case "line": _chartView.setType(ChartView.LINE_TYPE); break;
        case "column": _chartView.setType(ChartView.BAR_TYPE); break;
    }
}

/**
 * Parse a Title node.
 */
protected void parseTitle(JSONNode aNode)
{
    String text = aNode.getNodeString("text");
    if(text!=null)
        _chartView.setTitle(text);
}

/**
 * Parse a Subtitle node.
 */
protected void parseSubtitle(JSONNode aNode)
{
    String text = aNode.getNodeString("text");
    if(text!=null)
        _chartView.setSubtitle(text);
}

/**
 * Parse a YAxis node.
 */
protected void parseYAxis(JSONNode aNode)
{
    // Get title node
    JSONNode titleNode = aNode.getNode("title");
    if(titleNode!=null) {
        String text = titleNode.getNodeString("text");
        if(text!=null)
            _chartView.setYAxisTitle(text);
    }
}

/**
 * Parse a Legend node.
 */
protected void parseLegend(JSONNode aNode)
{
    // Get enabled node
    JSONNode enabledNode = aNode.getNode("enabled");
    if(enabledNode!=null)
        _chartView.setShowLegend(enabledNode.getBoolean());
}

/**
 * Parse plot options.
 */
protected void parsePlotOptions(JSONNode aNode)
{
    JSONNode series = aNode.getNode("series");
    
    // Parse series
    if(series!=null) {
        
        JSONNode pointStart = series.getNode("pointStart");
        if(pointStart!=null) {
            int start = SnapUtils.intValue(pointStart.getNumber());
            _chartView.setSeriesStart(start);
        }
    }
}

/**
 * Parse plot series.
 */
protected void parseSeries(JSONNode aNode)
{
    // Complain if not series
    if(!aNode.isArray()) { System.out.println("Series is not array"); return; }
    
    // Iterate over array
    for(int i=0;i<aNode.getNodeCount();i++) { JSONNode seriesNode = aNode.getNode(i);
    
        // Create series
        DataSeries series = new DataSeries();
        
        // Get name
        JSONNode nameNode = seriesNode.getNode("name");
        String name = nameNode!=null? nameNode.getString() : null;
        if(name!=null)
            series.setName(name);
            
        // Get data node
        JSONNode dataNode = seriesNode.getNode("data");
        if(dataNode==null) { System.out.println("Series has no data!"); continue; }
        
        // Iterate over values
        for(int j=0; j<dataNode.getNodeCount(); j++) { JSONNode valNode = dataNode.getNode(j);
            Number value = valNode.getNumber();
            if(value!=null)
                series.addValue(value.doubleValue());
        }
        
        // Add series
        _chartView.addSeries(series);
    }
}

}