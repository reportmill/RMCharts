package rmcharts.app;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import snap.util.SnapUtils;
import snap.view.ViewUtils;
import snap.web.WebURL;

/**
 * A custom class.
 */
public class App extends Object {

public static void main(String args[])
{
    snaptea.TV.set();
    
    if(SnapUtils.isTeaVM) {
        showChart();       //chartPane.getWindow().setMaximized(true);
    }
    
    else ViewUtils.runLater(() -> {
        ChartPane chartPane = new ChartPane(); chartPane.setWindowVisible(true);
        //String jsonText = WebURL.getURL(App.class, "Sample.json").getText();
        String jsonText = WebURL.getURL("/Temp/ChartSamples/ColBasic.json").getText();
        //String jsonText = WebURL.getURL("/Temp/ChartSamples/ThriveLeads.json").getText();
        chartPane._chartView.loadFromString(jsonText);
    });
}

public static void showChart(String anId, JSObject aMap)
{
    snaptea.TV.set();
    ChartPane chartPane = new ChartPane(); chartPane.setWindowVisible(true);
}

public static void showChart()
{
    // Get args from TeaVM env
    String arg0_containerName = getMainArg0();
    Object arg1_ConfigObject = getMainArg1();
    
    // Create ad show ChartPane
    ChartPane chartPane = new ChartPane();
    if(arg0_containerName!=null) chartPane.getWindow().setName(arg0_containerName);
    chartPane.setWindowVisible(true);
    
    // Load chart from JSON string
    if(arg1_ConfigObject instanceof String) { String str = (String)arg1_ConfigObject;
        chartPane._chartView.loadFromString(str);
    }
}

@JSBody(params = { }, script = "return rmChartsMainArg0;")
public static native String getMainArg0();

@JSBody(params = { }, script = "return rmChartsMainArg1;")
public static native String getMainArg1();

@JSBody(params = { "anObj" }, script = "return JSON.stringify(anObj);")
public static native String getJSON(JSObject anObj);

/**
 * A class to wrap around JavaScript objects to return properties by name.
 */
public interface JSMap extends JSObject {
    
    @JSBody(params = "aKey", script = "return this[aKey];")
    public JSMap get(String aKey);
}

}