jQuery(document).ready(function($) {
  var graph_url = '/api/v1/namespaces/istio-system/services/servicegraph:http/proxy/graph';

  var sankey_options = {
    animation: {
      startup: true,
      duration: 100,
      easing: 'out'
    },
    sankey: {
      link: {
        color: {
          fill: '#D4EAF6'
        }
      },
      node: {
        colors: [ '#a6cee3' ],
        width: 12,
        label: {
          fontSize: 12,
          color: '#4C84A2'
        }
      }
    }
  };

  function setup() {
  }

  function refresh() {
    var chart = new google.visualization.Sankey(document.getElementById('sankey'));
    var request = $.getJSON(graph_url, function(data) {
      if (!data || !data.edges) return;
      
      var table = new google.visualization.DataTable();

      table.addColumn('string', 'Source');
      table.addColumn('string', 'Destination');
      table.addColumn('number', 'RPS');

      var rows = data.edges
        .filter(e => !e.source.startsWith('unknown'))
        .filter(e => !isNaN(e.labels['reqs/sec']))
				.map(function(e) {
					return [e.source, e.target, Number(e.labels['reqs/sec'])];
				})
        .filter(e => e[2] > 0);
      console.log(rows);
      table.addRows(rows);

      chart.draw(table, sankey_options);
    });

    $.when(request).then(function() {
      setTimeout(refresh, 1000);
    });
  }

  google.charts.load('current', {'packages':['sankey']});
  google.charts.setOnLoadCallback(refresh);
  setup();
});
