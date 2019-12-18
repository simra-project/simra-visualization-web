package visualization.data.mongodb.entities;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LegPropertyEntity {

    private Set<String> fileIdSet;

}
