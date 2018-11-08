package rmcharts.app;
import java.util.*;
import snap.gfx.Color;
import snap.gfx.VPos;
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
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        
        switch(key.toLowerCase()) {
            case "chart": parseChartNode(child); break;
            case "colors": parseColors(child); break;
            case "legend": parseLegend(child); break;
            case "plotoptions": parsePlotOptions(child); break;
            case "series": parseSeries(child); break;
            case "subtitle": parseSubtitle(child); break;
            case "title": parseTitle(child); break;
            case "xaxis": parseXAxis(child); break;
            case "yaxis": parseYAxis(child); break;
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
 * Parse a XAxis node.
 */
protected void parseXAxis(JSONNode aNode)
{
    // Get XAxis
    ChartXAxisView xaxis = _chartView.getXAxis();

    // Iterate over nodes
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        switch(key.toLowerCase()) {
            
            // Handle title node
            case "title": parseXAxisTitle(child); break;
            
            // Handle categories array
            case "categories": parseXAxisCategories(child); break;
            
            // Handle labels node
            case "labels": parseXAxisLabels(child); break;
            
            // Handle default (complain)
            default: System.out.println("Unsupported node: xaxis." + key + " = " + child.getString());
        }
    }
}

/**
 * Parse xaxis categories node.
 */
protected void parseXAxisCategories(JSONNode aNode)
{
    // Complain if not series
    if(!aNode.isArray()) { System.err.println("ChartParser.parseXAxisCategories: Node not array"); return; }
    
    // Iterate over array
    List <String> strings = new ArrayList();
    for(int i=0;i<aNode.getNodeCount();i++) { JSONNode strNode = aNode.getNode(i);
        strings.add(strNode.getString()); }
    
    // Set categories string list
    _chartView.getXAxis().setCategories(strings);
}

/**
 * Parse a XAxis labels node.
 */
protected void parseXAxisLabels(JSONNode aNode)
{
    // Get YAxis
    ChartYAxisView yaxis = _chartView.getYAxis();

    // Iterate over nodes
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        switch(key.toLowerCase()) {
            
            // Handle style node
            //case "style": parseXAxisLabelsStyle(child); break;
            
            // Handle default (complain)
            default: System.out.println("Unsupported node: xaxis.labels." + key + " = " + child.getString());
        }
    }
}

/**
 * Parse a XAxis title node.
 */
protected void parseXAxisTitle(JSONNode aNode)
{
    // Get XAxis
    ChartXAxisView xaxis = _chartView.getXAxis();

    // Iterate over nodes
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        switch(key.toLowerCase()) {
            
            // Handle xaxis.title.align
            /*case "align": {
                String val = child.getString().toLowerCase();
                if(val.equals("high")) xaxis.getTitleView().getParent().setAlign(VPos.TOP);
                else if(val.equals("low")) xaxis.getTitleView().getParent().setAlign(VPos.BOTTOM);
            } break;*/
            
            // Handle xaxis.title.rotation
            /*case "rotation": {
                double rot = child.getNumber().doubleValue();
                yaxis.getTitleView().setRotate(rot);
            } break;*/
            
            // Handle xaxis.title.text
            /*case "offset": {
                double val = child.getNumber().doubleValue();
                yaxis.setTitleOffset(val);
            } break;*/
            
            // Handle xaxis.title.text
            /*case "text": {
                String text = child.getString();
                yaxis.setTitle(text);
            } break;*/
            
            // Handle xaxis.title.X
            /*case "x": {
                double x = child.getNumber().doubleValue();
                yaxis.getTitleView().setTransX(x);
            } break;*/
            
            // Handle xaxis.title.Y
            /*case "y": {
                double y = child.getNumber().doubleValue();
                yaxis.getTitleView().setTransY(y);
            } break;*/
            
            // Handle default (complain)
            default: System.out.println("Unsupported node: xaxis.title." + key + " = " + child.getString());
        }
    }
}

/**
 * Parse a YAxis node.
 */
protected void parseYAxis(JSONNode aNode)
{
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        switch(key.toLowerCase()) {
            
            // Handle labels node
            case "labels": parseYAxisLabels(child); break;
            
            // Handle title node
            case "title": parseYAxisTitle(child); break;
            
            // Handle default (complain)
            default: System.out.println("Unsupported node: yaxis." + key + " = " + child.getString());
        }
    }
}

