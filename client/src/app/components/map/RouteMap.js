import React from "react";
import { MapContainer, TileLayer, Polyline } from "react-leaflet";
import "leaflet/dist/leaflet.css";


delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});

export const RouteMap = (width, height, polyline, bounds) => {
    const dimensions = {
        width: `${width}`,
        height: `${height}`
    };
    
    return (
        <div>
            {this.props.polyline &&
                <MapContainer style={dimensions} 
                    center={position} 
                    zoom={zoom}
                    bounds={this.props.polyline.getBounds(bounds)}>
                    <TileLayer // http://{s}.tile.osm.org/{z}/{x}/{y}.png free simple tiles
                        url='https://tile.thunderforest.com/cycle/{z}/{x}/{y}.png?apikey=bf035ff674364bd0b3894f41d665642f' // thunderforest.com
                        attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                    />
                    {polyline &&
                        <Polyline 
                            color={polyline.options.color} 
                            positions={polyline.getLatLngs()}
                        />
                    }
                </MapContainer>
            }
        </div>
    )
}
