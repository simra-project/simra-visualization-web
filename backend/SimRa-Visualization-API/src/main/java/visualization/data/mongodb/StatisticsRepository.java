package visualization.data.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import visualization.data.mongodb.entities.StatisticsEntity;

import java.util.Optional;


public interface StatisticsRepository extends MongoRepository<StatisticsEntity, String> {

    Optional<StatisticsEntity> findByRegion(String region);

}