/**
 * Parse a YAxis labels node.
 */
protected void parseYAxisLabels(JSONNode aNode)
{
    // Get YAxis
    ChartYAxisView yaxis = _chartView.getYAxis();

    // Iterate over nodes
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        switch(key.toLowerCase()) {
            
            // Handle style node
            case "style": parseYAxisLabelsStyle(child); break;
            
            // Handle default (complain)
            default: System.out.println("Unsupported node: yaxis.labels." + key + " = " + child.getString());
        }
    }
}

/**
 * Parse a YAxis labels style node.
 */
protected void parseYAxisLabelsStyle(JSONNode aNode)
{
    // Get YAxis
    ChartYAxisView yaxis = _chartView.getYAxis();

    // Iterate over nodes
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        switch(key.toLowerCase()) {
            
            // Handle default (complain)
            default: System.out.println("Unsupported node: yaxis.labels.style." + key + " = " + child.getString());
        }
    }
}

/**
 * Parse a YAxis title node.
 */
protected void parseYAxisTitle(JSONNode aNode)
{
    // Get YAxis
    ChartYAxisView yaxis = _chartView.getYAxis();

    // Iterate over nodes
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        switch(key.toLowerCase()) {
            
            // Handle yaxis.title.align
            case "align": {
                String val = child.getString().toLowerCase();
                if(val.equals("high")) yaxis.getTitleView().getParent().setAlign(VPos.TOP);
                else if(val.equals("low")) yaxis.getTitleView().getParent().setAlign(VPos.BOTTOM);
            } break;
            
            // Handle yaxis.title.rotation
            case "rotation": {
                double rot = child.getNumber().doubleValue();
                yaxis.getTitleView().setRotate(rot);
            } break;
            
            // Handle yaxis.title.offset
            case "offset": {
                double val = child.getNumber().doubleValue();
                yaxis.setTitleOffset(val);
            } break;
            
            // Handle yaxis.title.style
            case "style": parseYAxisTitleStyle(child); break;
            
            // Handle yaxis.title.text
            case "text": {
                String text = child.getString();
                yaxis.setTitle(text);
            } break;
            
            // Handle yaxis.title.x
            case "x": {
                double x = child.getNumber().doubleValue();
                yaxis.getTitleView().setTransX(x);
            } break;
            
            // Handle yaxis.title.y
            case "y": {
                double y = child.getNumber().doubleValue();
                yaxis.getTitleView().setTransY(y);
            } break;
            
            // Handle default (complain)
            default: System.out.println("Unsupported node: yaxis.title." + key + " = " + child.getString());
        }
    }
}

/**
 * Parse a YAxis title style node.
 */
protected void parseYAxisTitleStyle(JSONNode aNode)
{
    // Get YAxis
    ChartYAxisView yaxis = _chartView.getYAxis();

    // Iterate over nodes
    for(JSONNode child : aNode.getNodes()) { String key = child.getKey();
        switch(key.toLowerCase()) {
            
            // Handle default (complain)
            default: System.out.println("Unsupported node: yaxis.title.style." + key + " = " + child.getString());
        }
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
    if(!aNode.isArray()) { System.err.println("ChartParser.parseSeries: Series is not array"); return; }
    
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
        if(dataNode==null) { System.err.println("ChartParser.parseSeries: Series has no data!"); continue; }
        
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

/**
 * Parse chart colors.
 */
protected void parseColors(JSONNode aNode)
{
    // Complain if not array
    if(!aNode.isArray()) { System.err.println("Colors is not array"); return; }
    
    // Iterate over nodes to get color strings
    List <Color> colors = new ArrayList();
    for(int i=0;i<aNode.getNodeCount();i++) { JSONNode colorNode = aNode.getNode(i);
    
        // Get color
        String str = colorNode.getString();
        Color color = Color.get(str);
        if(color==null) System.err.println("ChartParser.parseColors: Invalid color: " + str);
        else colors.add(color);
    }
    
    // Set colors
    _chartView.setColors(colors.toArray(new Color[colors.size()]));
}

}