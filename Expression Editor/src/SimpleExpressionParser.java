/**
 * Starter code to implement an ExpressionParser. Your parser methods should use the following grammar:
 * E := A | X
 * A := A+M | M
 * M := M*M | X
 * X := (E) | L
 * L := [0-9]+ | [a-z]
 */
public class SimpleExpressionParser implements ExpressionParser {
	
	/**
	 * Attempts to create an expression tree -- flattened as much as possible -- from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @param withJavaFXControls you can just ignore this variable for R1
	 * @return the Expression object representing the parsed expression tree
	 */
	public Expression parse (String str, boolean withJavaFXControls) throws ExpressionParseException {
		// Remove spaces -- this simplifies the parsing logic
		str = str.replaceAll(" ", "");
		Expression expression = parseExpression(str);
		if (expression == null) {
			// If we couldn't parse the string, then raise an error
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Flatten the expression before returning
		expression.flatten();
		
		return expression;
	}
	
	/**
	 * Check if the String is a number
	 * @param String for checking
	 * @return Boolean: True if the string is a number 0-9
	 */
	private boolean isNumber(String s) {
		for (char c : s.toCharArray()) {
			if (c < '0' || c > '9') return false;
		}
		
		return true;
	}
	
	/**
	 * Check if the String is a letter
	 * @param String for checking
	 * @return True if the string is a letter a-z
	 */
	private boolean isLetter(String s) {
		if (s.length() > 1 || s.length() == 0) return false;
		
		char c = s.toCharArray()[0];
		return (c >= 'a' && c <= 'z');
	}
	
	/**
	 * Verify that the string contains a literal
	 * @param String for checking
	 * @return True if the string contains a number 0-9 or letter a-z
	 */
	private boolean containsLiteral(String s) {
		for (char c : s.toCharArray()) {
			if (isNumber("" + c) || isLetter("" + c)) {
				return true;
			}
		}
		
		return false;
	}
	/**
	 * Parse the expression into a tree
	 * @param String for parsing
	 * @return parsed expression
	 * @throws ExpressionParseException
	 */
	protected Expression parseExpression (String str) throws ExpressionParseException {
		//Expression with no literals cannot be parsed
		if (!containsLiteral(str)) {
			throw new ExpressionParseException("Invalid Expression: " + str + " Contains no literals");
		}
		//Expressions that are literals end here
		if (isNumber(str) || isLetter(str)) {
		    return new LiteralExpression(str);
		} else {	//reduces complex expressions
			//if expression is wrapped in parenthesis, add a parenthetical expression, parse contents
			if (str.charAt(0) == '(' && str.charAt(str.length() - 1) == ')') {
				ParentheticalExpression expr = new ParentheticalExpression();
				String contents = str.substring(1, str.length() - 1);
				expr.addSubexpression(parseExpression(contents));
				return expr;
			}
			int operatorIndex = -1;
			int operatorDepth = Integer.MAX_VALUE;
			char operator = '+';
			int currDepth = 0;
			
			//locate lowest precedence operator 
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (c == '(') currDepth++;
				else if (c == ')') currDepth--;
				
				if (c == '+' && (currDepth < operatorDepth || (currDepth == operatorDepth && operator == '*'))) {
					operatorIndex = i;
					operatorDepth = currDepth;
					operator = '+';
				} else if (c == '*' && currDepth < operatorDepth) {
					operatorIndex = i;
					operatorDepth = currDepth;
					operator = '*';
				}
			}
			//initialize appropriate operator
			BaseCompoundExpression expr = null;
			if (operator == '+') {
				expr = new AdditiveExpression();
			} else {
				expr = new MultiplicativeExpression();
			}
			//split expression into parts before and after operator
			String before = str.substring(0, operatorIndex);
			String after = str.substring(operatorIndex + 1);
			//parse expressions on either side recursively.
			expr.addSubexpression(parseExpression(before));
			expr.addSubexpression(parseExpression(after));
			return expr;
		}
	}
}
