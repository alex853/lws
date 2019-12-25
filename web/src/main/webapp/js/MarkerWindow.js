
Ext.define('LightWeightedScenery.map.MarkerWindow', {
    extend: 'Ext.window.Window',
    controller: 'marker-window-controller',

    height: 300,
    width: 400,
    scrollable: true,
    bodyPadding: 10,
    constrain: true,
    closable: true,
    modal: true,
    
    layout: {
        type: 'vbox',
        pack: 'start',
        align: 'stretch'
    },

    defaults: {
        // frame: true
        // bodyPadding: 10
    },

    items: [
    ]
    
});
