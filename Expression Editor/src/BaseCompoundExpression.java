import java.util.ArrayList;
import java.util.Collections;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public abstract class BaseCompoundExpression extends BaseExpression implements CompoundExpression {
	
	final private ArrayList<Expression> _subExpressions;
	final private ArrayList<Expression> _toRemove;
	final private ArrayList<Expression> _toAdd;
	final private static int FONT_SIZE = 20;
	
	public BaseCompoundExpression(){
		_subExpressions = new ArrayList<>();
		_toRemove = new ArrayList<>();
		_toAdd = new ArrayList<>();
	}
	
	@Override
	public void convertToString(StringBuilder stringBuilder, int indentLevel) {
		super.convertToString(stringBuilder, indentLevel);
		stringBuilder.append(this.toString());
		stringBuilder.append('\n');
		for (Expression e : getSubExpressions()) {
			e.convertToString(stringBuilder, indentLevel + 1);
			if (stringBuilder.charAt(stringBuilder.length() - 1) != '\n') {
				stringBuilder.append('\n');
			}
		}
	}
	
	@Override
	public void addSubexpression(Expression subExpression) {
		subExpression.setParent(this);
		_subExpressions.add(subExpression);
	}
	
	/**
	 * Switch the positions of two sub expressions within a compound expression
	 * @param subExpressionA
	 * @param subExpressionB
	 */
	public void swapSubexpression(Expression subExpressionA, Expression subExpressionB) {
		Collections.swap(_subExpressions, _subExpressions.indexOf(subExpressionA), _subExpressions.indexOf(subExpressionB));
	}
	
	/**
	 * Returns an ArrayList of all of this expression's sub-expressions.
	 * @return ArrayList<Expression>: of all of this expression's sub-expressions.
	 */
	private ArrayList<Expression> getSubExpressions() {
		return _subExpressions;
	}
	
	/**
	 * Add things to the list of objects marked for removal
	 * @param subExpression
	 */
	private void markForRemoval(Expression subExpression) {
		_toRemove.add(subExpression);
	}
	
	/**
	 * Add things to the list of objects marked for addition
	 * @param subExpression
	 */
	private void markForAddition(Expression subExpression) {
		_toAdd.add(subExpression);
	}
	
	@Override
	/**
	 * returns the an ExpressionHBox node containing this expression (with all relevant subexpressions).
	 * @return ExpressionHBox node.
	 */
	public Node getNode() {
		ExpressionHBox expr = new ExpressionHBox(0,this);
		ArrayList labels = new ArrayList<>();
		
		Label label;
		if(this.toString().equals("()")){
			label = new Label("(");
			label.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, FONT_SIZE));
			labels.add(label);
			//add each of the subexpressions to this compound expression
			for(Expression subExpression : _subExpressions) {
				labels.add(subExpression.getNode());
			}
			
			label = new Label(")");
			label.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, FONT_SIZE));
			labels.add(label);
		}
		else {
			for (int i = 0; i < _subExpressions.size(); i++) {
				labels.add(_subExpressions.get(i).getNode());
				if (i != _subExpressions.size() - 1)  {
					label = new Label(this.toString());
					label.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, FONT_SIZE));
					labels.add(label);
				}
			}
		}
		
		expr.getChildren().addAll(labels);
		return  expr;
	}
	
	@Override
	public void flatten() {
		
		//work up through the tree and flatten all subExpressions
		for (Expression subExpr : _subExpressions) {
			subExpr.flatten();
		}
		
		//add things queued for addition
		for (Expression subExpr : _toAdd) {
			_subExpressions.add(subExpr);
			subExpr.setParent(this);
		}
		_toAdd.clear();
		
		//remove things queued for removal
		for (Expression subExpr : _toRemove) {
			_subExpressions.remove(subExpr);
			subExpr.setParent(null);
		}
		_toRemove.clear();
		
		//if object has a parent and parent is of same expression type, flatten it and queue changes on parent
		if (this.getParent() != null && this.getParent().toString().equals(this.toString())) {
			((BaseCompoundExpression) this.getParent()).markForRemoval(this);
			for(Expression subExpr : _subExpressions) {
				((BaseCompoundExpression) this.getParent()).markForAddition(subExpr);
			}
		}
	}
	
	@Override
	/**
	 * create a new compoundExpression of the same type as the original, with all relevant subexpressions
	 */
	public Expression deepCopy() {
		BaseCompoundExpression copy;
		copy = null;
		if (this instanceof AdditiveExpression) {
			copy = new AdditiveExpression();
		}
		else if (this instanceof MultiplicativeExpression){
			copy = new MultiplicativeExpression();
		}
		else if (this instanceof ParentheticalExpression){
			copy = new ParentheticalExpression();		
		}
		for (Expression child : this.getSubExpressions()) {
			copy.addSubexpression(child.deepCopy());
		}
		return copy;
	}
}
