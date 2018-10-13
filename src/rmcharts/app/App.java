package rmcharts.app;
import snap.util.SnapUtils;

/**
 * A custom class.
 */
public class App extends Object {

public static void main(String args[])
{
    snaptea.TV.set();
    ChartPane chartPane = new ChartPane(); chartPane.setWindowVisible(true);
    //if(SnapUtils.isTeaVM) chartPane.getWindow().setMaximized(true);
}

public static void mainCharts(String args[])
{
    
}

}