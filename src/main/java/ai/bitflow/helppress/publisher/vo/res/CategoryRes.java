package ai.bitflow.helppress.publisher.vo.res;

import ai.bitflow.helppress.publisher.vo.res.result.CategoryResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@ToString
public class CategoryRes extends GeneralRes {
	
	private CategoryResult result;
	
}