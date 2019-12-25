
Ext.define('ImprovedScenery.map.SceneryInfoModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.scenery-info-model',

    data: {
    },

    formulas: {
        title: function (get) {
            return get('sceneryTitle');
        },

        authors: function (get) {
            return get('sceneryAuthors');
        }
    }
});
