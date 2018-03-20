<div id="pieChart" class="col-md-4"></div>

<script src="public/charts/website/libs/d3.min.js"></script>
<script src="public/charts/d3pie/d3pie.min.js"></script>
<script>

var dataJson = ${detailsDocJson};
		
var pie = new d3pie("pieChart", {
	"header": {
		"title": {
			"text": "Document Concepts Statistics",
			"fontSize": 24,
			"font": "open sans"
		},
		"subtitle": {
			"text": "% relevance of semanitc concepts in the selected document",
			"color": "#999999",
			"fontSize": 12,
			"font": "open sans"
		},
		"titleSubtitlePadding": 9
	},
	"footer": {
		"color": "#999999",
		"fontSize": 10,
		"font": "open sans",
		"location": "bottom-left"
	},
	"size": {
		"canvasWidth": 600,
		"pieOuterRadius": "70%"
	},
	"data": {
		"sortOrder": "value-desc",
		"content": dataJson
	},
	"labels": {
		"outer": {
			"pieDistance": 32
		},
		"inner": {
			"hideWhenLessThanPercentage": 3
		},
		"mainLabel": {
			"fontSize": 11
		},
		"percentage": {
			"color": "#ffffff",
			"decimalPlaces": 0
		},
		"value": {
			"color": "#adadad",
			"fontSize": 11
		},
		"lines": {
			"enabled": true
		},
		"truncation": {
			"enabled": true
		}
	},
	"effects": {
		"pullOutSegmentOnClick": {
			"effect": "linear",
			"speed": 400,
			"size": 8
		}
	},
	"misc": {
		"gradient": {
			"enabled": true,
			"percentage": 100
		}
	}
});
</script>