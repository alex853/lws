
Ext.define('ImprovedScenery.map.MarkerWindowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.marker-window-controller',

    marker: null,

    init: function () {
        var view = this.getView();
        this.marker = view.initialConfig.marker;

        var data = this.marker.data;

        view.setTitle(data.icao + ' ' + data.name);

        Ext.Ajax.request({
            url: 'service/map/scenery-info',
            method: 'POST',
            params: {
                ids: Ext.encode(data.ids)
            },
            timeout: 120000,
            success: function (xhr, opts) {
                var response = Ext.decode(xhr.responseText);
                if (!response.success) {
                    Ext.Msg.alert("Error on loading", xhr.responseText);
                    return;
                }

                Ext.each(response.data, function (sceneryInfoData) {
                    var panel = new ImprovedScenery.map.SceneryInfoPanel();
                    var vm = panel.getViewModel();
                    vm.setData(sceneryInfoData);
                    view.add(panel);
                });
            },
            failure: function (xhr, opts) {
                view.close();

                Ext.Msg.alert("Error on loading", xhr.responseText);
            }
        });
    }
});
