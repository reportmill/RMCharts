
To build for TeaVM:

	- Run teavm compiler (with minimizer)
	- cat runtime.js classes.js RMCharts0.js > RMCharts.js


RMCharts0.js looks like this:

    var rmChartsMainArg0, rmChartsMainArg1;

    var ReportMill = {
      chart: function chart(anId, anObj) {
        rmChartsMainArg0 = anId; rmChartsMainArg1 = JSON.stringify(anObj);
        main();
      }
    };

Then you can generate the chart from your HTML like this:

<script src="http://reportmill.com/rmc1/RMCharts.js"></script>

<script>

var params = {

    title: { text: 'Solar Employment Growth by Sector, 2010-2016' },
    subtitle: { text: 'Source: thesolarfoundation.com' },
    yAxis: { title: { text: 'Number of Employees' } },
    legend: { layout: 'vertical', align: 'right', verticalAlign: 'middle' },
    plotOptions: {
        series: {
            pointStart: 2010
        }
    },

    series: [{
        name: 'Installation',
        data: [43934, 52503, 57177, 69658, 97031, 119931, 137133, 151175]
    }, {
        "name": "Manufacturing",
        "data": [24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434]
    }],
};

ReportMill.chart("container", params);

</script>

