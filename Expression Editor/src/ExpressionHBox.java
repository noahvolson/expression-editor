import javafx.scene.layout.HBox;

/**
 * This class was created to store the expression contained in the HBox and have the same functionality as a HBox.
 */
public class ExpressionHBox extends HBox {
	private Expression _expression;
	
	public ExpressionHBox(int spacing, Expression expression) {
		super(spacing);
		_expression = expression;
	}
	/**
	 * Returns the expression of the HBox
	 * @return expression of the HBox
	 */
	public Expression getExpression() {
		return _expression;
	}
}
