package gym.crm.platform.workload.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummary {

    @Field("month")
    private Integer month;

    @Field("trainingSummaryDuration")
    private Integer trainingSummaryDuration;
}
