
/*------ Olivier Karangwa, Daniel Willis,and Travus Helmly -----------*
 *       Group Project: Complex Expression-Graphing Calculator    *
 * 		                NCSU. Fall 2012                               *
 * 	`		                ECE 309				                      *
 *----------------------Due 27 NOV 2012-------------------------------*/

//This program is a final version of the whole calculator project. It ensures that
//"complex" expressions (multi-operator expressions) such as ((2*pi)^(x+1)) are calculated correctly 
//in Expression and Graphing modes

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.cbrt;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;

public class ComplexExpressionCalculator_06 implements ActionListener, ItemListener
{
// STATIC ----------------------------------------------------------------------------	
	public static void main(String[] args)
	{
	new ComplexExpressionCalculator_06();
	}

// OBJECT ----------------------------------------------------------------------------	
private boolean      isInAccumulatorMode     = false;
private boolean      isInExpressionMode      = false;
private boolean      isInGraphingMode		 =true;
	
private JFrame       window                = new JFrame("Complex Expression Calculator Program by Team 6 (ECE 309-Fall 2012)");
private JButton      clearButton           = new JButton("Clear");
private JButton      logButton             = new JButton("Log Selected Text from History");

private JTextField   totalTextField       = new JTextField(8);
private JLabel       totalLabel           = new JLabel("Result");

private JTextField   expressionTextField   = new JTextField(20);
private JTextArea    logTextArea           = new JTextArea(20,1); //The arguments makes the scroll bar appear from the start.
private JScrollPane  logScrollPane         = new JScrollPane(logTextArea);

private JLabel       xValueLabel           = new JLabel("from x = ", SwingConstants.RIGHT);
private JTextField   xValueTextField       	= new JTextField(3);

private JLabel 			toXLabel = new JLabel("to x = ", 4);
private JTextField  	toXTextField = new JTextField(3);
private JLabel 			xIncrementLabel = new JLabel("with increments of x = ", 4);
private JTextField 		xIncrementTextField = new JTextField(3); 

private JCheckBox		xIncrementCheckBox   =new JCheckBox("Provide x Increment?");

private JTextArea    instructionTextArea	= new JTextArea();
private JScrollPane  instructionScrollPane  = new JScrollPane(instructionTextArea);
private JTextField 		 noteTextField	    = new JTextField("ECE 309. NCSU.Fall 2012");
private JTextField     demoTextField		= new JTextField(20);


private JRadioButton accumulatorRadioButton= new JRadioButton("Accumulator",  false);
private JRadioButton expressionRadioButton = new JRadioButton("Expression",   false);
private JRadioButton graphingRadioButton = new JRadioButton("Graphing",   true);
private ButtonGroup  radioButtonGroup      = new ButtonGroup();

private JPanel      upRegionPanel				=new JPanel(new GridLayout(2,1));
private JPanel      downRegionPanel				=new JPanel(new GridLayout(2,1));
private JPanel      estRegionPanel				=new JPanel(new GridLayout(1,2));

private float        previousResult;
private int 		 counter=1;
private String       newLine      = System.getProperty("line.separator");
private char         defaultOperator       = '+';

private float xValue = 0.0f;

private boolean      xIsInExpression  	= false;
private boolean      toXSpecified  		=false;
private boolean      incrementSpecified =false;
private boolean      unavailableExpandExpression;

private double[] xValues			= new double[11];
private String[] xValuesStrings		=new String[xValues.length];
private float[] expressionValues  =new float[xValues.length];
private String[] expressionValuesStrings  =new String[xValues.length];
private String   enteredExpression;

public ComplexExpressionCalculator_06() 
	{
	System.out.println("Olivier Karangwa, Daniel Willis, and Travus Helmly--Team 06");
	System.out.println("*************************************************************");
	System.out.println("Complex Expression Calculator Program ");
	System.out.println("*************************************************************");
	

	JPanel topPanel = new JPanel();
	topPanel.add(totalLabel);
	topPanel.add(totalTextField);
	topPanel.add(clearButton);
	topPanel.add(expressionTextField);
	topPanel.add(xValueLabel);
	topPanel.add(xValueTextField);	
	topPanel.add(toXLabel);
	topPanel.add(toXTextField);
	topPanel.add(xIncrementLabel);
	topPanel.add(xIncrementTextField);
	topPanel.add(xIncrementCheckBox);
		     
    JPanel bottomPanel = new JPanel();
    bottomPanel.add(logButton);
		
	JPanel rightPanel=new JPanel(new GridLayout(2,1));
	rightPanel.add(instructionScrollPane);
	rightPanel.add(demoTextField);	
	
	JPanel buttonPanel=new JPanel();
	buttonPanel.setBorder(BorderFactory.createTitledBorder("Mode"));
	buttonPanel.add(accumulatorRadioButton);
	buttonPanel.add(expressionRadioButton);
	buttonPanel.add(graphingRadioButton);
	
	upRegionPanel.add(buttonPanel);
	upRegionPanel.add(topPanel);
	        
    downRegionPanel.add(bottomPanel);
    downRegionPanel.add(noteTextField);
    
    estRegionPanel.add(logScrollPane);
    estRegionPanel.add(rightPanel);
		
    window.getContentPane().add(downRegionPanel, "South");
    window.getContentPane().add(upRegionPanel,"North");
    window.getContentPane().add(estRegionPanel, "Center");
    
    
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setSize(1100,600);
    window.setLocation(80,50);
    
    totalTextField.setText("0.0");
    totalTextField.setEditable(false);
    
    xValueTextField.setText("0.0");
    xValueTextField.setEditable(true); //to match the default for the default button
    
    toXTextField.setText("0.0");
    toXTextField.setEditable(true);//to match the default for the default button
    
    xIncrementTextField.setText("1.0");
    xIncrementTextField.setEditable(true); //to match the default for the default button
    
    //Defaults when the window is opened
    incrementSpecified=false;
    toXSpecified=true;
    xIncrementTextField.setEditable(false); //Disable this input
    toXTextField.setEditable(true);   //Enable this input
    xIncrementCheckBox.setSelected(false);//Uncheck the checkBox (whether it's checked or not you just do it to make sure)
    
    instructionTextArea.setEditable(false);
    instructionTextArea.setFont(new Font("TimesRoman",Font.BOLD,15));
    
    instructionTextArea.setText("");
    instructionTextArea.append("INSTRUCTIONS");
    instructionTextArea.append(newLine);
    instructionTextArea.append(newLine+"*This is Graphing Mode."); 
    instructionTextArea.append(newLine+"*Enter the expression to graph [same rules as Expression Mode's apply].");
    instructionTextArea.append(newLine+"*Press enter to show the graph.");
    instructionTextArea.append(newLine+"*You can specify x range or an increment of x.");
    instructionTextArea.append(newLine+"*Only a limited number of points will be generated.");
    instructionTextArea.append(newLine+"*WARNING: Unlike in History logs, numbers labelled on the ordinate axis are generated "+newLine+" " +
    		"independently from user specified/suggested x increments.");
    instructionTextArea.append(newLine+"*Start comments with //.");
    instructionTextArea.append(newLine+"*When dividing or multiplying, parentheses must surround x,e, and pi.");
    instructionTextArea.append(newLine+"*You have to provide a complete mathematical expression. e.g. (3*(e)+4)*(x).");
    instructionTextArea.append(newLine+"*Log file: CalLog.txt. ");
       
    noteTextField.setEditable(false);
    noteTextField.setFont(new Font("Tahoma",Font.PLAIN,10));
    noteTextField.setHorizontalAlignment(JTextField.CENTER);
    
    demoTextField.setEditable(false);
    demoTextField.setText("Demo: Type x+2>>Hit Enter: a graph is generated");
    demoTextField.setFont(new Font("Tahoma",Font.BOLD,12));
    demoTextField.setHorizontalAlignment(JTextField.LEFT);
    
    logTextArea.setText("History");
    logTextArea.setEditable(false);
    logTextArea.setFont(new Font("Courier New",Font.PLAIN,15));
         
    expressionTextField.setFont(new Font("default",Font.BOLD,15));
    expressionTextField.requestFocus(); // set cursor in
    
    //Get event notifications
    expressionTextField.addActionListener(this); // give our address to GUI objects
    clearButton.addActionListener(this);
    logButton.addActionListener(this);
    xValueTextField.addActionListener(this);
    toXTextField.addActionListener(this);
    xIncrementTextField.addActionListener(this);
    accumulatorRadioButton.addActionListener(this);
    expressionRadioButton.addActionListener(this);
    graphingRadioButton.addActionListener(this);
    radioButtonGroup.add(accumulatorRadioButton);
    radioButtonGroup.add(expressionRadioButton);
    radioButtonGroup.add(graphingRadioButton);
    
    xIncrementCheckBox.addItemListener(this);
       
    window.setVisible(true);
	}

// GUI 
public void actionPerformed(ActionEvent ae) // button & text fields call here
	{
    
    if (ae.getSource() == accumulatorRadioButton)
    {
    xValueLabel.setText("from x = ");
    isInAccumulatorMode = true;
    isInExpressionMode  = false;
    isInGraphingMode=false;
    
    //Don't need these inputs yet:
    xValueTextField.setEditable(false); 
    toXTextField.setEditable(false);
	xIncrementTextField.setEditable(false);
	xIncrementCheckBox.setSelected(false);//Uncheck the checkBox (whether it's checked or not you just do it to make sure)
    
    demoTextField.setText("Demo: Type 5>>Hit Enter: Total is 5");
    
    instructionTextArea.setText("");
    instructionTextArea.append("INSTRUCTIONS");  
    instructionTextArea.append(newLine);
    instructionTextArea.append(newLine+"*This is Accumulator Mode" +newLine+ "*Start comments with //.");
    instructionTextArea.append(newLine+"*Perfomed operations in this mode: addition");
    instructionTextArea.append(newLine+"*No need for any operator; just one real number (positive or negative) as operand");
    instructionTextArea.append(newLine+"*The operator and the other operand are embedded in Total");
    instructionTextArea.append(newLine+"*Log file: CalLog.txt. ");
    return;
    }
 
 if (ae.getSource() == expressionRadioButton)
    {
	
	xValueLabel.setText("such that x = ");
	isInAccumulatorMode = false;
	isInExpressionMode  = true;
	isInGraphingMode    =false;
	xValueTextField.setEditable(true); //Now, a user can input an x value in this mode
	toXTextField.setEditable(false);   //But not here
	xIncrementTextField.setEditable(false); //and here
	xIncrementCheckBox.setSelected(false);//Uncheck the checkBox (whether it's checked or not you just do it to make sure)
	
    demoTextField.setText("Demo: Type 5+2>>Hit Enter: Total is 7");
    
    instructionTextArea.setText("");
    instructionTextArea.append("INSTRUCTIONS");
    instructionTextArea.append(newLine);
    instructionTextArea.append(newLine+"*This is Expression Mode" +newLine+ "*Start comments with //");
    instructionTextArea.append(newLine+"*Operands: real numbers, x, e, and pi");
    instructionTextArea.append(newLine+"*When dividing or multiplying, parentheses must surround x,e, and pi.");
    instructionTextArea.append(newLine+"*You have to provide a complete mathematical expression. e.g. (3*(e)+4)*(x)");
    instructionTextArea.append(newLine+"*Set value for x as needed");
    instructionTextArea.append(newLine+"*If no paranthesis used, operator precedence is from left to right.");
    instructionTextArea.append(newLine+"*Log file: CalLog.txt. ");
    
    return;
    }
    
    if (ae.getSource()==graphingRadioButton)
    {
    
	isInAccumulatorMode = false;
	isInExpressionMode  = false;
	isInGraphingMode    =true;
	xValueLabel.setText("from x = ");
	xValueTextField.setEditable(true); //Input an x value in this mode of course 
	
	//Initial states
	incrementSpecified=false;
    toXSpecified=true;
	xIncrementTextField.setEditable(false); //Disable this input
    toXTextField.setEditable(true);   //Enable this input
    xIncrementCheckBox.setSelected(false);//Uncheck the checkBox (whether it's checked or not you just do it to make sure)
    
    demoTextField.setText("Demo: Type x+2>>Hit Enter: a graph is generated");
    
    instructionTextArea.setText("");
    instructionTextArea.append("INSTRUCTIONS");
    instructionTextArea.append(newLine);
    instructionTextArea.append(newLine+"*This is Graphing Mode."); 
    instructionTextArea.append(newLine+"*Enter the expression to graph [same rules as Expression Mode's apply].");
    instructionTextArea.append(newLine+"*Press enter to show the graph.");
    instructionTextArea.append(newLine+"*You can specify x range or an increment of x.");
    instructionTextArea.append(newLine+"*Only a limited number of points will be generated.");
    instructionTextArea.append(newLine+"*WARNING: Unlike in History logs, numbers labelled on the ordinate axis are generated "+newLine+" " +
    		"independently from user specified/suggested x increments.");
    instructionTextArea.append(newLine+"*Start comments with //.");
    instructionTextArea.append(newLine+"*When dividing or multiplying, parentheses must surround x,e, and pi.");
    instructionTextArea.append(newLine+"*You have to provide a complete mathematical expression. e.g. (3*(e)+4)*(x).");
    instructionTextArea.append(newLine+"*Log file: CalLog.txt. ");
    return;
    }
    
	if (ae.getSource() == clearButton)
	   {
	   expressionTextField.setText("");
	   xValueTextField.setText("  0.0  ");
	   previousResult = 0.0f;
	   totalTextField.setText("  0.0  ");
	   expressionTextField.requestFocus(); // allow cursor in
	   return; 
	   }
 
	if (ae.getSource() == logButton)
	   {
	   String selectedText = logTextArea.getSelectedText();
	   System.out.println(selectedText);
	   if ((selectedText == null) || (selectedText.length() == 0))
	      {
		 
		  JOptionPane.showMessageDialog(window,
				    "No text was selected to put in logs!",
				    "No Selection Error",
				    JOptionPane.ERROR_MESSAGE);

		  return;
	      }
	   
	   try {
		   BufferedWriter bw = new BufferedWriter(new FileWriter("CalLog.txt",true));
		   bw.write("__________________________________________________");
		   bw.newLine();
		   bw.write(new Date().toString());// append date & time in saved log
		   bw.newLine();
		   bw.write(selectedText);
		   bw.newLine();
		   bw.close();
		   }
	   catch (IOException e)
	       {		   
			  JOptionPane.showMessageDialog(window,
					    "Sorry, an error occured when attempting to write log.",
					    "Log Writting Error",
					    JOptionPane.ERROR_MESSAGE);
		   return;
	       }
	   expressionTextField.requestFocus(); // allow cursor in
	   return;
	   }
	
	if (ae.getSource() == expressionTextField) // for present comments 
	   {
	   String expression = expressionTextField.getText().trim();
	   if (expression.startsWith("//"))
	      {
		  logTextArea.append(newLine + expression);
		  expressionTextField.setText("");
		  return;
	      }
	   }
	
	if ((ae.getSource() == expressionTextField) || (ae.getSource() == xValueTextField)
			|| (ae.getSource() ==toXTextField)|| (ae.getSource() ==xIncrementTextField))
	   {
	   float  result   = 0.0f;
	   String toXString="";
	   double toXDouble=0.0;
	   String incrementString="";
	   double incrementDouble=0.0;
	   float yValue   =0.0f;
	   String xValueString="";
       xIsInExpression = false; 
	   try { 
		   if (isInAccumulatorMode)
		      {
		      defaultOperator = ' ';
	          result = evaluateExpression(expressionTextField.getText(), previousResult, xValueTextField.getText());
              totalTextField.setText(String.valueOf(result));
              logTextArea.append(newLine+"("+counter++ +")"); //order  operation times
              logTextArea.append(String.valueOf(previousResult) + " " + defaultOperator + " " + expressionTextField.getText() + " = "   + totalTextField.getText());
		      }
		   if (isInExpressionMode)
		      {
			  unavailableExpandExpression = true;
		      result = evaluateExpression(expressionTextField.getText(),
                                              xValueTextField.getText());
		      totalTextField.setText(String.valueOf(result));
		      logTextArea.append(newLine+"("+counter++ +")");//order  operation times
			  logTextArea.append(expressionTextField.getText()
					                    + " = " +  totalTextField.getText());
		      }
		   
		   if(isInGraphingMode)
		   {
			    xValueString=xValueTextField.getText().trim();
			    xValue=Float.parseFloat(xValueString);
			    				
			    //==========Check the increment============
			    incrementString = xIncrementTextField.getText().trim();
		        if(incrementString.length() == 0)
		             incrementSpecified = false;
		        //Continue
		        if(incrementSpecified)
		        {
			        try
			        {
			            incrementDouble = Double.parseDouble(incrementString);
			            if(incrementDouble ==0.0)
			            {
				        	JOptionPane.showMessageDialog(window,
			 					    "The increment is zero.",
			 					    "Error",
			 					    JOptionPane.ERROR_MESSAGE);
				            return;
			            }
			            if(incrementDouble==(double)xValue)
			            {
			            	JOptionPane.showMessageDialog(window,
			 					    "The increment is equal to x.",
			 					    "Error",
			 					    JOptionPane.ERROR_MESSAGE);
				            return;
			            }
			            
			        }
			        catch(NumberFormatException nfe)
			        {
			        	JOptionPane.showMessageDialog(window,
		 					    "The increment isn't numeric.",
		 					    "Error",
		 					    JOptionPane.ERROR_MESSAGE);
			            return;
			        }		        
		        }
		        //==============Check 'toX'================
		        toXString = toXTextField.getText().trim();
		        if(toXString.length()==0)
		           toXSpecified = false;
		        //Continue
		        if(toXSpecified)
		        {
			        try
			        {
			            toXDouble = Double.parseDouble(toXString);
			            if(toXDouble ==0.0)
			             {
			            	JOptionPane.showMessageDialog(window,
			 					    "The limit 'to x' is zero.",
			 					    "Error",
			 					    JOptionPane.ERROR_MESSAGE);
				            return;
			             }
			            if(toXDouble==(double)xValue)
			            {
			            	JOptionPane.showMessageDialog(window,
			 					    "The 'to x' is equal to x.",
			 					    "Error",
			 					    JOptionPane.ERROR_MESSAGE);
				            return;
			            }
			            if(toXDouble<(double)xValue)
			            {
			            	JOptionPane.showMessageDialog(window,
			 					    "The 'to x' is less than x.",
			 					    "Error",
			 					    JOptionPane.ERROR_MESSAGE);
				            return;
			            }
			           		          
			        }
			        catch(NumberFormatException nfe)
			        {
			        	JOptionPane.showMessageDialog(window,
		 					    "The limit 'to x' isn't numeric.",
		 					    "Error",
		 					    JOptionPane.ERROR_MESSAGE);
			            return;
			        }		        
		        }
		               
		        
		        //=======Use the input values===================
		        makeXValues((double) xValue,toXDouble,incrementDouble); //Call this method (see down the page)
		        
		        for(int i = 0; i < xValues.length; i++)
			        {
			        yValue=evaluateExpression(expressionTextField.getText(),
	                        xValuesStrings[i]);//Calculate y values
			        expressionValues[i]=yValue; // and put them in an array
			        }
		        
		        for(int i=0; i<xValues.length;i++)
		        	{
		        	expressionValuesStrings[i]=String.format("%.02f",expressionValues[i]);
		        	//expressionValuesStrings[i] = stripTrailingZeros(expressionValuesStrings[i]);//To make it look more neat on the display
		        	//expressionValuesStrings[i]=Double.toString(Double.valueOf(twoDPlaces.format(expressionValues[i])));//Round>>In Double>>From Double to String
		        	}		        

		        System.out.println("y values: "+Arrays.toString(expressionValues));
		        System.out.println("y values strings: "+Arrays.toString(expressionValuesStrings));
		        
		        logTextArea.append(newLine+"("+counter++ +")"+"Plot points for "+expressionTextField.getText()+":  ");//order  operation times
		        for(int i=0; i<xValues.length;i++)
		         {		         
				  logTextArea.append(newLine+"   ("+(String.format("%.02f", xValues[i]))+" , "+(String.format("%.02f", expressionValues[i]))+")");
				 
		         }
		              
		        //**********CALL THE GRAPHER CONSTRUCTOR (in a separate file)**********
		        new GrapherComplexExpression_06(this,expressionTextField.getText(), xValues,xValuesStrings, expressionValues, expressionValuesStrings, xIsInExpression);
	
		       
		   }
	       if (xIsInExpression && !isInGraphingMode) 
	    	   logTextArea.append(" such that x = " + xValueTextField.getText());//Don't do this in Graphing mode
	        else
	           expressionTextField.setText(""); // clear input area & set cursor
           logTextArea.setCaretPosition(logTextArea.getDocument().getLength()); // scroll to bottom		     
           previousResult = result;
 	       }
	   catch(IllegalArgumentException iae)
	       {
			  JOptionPane.showMessageDialog(window,
					    ""+iae.getMessage()+"",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);			  
	       }
	   }
	}

//Evaluate for the Accumulator mode
public float evaluateExpression(String expression, float previousResult, String x)
                                throws IllegalArgumentException
   {
   float xValue          = 0.0f;	
   float operand         = 0.0f;
   float expressionValue = 0.0f;   
   char  operator        = ' ';   
   
   if (expression == null) 
	   throw new IllegalArgumentException("Expression is null.");
   expression = expression.trim(); 
   if (expression.length() == 0)
	   throw new IllegalArgumentException("Expression is blank.");   
   
   if (x == null); 
    else
      {
      x = x.trim(); 
      if (x.length() == 0); 
       else 
         {
    	 if (x.equalsIgnoreCase("e"))        xValue = (float)E;
    	  else if (x.equalsIgnoreCase("-e")) xValue = (float)-E;
    	  else if (x.equalsIgnoreCase("pi")) xValue = (float)PI;
    	  else if (x.equalsIgnoreCase("-pi"))xValue = (float)-PI;
    	  else // x is now a number
    	     {
    		 try {
    			 xValue = Float.parseFloat(x);
    		     }
    		 catch(NumberFormatException nfe)
    		     {
    			 throw new IllegalArgumentException("x is not a real number.");
    		     }
    	     }
         }
      }

   try  {
	    expressionValue = Float.parseFloat(expression);
	    defaultOperator = '+'; 
	    return previousResult + expressionValue; 
        }
   catch(NumberFormatException nfe)
        {   
        }
   
   operator = expression.charAt(0); 
   String operandString = expression.substring(1).trim(); 
   if (operandString.length() == 0)
	   throw new IllegalArgumentException("Legal operator or legal operand not found ");
   
   if (operandString.equalsIgnoreCase("e"))          operand = (float)E;
      else if (operandString.equalsIgnoreCase("-e")) operand = (float)-E;
	  else if (operandString.equalsIgnoreCase("pi")) operand = (float)PI;
	  else if (operandString.equalsIgnoreCase("-pi"))operand = (float)-PI;
	  else if (operandString.equalsIgnoreCase("x"))
	          {
     	      xIsInExpression = true;           
     	      operand = xValue;
	          }
	  else if (operandString.equalsIgnoreCase("-x"))
	          {
     	      xIsInExpression = true;           
     	      operand = -xValue;
              }                                       
	  else // now operand is a real number 
	     {
		 try {
			 operand = Float.parseFloat(operandString);
		     }
		 catch(NumberFormatException nfe)
		     {
			 throw new IllegalArgumentException("In Accumulator mode, only one and real number operand is allowed."+newLine+
			 		"In Calculator mode, operand must be a number, x, e, or pi only.");
		     }
	     }
   
   switch(operator)
         {
         case '+': return previousResult + operand;
         case '-': return previousResult - operand;
         case '*': return previousResult * operand;
         case '/': return previousResult / operand;
         case '^': return (float) pow(previousResult, operand);
         case 'r': 
         case 'R': return (float) pow(previousResult, (float)(1.0/operand));
         default:  throw new IllegalArgumentException("Using an operator in Accumulator mode"+newLine+ " Or operator is not '+','-', '*', '/', 'r', or '^'");
         }
   }

//Evaluate for the Expression mode or the Graphing mode
public float evaluateExpression(String expression, String x)
                                throws IllegalArgumentException
   {
  
   if (expression == null) 
       throw new IllegalArgumentException("Null expression");
   expression = expression.trim(); 
   if (expression.length() == 0)
       throw new IllegalArgumentException("Blank expression");
   if (expression.endsWith("=")) 
	   expression = expression.substring(0,expression.length()-1).trim();
   if (expression.contains("R")) 
	   expression.replace("R", "r");
   if (expression.contains("X")) 
	   expression.replace("X", "x");
   if (expression.contains("E")) 
	   expression.replace("E", "e");
   if (expression.contains("PI")) 
	   expression.replace("PI", "pi");      
   	
   if (x == null); 
    else
      {
      x = x.trim(); 
      if (x.length() == 0); 
       else 
         {
         if (x.equalsIgnoreCase("e"))  xValue = (float)E;
    else if (x.equalsIgnoreCase("-e")) xValue = (float)-E;
    else if (x.equalsIgnoreCase("pi")) xValue = (float)PI;
    else if (x.equalsIgnoreCase("-pi"))xValue = (float)-PI;
    else  
            {
            try {
                xValue = Float.parseFloat(x);
                }
            catch(NumberFormatException nfe)
                {
                throw new IllegalArgumentException("x is not a real number.");
                }
            }
         }
      }

   if (unavailableExpandExpression)
      {
      if (expression.contains("^")) 
	      expression = expandExpression(expression,'^');
      if (expression.contains("r")) 
	      expression = expandExpression(expression,'r');
      if (expression.contains("*")) 
	      expression = expandExpression(expression,'*');
      if (expression.contains("/")) 
	      expression = expandExpression(expression,'/');
      unavailableExpandExpression = false;
      }
 
   if (expression.contains("(") || expression.contains(")"))
       expression = reduceExpression(expression,x); 
   
   int operatorOffset = findNextOperator(expression);
   char operator = expression.charAt(operatorOffset);
   String  leftOperandString = expression.substring(0,operatorOffset).trim(); 
   expression = expression.substring(operatorOffset+1).trim();
   if (expression.length()==0) 
	   throw new IllegalArgumentException("Second operand missing.");
   
   float   leftOperandValue =  convertOperand(leftOperandString, xValue);
   
   float   rightOperandValue = 0;
   try { 
	   rightOperandValue = convertOperand(expression, xValue);
	     
       float result = evaluateExpression(leftOperandValue,operator,rightOperandValue);
       
       return result;
       }
   catch (NumberFormatException nfe)
       { }
     
   while(true) 
        {     
        operatorOffset = findNextOperator(expression);
        String rightOperandString = expression.substring(0,operatorOffset).trim();
        rightOperandValue = convertOperand(rightOperandString, xValue);
        
        leftOperandValue  = evaluateExpression(leftOperandValue, operator, rightOperandValue);
       
        operator = expression.charAt(operatorOffset);
        expression = expression.substring(operatorOffset+1).trim();
        enteredExpression=expression;
        
        try { 
	        rightOperandValue = convertOperand(expression, xValue);	          
            float result = evaluateExpression(leftOperandValue,operator,rightOperandValue);
            return result;
            }
        catch (NumberFormatException nfe)
            { 
            } 
        }
   }

private int findNextOperator(String expression) throws IllegalArgumentException
   {
  
   int     i;
   boolean expressionStartsWithUnary;
   if (expression.startsWith("-"))
      {
	  i = 1;
	  expressionStartsWithUnary = true;
      }
    else
      {
	  i = 0;
	  expressionStartsWithUnary = false;
      }
   for (; i < expression.length(); i++)
       {
       if ((expression.charAt(i) == '+')	
        || (expression.charAt(i) == '-')	   
        || (expression.charAt(i) == '*')	   
        || (expression.charAt(i) == '/')	   
        || (expression.charAt(i) == '^')	   
        || (expression.charAt(i) == 'r')	   
        || (expression.charAt(i) == 'R'))	   
            break;
       }
   if (expression.startsWith("- "))
	   throw new IllegalArgumentException("No blank allowed before '-'.");
   if (((i == 0) && !expressionStartsWithUnary)
	|| ((i == 1) &&  expressionStartsWithUnary))   
       throw new IllegalArgumentException("Operand missing");
   if (i == expression.length() - 1)
       throw new IllegalArgumentException("No operator allowed at the end of an expression.");
   if (i == expression.length())
       throw new IllegalArgumentException("Unrecognized operator" + expression);
   return i;
   }

private float convertOperand(String operand, float xValue) throws NumberFormatException
   {
   if (operand.contains("x") || operand.contains("X")) 
	   xIsInExpression = true;	
   
   if (operand.equalsIgnoreCase("e"))  return (float)E;
   if (operand.equalsIgnoreCase("-e")) return (float)-E;
   if (operand.equalsIgnoreCase("pi")) return (float)PI;
   if (operand.equalsIgnoreCase("-pi"))return (float)-PI;
   if (operand.equalsIgnoreCase("x"))  return xValue;
   if (operand.equalsIgnoreCase("-x")) return -xValue;
   
   try {
       return Float.parseFloat(operand);
       }
   catch(NumberFormatException nfe)
       {
       throw new NumberFormatException("Operand '" + operand + "' is not a real number");
       }
   }

//General Evaluation
private float evaluateExpression(float leftOperand, char operator, float rightOperand)
                                       throws IllegalArgumentException
   {
   switch(operator)
      {
      case '+': return leftOperand + rightOperand;
      case '-': return leftOperand - rightOperand;
      case '*': return leftOperand * rightOperand;
      case '/': return leftOperand / rightOperand;
      case '^': return (float) pow(leftOperand, rightOperand);
      case 'R': 
      case 'r': return (float) pow(leftOperand, (float)(1.0/rightOperand));

      default:  throw new IllegalArgumentException("Operator is not '+','-', '*', '/', 'r', or '^'");
      }
   }

private String reduceExpression(String expression, String x)
                                throws IllegalArgumentException
   {

   while (expression.contains("(") || expression.contains(")")) 
         {
	     int i           = 0;
         int leftOffset  = 0;
         int leftCount   = 0;
         int rightOffset = 0;
	     int rightCount  = 0;
         for (; i < expression.length(); i++)
         {
    	 if (expression.charAt(i) == '(') leftCount++;
    	 if (expression.charAt(i) == ')') rightCount++;
         }
	     if (leftCount != rightCount)
	    	 throw new IllegalArgumentException("Umbalanced parantheses.");
         for (i=0; i < expression.length(); i++)
             {
        	 if (expression.charAt(i) == '(')  leftOffset  = i; // replaces inner '('
        	 if (expression.charAt(i) == ')') {rightOffset = i; break;} // 1st ')'
             }
         if ((i == expression.length()) || (rightOffset < leftOffset))
        	 throw new IllegalArgumentException("Right-left parenthesis unmatch.");
         String innerExpression = expression.substring(leftOffset+1,i) + " + 0 ";
         
         float innerExpressionValue = evaluateExpression(innerExpression, x);
         expression = expression.substring(0,leftOffset)   + " "
                    + String.valueOf(innerExpressionValue) + " "
                    + expression.substring(rightOffset+1);
         
         }
   return expression + "+0";	
   }

private String expandExpression(String expression, char operator)
               throws IllegalArgumentException
  {
  int startingAt = 0;	
  while (expression.contains(String.valueOf(operator)))
        {
	    int operatorOffset = expression.indexOf(operator, startingAt);
	    if (operatorOffset <0)
	    	break; 
	    if ((operatorOffset < 1) || (operatorOffset > expression.length() -2))
	         throw new IllegalArgumentException("Cannot begin with this type of operator: " + operator);	    

	    String firstPart = expression.substring(0,operatorOffset).trim(); // before operator
	    String lastPart  = expression.substring(operatorOffset+1).trim(); // after operator
	    int afterRightOperand      = 2;  
	    int beginningOfLeftOperand = firstPart.length() - 1;   
	    boolean lookingForEnd     = true;
	    boolean lookingForStart = true;
	    
	    if ((lastPart.charAt(0) == '+')
		  ||(lastPart.charAt(0) == '*')
		  ||(lastPart.charAt(0) == '/')
		  ||(lastPart.charAt(0) == '^')
		  ||(lastPart.charAt(0) == 'r')
		  ||(lastPart.charAt(0) == 'R'))
             throw new IllegalArgumentException("operator " + lastPart.charAt(0) + " adjacent to operator " + operator);

	    if (lastPart.startsWith("e") 
		 || lastPart.startsWith("E") 
		 || lastPart.startsWith("x") 
		 || lastPart.startsWith("X"))
	    	lookingForEnd = false;
	    
	    if (lastPart.startsWith("-e") 
  		 || lastPart.startsWith("-E") 
  		 || lastPart.startsWith("-x") 
   		 || lastPart.startsWith("-X")
   		 || lastPart.startsWith("pi")
   		 || lastPart.startsWith("PI"))
	        {
		    afterRightOperand++;
	    	lookingForEnd = false;
	        }
	    
	    if (lastPart.startsWith("-pi") 
	   	 || lastPart.startsWith("-PI")) 
            {
		    afterRightOperand += 2;
	    	lookingForEnd = false;
	        }
	    
	    if (lastPart.startsWith(")"))	    
	       throw new IllegalArgumentException("Operator " + operator + " followed by right parenthesis.");
	    
	    if (lastPart.startsWith("(")) 
	       {
	       int leftParenthesesCount  = 1;
	       int rightParenthesesCount = 0;
	       int i;
	       for (i = 1; i < lastPart.length(); i++)
	           {
	    	   if (lastPart.charAt(i) == '(') leftParenthesesCount++;
	    	   if (lastPart.charAt(i) == ')')
	    	      {
	    		  rightParenthesesCount++;
	    		  if (leftParenthesesCount == rightParenthesesCount) break;
	    	      }
	           }
	       if (i == lastPart.length())
	    	   throw new IllegalArgumentException("Unmatched parentheses following opertor " + operator);
           afterRightOperand = i+1;	       
           lookingForEnd = false;
	       }
	    
	    if(lookingForEnd)
          {
		 	
	      char nextChar = lastPart.charAt(0);
    	  switch(nextChar)
	        {
	        case '-':  
	        case '.':
	        case '0':
	        case '1':
	        case '2':
	        case '3':
	        case '4':
	        case '5':
	        case '6':
	        case '7':
	        case '8':
	        case '9': break; //for all the above
	        default : throw new IllegalArgumentException("Invalid character " + nextChar + " following operator " + operator);
	        } // end of switch
    	  
    	  int i;
    	  for (i = 1; i < lastPart.length(); i++)
    	      {
    		  if ((lastPart.charAt(i) != '.')
        	   && (lastPart.charAt(i) != '0')
        	   && (lastPart.charAt(i) != '1')
        	   && (lastPart.charAt(i) != '2')
        	   && (lastPart.charAt(i) != '3')
        	   && (lastPart.charAt(i) != '4')
        	   && (lastPart.charAt(i) != '5')
        	   && (lastPart.charAt(i) != '6')
        	   && (lastPart.charAt(i) != '7')
        	   && (lastPart.charAt(i) != '8')
        	   && (lastPart.charAt(i) != '9'))
                   break;
    	      }
    	  afterRightOperand = i;
          }	 
	    String newLastPart = String.valueOf(operator) + lastPart.substring(0,afterRightOperand) + ")"; 
	    if (afterRightOperand < lastPart.length())
	    	newLastPart += lastPart.substring(afterRightOperand);	    
 
	    if ((firstPart.charAt(firstPart.length()-1) == '-')
	  	  ||(firstPart.charAt(firstPart.length()-1) == '+')
    	  ||(firstPart.charAt(firstPart.length()-1) == '*')
	  	  ||(firstPart.charAt(firstPart.length()-1) == '/')
	  	  ||(firstPart.charAt(firstPart.length()-1) == '^')
	  	  ||(firstPart.charAt(firstPart.length()-1) == 'r')
	  	  ||(firstPart.charAt(firstPart.length()-1) == 'R'))
	         throw new IllegalArgumentException("operator " + firstPart.charAt(firstPart.length()-1) + " adjacent to operator " + operator);

	    if (firstPart.endsWith("-pi") 
	   	 || firstPart.endsWith("-PI")) 
	        {
	   		beginningOfLeftOperand -= 2;
	   	    lookingForStart = false;
	   	    }

   	    if (lookingForStart 
   	 	 && (firstPart.endsWith("-e") 
   	 	  || firstPart.endsWith("-E") 
   	 	  || firstPart.endsWith("-x") 
   	 	  || firstPart.endsWith("-X")
   	 	  || firstPart.endsWith("pi")
   	 	  || firstPart.endsWith("PI")))
   	 	     {
   	 	     beginningOfLeftOperand --;
   	 	     lookingForStart = false;
   	 		 }
   	    		
   	    if (lookingForStart 
   	 	 && (firstPart.endsWith("e") 
   	   	  || firstPart.endsWith("E") 
   	   	  || firstPart.endsWith("x") 
   	   	  || firstPart.endsWith("X")))
  	 	     lookingForStart = false;

 	    if (firstPart.endsWith("("))	    
 	       throw new IllegalArgumentException("Operator " + operator + " preceded by a left parenthesis.");
 	    
 	    if (firstPart.endsWith(")")) 
 	       {
 	       int leftParenthesesCount  = 0;
 	       int rightParenthesesCount = 1;
 	       int i;
 	       for (i = firstPart.length()-2; i >= 0; i--)
 	           {
 	    	   if (firstPart.charAt(i) == ')') rightParenthesesCount++;
 	    	   if (firstPart.charAt(i) == '(')
 	    	      {
 	    		  leftParenthesesCount++;
 	    		  if (leftParenthesesCount == rightParenthesesCount) break;
 	    	      }
 	           }
 	       if (i < 0)
 	    	   throw new IllegalArgumentException("Unmatched parentheses preceding opertor " + operator);
           beginningOfLeftOperand = i;	       
           lookingForStart = false;
 	       }

	    if(lookingForStart)
        {
	   	
	    char previousChar = firstPart.charAt(firstPart.length()-1);
  	    switch(previousChar)
	        {
	        case '0': 
	        case '1':
	        case '2':
	        case '3':
	        case '4':
	        case '5':
	        case '6':
	        case '7':
	        case '8':
	        case '9': break; 
	        default : throw new IllegalArgumentException("Invalid character " + previousChar + " precedes operator " + operator);
	        } // end of switch
  	  
  	  int i;
  	  for (i = firstPart.length() -2; i >= 0 ; i--)
  	      {
  		  if ((firstPart.charAt(i) != '.')
     	   && (firstPart.charAt(i) != '-')
      	   && (firstPart.charAt(i) != '0')
      	   && (firstPart.charAt(i) != '1')
      	   && (firstPart.charAt(i) != '2')
      	   && (firstPart.charAt(i) != '3')
      	   && (firstPart.charAt(i) != '4')
      	   && (firstPart.charAt(i) != '5')
      	   && (firstPart.charAt(i) != '6')
      	   && (firstPart.charAt(i) != '7')
      	   && (firstPart.charAt(i) != '8')
      	   && (firstPart.charAt(i) != '9'))
               break;
  	      }
  	  if (i < 0)  
  	      beginningOfLeftOperand = 0;
  	   else 
  	      beginningOfLeftOperand = i+1;
      }	  
	    
	  String newFirstPart = "(" + firstPart.substring(beginningOfLeftOperand,operatorOffset); 
	  if (beginningOfLeftOperand > 0)
	      newFirstPart = firstPart.substring(0,beginningOfLeftOperand) + newFirstPart;
	    
      startingAt = newFirstPart.length() + 1;
      expression = newFirstPart + newLastPart;
      
      }
  
  return expression;
  }

//Make x values
private void makeXValues(double forX, double toX, double increment)
{
	double xIncrement= 0.0D;
	if(toXSpecified)
	{
		xIncrement=(toX-forX)/10D; //We choose ten values
		System.out.println((new StringBuilder("for x = ")).append(forX).append(" to x = ").append(toX).append(" with a suggested increment of ").append(xIncrement).toString());
	    
		xValues[0]=forX;
	   	for(int i=1; i<10;i++)//You start with [1] so that you can refer to a prior index [0]
	    {
	    	xValues[i]=xValues[i-1]+xIncrement;	    	
	    }
	}
	if(incrementSpecified)
	{
		xIncrement=increment;
		System.out.println((new StringBuilder("for x = ")).append(forX).append(" to x = ").append(forX + increment * 10D).append(" with a provided increment of ").append(increment).toString());
	    
		xValues[0]=forX;	    
		for(int i=1; i<10;i++)//You start with [1] so that you can refer to a prior index [0]
	    {
	    	xValues[i]=xValues[i-1]+xIncrement;	    	
	    }		
	}
	
	//Getting x values strings in the array
	xValuesStrings[0]=Float.toString(xValue);//To occupy the "null" space on [0]
	xValuesStrings[0] = stripTrailingZeros(xValuesStrings[0]);
	for(int i=1; i<xValues.length;i++) //You start with [1] so that you can refer to a prior index [0]
		{
			xValues[i]=xValues[i-1]+xIncrement; 
					
			//Get String conversion of xValues
			BigDecimal xValuesBD= new BigDecimal(xValues[i], MathContext.DECIMAL32);
			xValuesBD=xValuesBD.setScale(7,1);
			String xValueString = xValuesBD.toPlainString();
	        xValueString = stripTrailingZeros(xValueString);//To make it look more neat on the display
	        xValuesStrings[i] = xValueString;         
        }    	
		
	System.out.println("x values: "+Arrays.toString(xValues));
	System.out.println("x values strings: "+Arrays.toString(xValuesStrings));
}

public static String stripTrailingZeros(String number)
{
    int decimalPointOffset = number.indexOf(".");
    if(decimalPointOffset < 0)
        return number;
    for(; number.endsWith("0"); number = number.substring(0, number.length() - 1));
    if(number.endsWith("."))
        number = number.substring(0, number.length() - 1);
    return number;
}

public String limitToTwoDecimalPlaces(String theInput)
{
	int decimalIndex=theInput.indexOf('.'); //locating a period
	String theOutput=theInput.substring(decimalIndex, decimalIndex+2);
	return theOutput;
}

//Control what the check box does
public void itemStateChanged(ItemEvent e) 
{
	
	if(e.getSource()==xIncrementCheckBox)
	{
		if(xIncrementCheckBox.isSelected())
		{
			incrementSpecified=true;
		    toXSpecified=false;
			xIncrementTextField.setEditable(true); //Enable this input
            toXTextField.setEditable(false);   //Disable this input
		}
		else
		{
			incrementSpecified=false;
		    toXSpecified=true;
			xIncrementTextField.setEditable(false); //Disable this input
            toXTextField.setEditable(true);   //Enable this input
		}
	}
	
}


}//THE END

