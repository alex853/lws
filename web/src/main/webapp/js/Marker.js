
Ext.define('ImprovedScenery.map.Marker', {
    data: null,
    map: null,
    marker: null,
    mouseOverMarker: false,

    constructor: function(config) {
        this.data = config.data;
        this.map = config.map;
    },

    refresh: function() {
        var showMarker = true;

        if (showMarker) {
            if (!this.marker) {
                this.marker = new MarkerWithLabel({
                    labelContent: this.data.icao + ' ' + this.data.name,
                    labelVisible: false
                });

                var that = this;
                this.marker.addListener('click', function () {
                    that.onMarkerClick();
                });
                this.marker.addListener('mouseover', function () {
                    that.onMarkerMouseOver();
                });
                this.marker.addListener('mouseout', function () {
                    that.onMarkerMouseOut();
                });
            }

            var markerPosition = this.marker.getPosition();
            if (markerPosition == undefined
                || this.isDiff(markerPosition.lat(), this.data.lat)
                || this.isDiff(markerPosition.lng(), this.data.lon)) {
                this.marker.setPosition({
                    lat: this.data.lat,
                    lng: this.data.lon
                });
            }

            // todo AK check if it is changed
            this.marker.setIcon(this.getIcon(/*status, this.pilotPosition.heading*/));

            if (!this.marker.getMap()) {
                this.marker.setMap(this.map);
            }
        } else {
            if (this.marker && this.marker.getMap()) {
                this.marker.setMap(null);
            }
        }

        this.marker.labelVisible = this.mouseOverMarker;
        this.marker.setVisible(this.marker.visible);

    },

    remove: function() {
        this.removeMapObject('marker');
    },

    removeMapObject: function(mapObjectName) {
        var mapObject = this[mapObjectName];
        if (mapObject) {
            mapObject.setMap(null);
            delete this[mapObjectName];
            this[mapObjectName] = null;
        }
    },

    getIcon: function (/*status, heading*/) {
        return {
            url: 'images/fatcow/16/star.png',
            size: new google.maps.Size(16, 16),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(8, 8)
        };
    },

    onMarkerClick: function() {
        var window = new ImprovedScenery.map.MarkerWindow({
            marker: this
        });
        window.show();
    },

    onMarkerMouseOver: function() {
        if (!this.mouseOverMarker) {
            this.mouseOverMarker = true;
            this.refresh();
        }
    },

    onMarkerMouseOut: function() {
        this.mouseOverMarker = false;
        this.refresh();
    },

    isDiff: function (float1, float2) {
        var diff = float1 - float2;
        if (diff < 0) {
            diff = -diff;
        }
        return diff >= 0.000001;
    }
});
