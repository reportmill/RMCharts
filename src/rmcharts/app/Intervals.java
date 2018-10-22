/*
 * Copyright (c) 2010, ReportMill Software. All rights reserved.
 */
package rmcharts.app;
import java.util.*;
import snap.util.*;

/**
 * This class represent a set of equally spaced interval values for a given min and max value and a height.
 * The intervals cover the range and should grow in increments of 10%, 20%, 25%, 40%, 50% or 100% and guarantee
 * that the height of the intervals is at least 45 pnts but no more than 75 pnts.
 */
public class Intervals {
    
    // The seed max value provided
    double            _maxVal;
    
    // The seed height
    double            _height;
    
    // The change between intervals
    double            _delta;

    // The list of interval numbers
    List <Double>     _intervals;

/**
 * Return well-chosen intervals given a min value, a max value and a height. For instance, (1,4) would return
 *    (1,2,3,4,5), while (17,242) would return (50,100,150,200,250). Useful for graphing.
 */
public Intervals(double minValue, double maxValue, double aHeight)
{
    // Get intervals for range
    _maxVal = maxValue; _height = aHeight;
    _intervals = getIntervalsFor(minValue, maxValue, aHeight);
    _delta = getInterval(1) - getInterval(0);
}

/**
 * Returns the seed max val.
 */
public double getSeedValueMax()  { return _maxVal; }

/**
 * Returns the seed height val.
 */
public double getSeedHeight()  { return _height; }

/**
 * Returns the number of intervals for this filled graph.
 */
public int getCount()  { return _intervals.size(); }

/**
 * Returns the individual interval at a given index as a float value.
 */
public Double getInterval(int anIndex)  { return _intervals.get(anIndex); }

/**
 * Returns the last interval as a double value.
 */
public double getMax()  { return getInterval(getCount()-1); }

/**
 * Returns the interval change as a double value.
 */
public double getDelta()  { return _delta; }

/**
 * Return well-chosen intervals given a min/max value. For instance, (1,4) would return
 *    (1,2,3,4,5), while (17,242) would return (50,100,150,200,250). Useful methods for graphing.
 */
private List <Double> getIntervalsFor(double aMinValue, double aMaxValue, double aHeight)
{
    // If both max & min values greater than zero, just return intervalsFor maxValue
    if(aMaxValue>=0 && aMinValue>=0)
        return getIntervalsFor(aMaxValue, aHeight);
        
    // If maxVal is positive and greater in magnitude than minVal, find intervals for maxVal
    // such that unused intervals are sufficient for minVal
    if(aMaxValue>=0 && aMaxValue>=Math.abs(aMinValue)) {
        
        // Keep going till loop short circuits
        while(true) {
            
            // Get intervals for max value
            List <Double> intervals = getIntervalsFor(aMaxValue, aHeight);
            double interval = intervals.get(1) - intervals.get(0);
            
            // If the lesser value can fit in unused intervals, do a shift and return
            if(Math.abs(aMinValue) < (6 - intervals.size())*interval) {
                
                // Get first interval
                double firstInterval = intervals.get(0);

                // 
                while(aMinValue < firstInterval) {
                    firstInterval -= interval;
                    intervals.add(0, firstInterval);
                }
                
                // Return intervals
                return intervals;
            }
            
            // Bump max value and redo
            aMaxValue = 5*interval + .1f*interval;
        }
    }
    
    // If min/max aren't predominantly positive, get intervals for flipped & negated min/max...
    List <Double> intervals = getIntervalsFor(-aMaxValue, -aMinValue);
    
    // ...then flip & negate them
    ListUtils.reverse(intervals);
    for(int i=0, iMax=intervals.size(); i<iMax; i++)
        intervals.set(i, -intervals.get(i));
    
    // Return intervals
    return intervals;
}

/**
 * Returns well-chosen intervals from zero to a given a value. Finds the first multiple of {5,10 or 25}*10^n that
 * equals or exceeds max value, then divides by 5. This method could probably be done a lot simpler.
 */
private static List <Double> getIntervalsFor(double maxValue, double aHeight)
{
    // Find factor of 10 that is just below maxValue (10 ^ factor+1 is above)
    int pow = -10; double factor = Math.pow(10, pow);
    while(true) { if(factor<=maxValue && factor*10>=maxValue) break; pow++; factor = Math.pow(10,pow); }
    
    // Declare array of pleasing increments (percents/100)
    double increments[] = { .2, .25, .40, .50, 1, 2, 2.5, 4, 5, 10 }, incr = factor;
    int steps = 1;
    
    // Iterate over pleasing increments to find one that results in reasonable height for increment
    for(int i=0; i<increments.length; i++) { incr = increments[i]*factor;
        
        // Find out how many steps it takes to get from zero to maxValue with current increment
        steps = 1;
        double val = incr; while(val<maxValue && steps<11) { val += incr; steps++; }
        
        // If more than 10 continue
        if(steps>10) continue;
        
        // If height per step out of bounds, continue
        double dh = aHeight/steps; if(dh<45 || dh>75) continue;
        
        // If maxValue within 10 points of height, continue
        double axisMax = steps*incr, maxValueHeight = maxValue/axisMax*aHeight;
        if(maxValueHeight+10>aHeight) continue;
        
        // Break since increment, steps and padding are sufficient
        break;
    }
    
    // Create intervals
    List <Double> intervalsList = new ArrayList();
    for(int i=0;i<=steps;i++) intervalsList.add(incr*i);
    return intervalsList;
}

}