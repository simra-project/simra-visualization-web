package visualization.web.resources.serializers;

import org.springframework.stereotype.Component;
import visualization.data.mongodb.entities.LegEntity;
import visualization.web.resources.LegResource;
import visualization.web.resources.LegResourceProperty;

@Component
public class LegResourceMapper {

    public LegResource mapLegEntityToResource(LegEntity legEntity) {

        LegResource legResource = new LegResource();
        LegResourceProperty legResourceProperty = new LegResourceProperty();

        legResourceProperty.setFileIdSet(legEntity.getProperties().getFileIdSet());
        legResourceProperty.setWeekday(legEntity.getProperties().getWeekday());

        legResource.setType(legEntity.getType());
        legResource.setGeometry(legEntity.getGeometry());

        legResource.setProperties(legResourceProperty);

        return legResource;
    }
}
