import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class LiteralExpression extends BaseExpression{
	String _literal;
	
	public LiteralExpression(String literal) {
		_literal = literal;
	}
	
	@Override
	public void convertToString(StringBuilder stringBuilder, int indentLevel) {
		super.convertToString(stringBuilder, indentLevel);
		stringBuilder.append(_literal);
	}
	
	@Override
	/**
	 * returns an ExpressionHBox with a single label containing this literal
	 * @return an ExpressionHBox with a single label containing this literal
	 */
	public Node getNode() {
		Label label = new Label(_literal);
		label.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 20));
		
		ExpressionHBox hbox = new ExpressionHBox(0,this);
		hbox.getChildren().add(label);
		
		return hbox;
	}
	
	@Override
	/**
	 * Creates a new literal expression with the same _literal value as the original
	 */
	public Expression deepCopy() {
		return new LiteralExpression(_literal);
	}
}
