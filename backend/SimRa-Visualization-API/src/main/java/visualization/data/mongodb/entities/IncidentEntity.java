package visualization.data.mongodb.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="incidents")
public class IncidentEntity {

    @Id
    private String id;
    private int rideId;
    private int key;
    private ArrayList map;
    private long timeStamp;


}
