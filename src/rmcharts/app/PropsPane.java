package rmcharts.app;
import snap.view.*;

/**
 * A custom class.
 */
public class PropsPane extends ViewOwner {
    
    // The ChartView
    ChartView        _chartView;

/**
 * Reset UI.
 */
protected void resetUI()
{
    if(_chartView==null || _chartView.getLegend()==null) return;
    
    // Reset TitleText, SubtitleText, YAxisTitleText
    setViewValue("TitleText", _chartView.getTitle());
    setViewValue("SubtitleText", _chartView.getSubtitle());
    setViewValue("YAxisTitleText", _chartView.getYAxisTitle());
    
    // Reset ShowLegendCheckBox, PartialYAxisCheckBox
    System.out.println("ShowLegendCheckBox found: " + (getView("LegendShowingCheckBox")!=null));
    setViewValue("LegendShowingCheckBox", _chartView.isShowLegend());
    setViewValue("PartialYAxisCheckBox", _chartView.isShowPartialY());
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle LineChartButton, BarChartButton
    if(anEvent.equals("LineChartButton")) _chartView.setType(ChartView.LINE_TYPE);
    if(anEvent.equals("BarChartButton")) _chartView.setType(ChartView.BAR_TYPE);
    
    // Handle TitleText, SubtitleText, YAxisTitleText
    if(anEvent.equals("TitleText")) _chartView.setTitle(anEvent.getStringValue());
    if(anEvent.equals("SubtitleText")) _chartView.setSubtitle(anEvent.getStringValue());
    if(anEvent.equals("YAxisTitleText")) _chartView.setYAxisTitle(anEvent.getStringValue());
    
    // Handle ShowLegendCheckBox, PartialYAxisCheckBox
    if(anEvent.equals("ShowLegendCheckBox")) _chartView.setShowLegend(anEvent.getBoolValue());
    if(anEvent.equals("PartialYAxisCheckBox")) _chartView.setShowPartialY(anEvent.getBoolValue());
}

}