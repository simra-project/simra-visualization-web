package visualization.web.resources.serializers;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.stereotype.Component;
import visualization.data.mongodb.entities.IncidentEntity;
import visualization.data.mongodb.entities.LegEntity;
import visualization.web.resources.LegResource;
import visualization.web.resources.LegResourceProperty;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class LegResourceMapper {

    public List<LegResource> mapLegEntitiesToResourcesWithIncidents(List<LegEntity> legs, List<IncidentEntity> incidents) throws FactoryException {
        final GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(new Hints(Hints.CRS, DefaultGeographicCRS.WGS84));
        // Transforming coordinates into a different format? I really don't know... but this returns distances in meters (i think)
        final MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, CRS.decode("AUTO:42001,13.45,52.3"));

        List<Geometry> incidentPoints = incidents.stream().map(incident -> {
            try {
                return JTS.transform(factory.createPoint(new Coordinate(incident.getLocationMapMatched().getX(), incident.getLocationMapMatched().getY())), transform);
            } catch (TransformException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return legs.stream().map(leg -> mapLegEntityToResourceWithIncidents(leg, incidentPoints, factory, transform)).collect(Collectors.toList());
    }

    public LegResource mapLegEntityToResourceWithIncidents(LegEntity legEntity, List<Geometry> incidentPoints, GeometryFactory factory, MathTransform transform) {
        LegResource legResource = new LegResource();
        LegResourceProperty legResourceProperty = new LegResourceProperty();

        double incidentWeight = 0;
        try {
            Coordinate[] coordinates = legEntity.getGeometry().getCoordinates().stream().map(point -> new Coordinate(point.getX(), point.getY())).toArray(Coordinate[]::new);
            Geometry leg = JTS.transform(factory.createLineString(coordinates), transform);

            for (Geometry point : incidentPoints) {
                double distanceMeters = point.distance(leg);

                System.out.println(distanceMeters);
                incidentWeight += distanceMeters < 20 ? (20 - distanceMeters) / 20 : 0; // todo: optimize!!
            }
        } catch (TransformException e) {
            e.printStackTrace();
        }
        legResourceProperty.setIncidentCount(incidentWeight);
        legResourceProperty.setFileIdSet(legEntity.getProperties().getFileIdSet());

        legResource.setProperties(legResourceProperty);
        legResource.setType(legEntity.getType());
        legResource.setGeometry(legEntity.getGeometry());

        return legResource;
    }
}
