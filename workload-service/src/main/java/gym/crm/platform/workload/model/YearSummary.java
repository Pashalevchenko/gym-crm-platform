package gym.crm.platform.workload.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@AllArgsConstructor
public class YearSummary {

    @Field("year")
    private Integer year;

    @Field("months")
    private List<MonthSummary> months;
}
