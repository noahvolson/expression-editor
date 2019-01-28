abstract public class BaseExpression implements Expression {

	private CompoundExpression _parent;
	
	@Override
	public CompoundExpression getParent() {
		return _parent;
	}

	@Override
	public void setParent(CompoundExpression parent) {
		_parent = parent;
		
	}

	@Override
	/**
	 * no flattening needs to occur if the expression is a literal.
	 * this method is overridden by one in BaseCompoundExpression for compound expressions. 
	 */
	public void flatten() {
	}

	@Override
	public void convertToString(StringBuilder stringBuilder, int indentLevel) {
		for (int i = 0; i < indentLevel; i++) {
			stringBuilder.append('\t');	
		}
	}
}
