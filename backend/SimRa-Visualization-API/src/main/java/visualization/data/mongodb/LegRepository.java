package visualization.data.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.LegEntity;

public interface LegRepository extends MongoRepository<LegEntity, String>, LegRepositoryCustom {

}
