
Ext.define('LightWeightedScenery.controller.GMapController', {
    extend: 'Ext.app.Controller',

    control: {
        '#gmappanel': {
            render: 'onMapRendered'
        }
    },

    init: function() {
    },

    onMapRendered: function () {
        Ext.Ajax.request({
            url: 'service/map/markers',
            method: 'GET',
            params: {
            },
            timeout: 120000,
            success: function (xhr, opts) {
                var response = Ext.decode(xhr.responseText);
                if (!response.success) {
                    Ext.Msg.alert("Error on loading", xhr.responseText);
                    return;
                }

                var gmappanel = Ext.getCmp('gmappanel');
                var gmap = gmappanel.gmap;
                
                var markers = {};
                Ext.each(response.data, function (markerData) {
                    var marker = new LightWeightedScenery.map.Marker({
                        data: markerData,
                        map: gmap
                    });
                    marker.refresh();
                });
            },
            failure: function (xhr, opts) {
                Ext.Msg.alert("Error on loading", xhr.responseText);
            }
        });
    }
});
