package visualization.web.resources.serializers;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.stereotype.Component;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.data.mongodb.entities.LegEntity;
import visualization.web.resources.LegResource;
import visualization.web.resources.LegResourceProperty;

import java.util.List;

@Component
public class LegResourceMapper {

    public LegResource mapLegEntityToResourceWithIncidents(LegEntity legEntity, List<IncidentEntity> incidents)  {

        LegResource legResource = new LegResource();
        LegResourceProperty legResourceProperty = new LegResourceProperty();

        legResourceProperty.setFileIdSet(legEntity.getProperties().getFileIdSet());

        double incidentWeight = 0;

        try {
            Hints hints = new Hints(Hints.CRS, DefaultGeographicCRS.WGS84);
            GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(hints);

            // Transforming coordinates into a different format? I really don't know... but this returns distances in meters (i think)
            MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, CRS.decode("AUTO:42001,13.45,52.3"));

            Coordinate[] coords = new Coordinate[legEntity.getGeometry().getCoordinates().size()]; // todo: messy
            for (int i = 0; i < legEntity.getGeometry().getCoordinates().size(); i++) {
                coords[i] = new Coordinate(legEntity.getGeometry().getCoordinates().get(i).getX(), legEntity.getGeometry().getCoordinates().get(i).getY());
            }
            Geometry leg = JTS.transform(factory.createLineString(coords), transform);

            for (IncidentEntity incident : incidents) {
                Geometry point = JTS.transform(factory.createPoint(new Coordinate(incident.getLocationMapMatched().getX(), incident.getLocationMapMatched().getY())), transform);
                double distanceMeters = point.distance(leg);

                System.out.println(distanceMeters);
                incidentWeight += distanceMeters < 20 ? (20 - distanceMeters) / 20 : 0; // todo: optimize!!
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        legResourceProperty.setIncidentCount(incidentWeight);

        legResource.setType(legEntity.getType());
        legResource.setGeometry(legEntity.getGeometry());

        legResource.setProperties(legResourceProperty);

        return legResource;
    }
}
