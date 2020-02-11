package visualization.web.resources.serializers;

import kotlin.Pair;
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
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LegResourceMapper {

    final double MAX_WEIGHT = 5;
    final double MAX_DISTANCE_METERS = 50;
    final double MULTIPLIER_LEG_LENGTH = 1;
    final double MULTIPLIER_SCARY_INCIDENT = 2.5;
    final double MULTIPLIER_INCIDENT_FROM_RIDE = 1.5;

    public List<LegResource> mapLegEntitiesToResourcesWithIncidents(List<LegEntity> legs, List<IncidentEntity> incidents) throws FactoryException {
        final GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(new Hints(Hints.CRS, DefaultGeographicCRS.WGS84));
        // Transforming coordinates into a different format? I really don't know... but this returns distances in meters (i think)
        final MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, CRS.decode("AUTO:42001,13.45,52.3"));

        List<Pair<IncidentEntity, Geometry>> incidentPoints = incidents.stream().map(incident -> {
            try {
                Geometry point = JTS.transform(factory.createPoint(new Coordinate(incident.getLocationMapMatched().getX(), incident.getLocationMapMatched().getY())), transform);
                return new Pair<>(incident, point);
            } catch (TransformException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return legs.stream().map(leg -> mapLegEntityToResourceWithIncidents(leg, incidentPoints, factory, transform)).collect(Collectors.toList());
    }

    public LegResource mapLegEntityToResourceWithIncidents(LegEntity legEntity, List<Pair<IncidentEntity, Geometry>> incidentPoints, GeometryFactory factory, MathTransform transform) {
        LegResource legResource = new LegResource();
        LegResourceProperty legResourceProperty = new LegResourceProperty();
        Set<String> fileIdSet = legEntity.getProperties().getFileIdSet();

        double incidentWeights = 0;
        try {
            Coordinate[] coordinates = legEntity.getGeometry().getCoordinates().stream().map(point -> new Coordinate(point.getX(), point.getY())).toArray(Coordinate[]::new);
            Geometry leg = JTS.transform(factory.createLineString(coordinates), transform);

            for (Pair<IncidentEntity, Geometry> incident : incidentPoints) {
                double distanceMeters = incident.getSecond().distance(leg);

                System.out.println(distanceMeters);
                if (distanceMeters >= MAX_DISTANCE_METERS) continue;

                double weight = 1 - (distanceMeters / MAX_DISTANCE_METERS); // todo: optimize!!
                if (incident.getFirst().getScary()) weight *= MULTIPLIER_SCARY_INCIDENT;
                if (fileIdSet.contains(incident.getFirst().getFileId())) weight *= MULTIPLIER_INCIDENT_FROM_RIDE;

                incidentWeights += weight;
            }

            incidentWeights /= Math.log(leg.getLength()) * MULTIPLIER_LEG_LENGTH;
            incidentWeights = Math.min(incidentWeights, MAX_WEIGHT);
        } catch (TransformException e) {
            e.printStackTrace();
        }
        legResourceProperty.setIncidentCount(incidentWeights);
        legResourceProperty.setFileIdSet(fileIdSet);

        legResource.setProperties(legResourceProperty);
        legResource.setType(legEntity.getType());
        legResource.setGeometry(legEntity.getGeometry());

        return legResource;
    }
}
