public class ParentheticalExpression extends BaseCompoundExpression {
	@Override
	/**
	 * All AdditiveExpressions will consist of "()"
	 * @return "()", the value of the expression
	 */
	public String toString() {
		return "()";
	}
}
