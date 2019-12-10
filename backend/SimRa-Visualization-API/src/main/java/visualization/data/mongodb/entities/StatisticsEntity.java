package visualization.data.mongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "statistics")
public class StatisticsEntity {

    @Id
    private String id;

    private String region;

    // TBD : add db fields
}
