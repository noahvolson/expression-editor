import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Node;

interface Expression {
	/**
	 * Border for showing a focused expression
	 */
	public static final Border RED_BORDER = new Border(
	  new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
	);

	/**
	 * Border for showing a non-focused expression
	 */
	public static final Border NO_BORDER = null;

	/**
	 * Color used for a "ghosted" expression
	 */
	public static final Color GHOST_COLOR = Color.LIGHTGREY;

	/**
	 * Returns the expression's parent.
	 * @return the expression's parent
	 */
	CompoundExpression getParent ();
        
	/**
         * Sets the parent be the specified expression.
         * @param parent the CompoundExpression that should be the parent of the target object
         */
	void setParent (CompoundExpression parent);

	/**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
	Expression deepCopy ();


	/**
	 * Returns the JavaFX node associated with this expression.
	 * @return the JavaFX node associated with this expression.
	 */
	Node getNode ();

	/**
	 * Recursively flattens the expression as much as possible
	 * throughout the entire tree. Specifically, in every multiplicative
	 * or additive expression x whose first or last
	 * child c is of the same type as x, the children of c will be added to x, and
	 * c itself will be removed. This method modifies the expression itself.
	 */
	void flatten ();

	/**
	 * Creates a String representation by recursively printing out (using indentation) the
	 * tree represented by this expression, starting at the specified indentation level.
	 * @param stringBuilder the StringBuilder to use for building the String representation
	 * @param indentLevel the indentation level (number of tabs from the left margin) at which to start
	 */	
	void convertToString (StringBuilder stringBuilder, int indentLevel);

	public default String convertToString (int indentLevel) {
		final StringBuilder stringBuilder = new StringBuilder();
		convertToString(stringBuilder, indentLevel);
		return stringBuilder.toString();
	}

	/**
	 * Static helper method to indent a specified number of times from the left margin, by
	 * appending tab characters to the specified StringBuilder.
	 * @param sb the StringBuilder to which to append tab characters.
	 * @param indentLevel the number of tabs to append.
	 */
	public static void indent (StringBuilder stringBuilder, int indentLevel) {
		for (int i = 0; i < indentLevel; i++) {
			stringBuilder.append('\t');
		}
	}
}
