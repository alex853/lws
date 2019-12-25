Ext.define('ImprovedScenery.map.SceneryInfoPanel', {
    extend: 'Ext.panel.Panel',

    viewModel: {
        type: 'scenery-info-model'
    },

    layout: 'border',

    width: '100%',
    height: 100,

    frame: true,

    items: [{
        html: 'image',
        region: 'west',
        width: 100
    }, {
        region: 'center',
        layout: 'border',
        items: [{
            region: 'north',
            height: '30%',
            layout: {
                type: 'hbox',
                align: 'end'
            },
            items: [{
                tpl: '<div style="font-size: 80%; font-style: italic; ">[Category or Group or Package]</div>',
                bind: {
                    data: {
                        // title: '{category}'
                    }
                }
            }]
        }, {
            region: 'center',
            height: '40%',
            layout: {
                type: 'hbox',
                align: 'center'
            },
            items: [{
                tpl: '<div style="font-size: 120%;">{title}    (rev. {revisionNumber})</div>',
                bind: {
                    data: {
                        title: '{title}',
                        revisionNumber: '{revisionNumber}'
                    }
                }
            }]
        }, {
            region: 'south',
            height: '30%',
            layout: {
                type: 'hbox',
                align: 'start'
            },
            items: [{
                tpl: '<div style="vertical-align: top; font-size: 80%; ">{authors}</div>',
                bind: {
                    data: {
                        authors: '{authors}'
                    }
                }
            }]
        }]
    }]
});
