
Ext.application({
    name : 'LightWeightedScenery',
    
    controllers: [
        'GMapController'
    ],
    
    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout: 'fit',
            renderTo: Ext.getBody(),
            items: [
                {
                    layout: 'border',
                    tbar: [
                        {
                            xtype: 'tbtext',
                            text: 'LightWeighted Scenery',
                            width: 200
                        }/*,


                         {
                         id: 'vatsim-toggle-button',
                         xtype: 'button',
                         enableToggle: true,
                         pressed: true,
                         text: 'VATSIM',
                         width: 100,
                         handler: null//Logics.networkToggleButtonHandler
                         },
                         {
                         id: 'vatsim-status-text',
                         xtype: 'tbtext',
                         text: '',
                         width: 300
                         },


                         {
                         id: 'ivao-toggle-button',
                         xtype: 'button',
                         enableToggle: true,
                         pressed: true,
                         text: 'IVAO',
                         width: 100,
                         handler: null//Logics.networkToggleButtonHandler
                         },
                         {
                         id: 'ivao-status-text',
                         xtype: 'tbtext',
                         text: '',
                         width: 300
                         }*/
                    ],
                    items: [
                        {
                            id: 'gmappanel',
                            xtype: 'gmappanel',
                            region: 'center',
                            center: {
                                lat: 20,
                                lng: 0
                            },
                            mapOptions: {
                                zoom: 2,
                                mapTypeId: google.maps.MapTypeId.ROADMAP,
                                styles: GMapStylesArray
                            }
                        }
                    ]
                }
            ]
        });
    }
});
