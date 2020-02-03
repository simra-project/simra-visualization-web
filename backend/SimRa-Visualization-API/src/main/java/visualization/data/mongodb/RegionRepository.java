package visualization.data.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.RegionEntity;

import java.util.List;

public interface RegionRepository extends MongoRepository<RegionEntity, String> {

    List<RegionEntity> findAll();

}
