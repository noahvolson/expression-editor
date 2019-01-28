import javafx.application.Application;

import java.awt.Paint;
import java.util.*;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}
	
	/**
	 * Recursively set the opacity of a node and all of its children
	 * @param node
	 * @param opacity value (0-1)
	 */
	public static void setChildrenOpacity(Node node, double opacity) {
		if (node instanceof Label) {
			((Label) node).setOpacity(opacity);
		}
		else if (node instanceof HBox) {
			for(Node child : ((HBox) node).getChildren()) {
				setChildrenOpacity(child, opacity);
			}
		}
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
		ExpressionHBox _focus;
		ExpressionHBox _dragExpression;
		Pane _pane;
		boolean _dragging;
		CompoundExpression _rootExpression;
		
		
		MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {
			_pane = pane_;
			_rootExpression = rootExpression_;
			_focus = null;
			_dragging = false;
			_dragExpression = null;
		}
		
		//Clone an expression and create a node to follow cursor
		private void startDrag(){
			Expression clone = _focus.getExpression().deepCopy();
			_dragExpression = (ExpressionHBox) clone.getNode();
			_pane.getChildren().add(_dragExpression);
			setChildrenOpacity(_focus,.5);
			_dragging = true;
		}
		
		//Removes node following cursor from the pane
		private void endDrag() {
			setChildrenOpacity(_focus,1);
			_dragging = false;
			_pane.getChildren().remove(_dragExpression);
		}
		
		//Selects an expression Node at the given coordinates and set it as the focus (red box)
		private void selectFocus(double xPos, double yPos) {
			ExpressionHBox rootNode = (_focus == null) ? (ExpressionHBox) _pane.getChildren().get(0) : _focus;
			ExpressionHBox focusBefore = _focus;
			for (Node child : rootNode.getChildren()) {
				Bounds bounds = child.localToScene(child.getBoundsInLocal());
				bounds.contains(xPos, yPos);
				if (bounds.contains(xPos, yPos) && child instanceof ExpressionHBox) {
					if (_focus != null) ((Pane) _focus).setBorder(Border.EMPTY);
					((Pane) child).setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
					_focus = (ExpressionHBox) child;
					break;
				}
			}
			if (focusBefore == _focus && _focus != null) {
				((Pane) _focus).setBorder(Border.EMPTY);
				_focus = null;
			}
		}
		
		public void handle (MouseEvent event) {
			//position of the mouse cursor
			double xPos = event.getSceneX();
			double yPos = event.getSceneY();
			
			if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && _focus != null) {
				if (!_dragging) startDrag();
				//update the position of the expression being dragged to cursor's current position
				_dragExpression.setLayoutX(event.getX());
				_dragExpression.setLayoutY(event.getY());
				
				ExpressionHBox parent = (ExpressionHBox) _focus.getParent();
				List<Node> children = parent.getChildren();
				//find the node that the cursor is over 
				ExpressionHBox swap = _focus;
				for (int i = 0; i < children.size(); i++) {
					Node child = children.get(i);
					if (child instanceof Label) continue;
					Bounds bounds = child.localToScene(child.getBoundsInLocal());
					if (bounds.getMinX() < xPos && bounds.getMaxX() > xPos) swap = (ExpressionHBox) child;
				}
				if (swap != _focus) {	//if cursor is over a different node, swap it out
					ArrayList<Node> copy = new ArrayList<>(children);
					
					int from = copy.indexOf(_focus);
					int to = copy.indexOf(swap);
					
					Collections.swap(copy, from, to);
					children.clear();
					children.addAll(copy);
					
					((BaseCompoundExpression) parent.getExpression()).swapSubexpression(swap.getExpression(), _focus.getExpression());
					System.out.println(_rootExpression.convertToString(0));
				}
				
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				if (_dragging) endDrag();
				else {
					selectFocus(xPos,yPos);
				}
			}
		}
	}

	/**
	 * Size of the GUI
	 */
	private static final int WINDOW_WIDTH = 500, WINDOW_HEIGHT = 250;

	/**
	 * Initial expression shown in the textbox
	 */
	private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

	/**
	 * Parser used for parsing expressions.
	 */
	private final ExpressionParser expressionParser = new SimpleExpressionParser();

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Expression Editor");

		// Add the textbox and Parser button
		final Pane queryPane = new HBox();
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button button = new Button("Parse");
		queryPane.getChildren().add(textField);

		final Pane expressionPane = new Pane();

		// Add the callback to handle when the Parse button is pressed	
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle (MouseEvent e) {
				// Try to parse the expression
				try {
					// Success! Add the expression's Node to the expressionPane
					final Expression expression = expressionParser.parse(textField.getText(), true);
					expression.flatten();
					System.out.println(expression.convertToString(0));
					expressionPane.getChildren().clear();
					Node node = expression.getNode();
					expressionPane.getChildren().add(node);
					node.setLayoutX(WINDOW_WIDTH/4);
					node.setLayoutY(WINDOW_HEIGHT/2);

					// If the parsed expression is a CompoundExpression, then register some call backs
					if (expression instanceof CompoundExpression) {
						((Pane) expression.getNode()).setBorder(Expression.NO_BORDER);
						final MouseEventHandler eventHandler = new MouseEventHandler(expressionPane, (CompoundExpression) expression);
						expressionPane.setOnMousePressed(eventHandler);
						expressionPane.setOnMouseDragged(eventHandler);
						expressionPane.setOnMouseReleased(eventHandler);
					}
				} catch (ExpressionParseException epe) {
					// If we can't parse the expression, then mark it in red
					textField.setStyle("-fx-text-fill: red");
				}
			}
		});
		queryPane.getChildren().add(button);

		// Reset the color to black whenever the user presses a key
		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(expressionPane);

		primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		primaryStage.show();
	}
}
