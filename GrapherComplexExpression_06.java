
/*------ Olivier Karangwa, Daniel Willis,and Travus Helmly -----------*
 *       Group Project: Complex Expression-Graphing Calculator    *
 * 		                NCSU. Fall 2012                               *
 * 	`		                ECE 309				                      *
 *----------------------Due 27 NOV 2012-------------------------------*/

//This contains the constructor called by ComplexExpressionCalculator_06
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GrapherComplexExpression_06 extends JPanel implements MouseListener{
	private JFrame window= new JFrame();
	private JPanel popUpPanel=new JPanel();
	private JFrame popUpWindow=new JFrame();
	private JLabel coordinatesLabel=new JLabel();
	Line2D xAxis; 
	Line2D yAxis; 
	Stroke drawingStroke = new BasicStroke(2);
	
	private int frameWidth=400;
	private int frameHeight=450;
	private int margin=70;
	
	//xAxis
	private int xStartHoriz;
	private int xStopHoriz;  
	private int yOnxAxis;
	
	//yAxis
	private int yStartVert;
	private int yStopVert;
	private int xOnyAxis;  
	
	private double xAxisLength;
	private double yAxisLength;
	
	private int numberOfXs;
	private int numberOfExpressions;
	
	private ComplexExpressionCalculator_06 ComplexExpressionCalculator;
	
	private String function;
	private boolean functionIsNotConstant;
	
	private int xPixelRelative;
	private int yPixelRelative;
	private int clickedX;
	private int clickedY;
	//private float resultClicked;
	
	private double[] abscissae;  
	private String[] abscissaeStrings;
	private float[] ordinates;
	private String[] ordinatesStrings;
	
	private String displayableX;
	private String displayableY;
	    
	public GrapherComplexExpression_06(ComplexExpressionCalculator_06 cal,String expression, double [] xValues,String[] xValuesStrings, 
					  float[] expressionValues, String[] expressionValuesStrings, boolean xIsInExpression) 
	
	{	  
		   		
		ComplexExpressionCalculator=cal;
		abscissae=xValues;
		abscissaeStrings=xValuesStrings;
		ordinates=expressionValues;
		ordinatesStrings=expressionValuesStrings; //This won't be used because we need y values with increments.
												   //To get it, we use min and max expressionsValues received. 			
		numberOfXs=xValues.length;
		numberOfExpressions=expressionValues.length;
		
		function=expression;
		functionIsNotConstant=xIsInExpression;
		
		xStartHoriz=margin;
		xStopHoriz=frameWidth-margin+30; //+30 add some space after the last | on the x axis
		yOnxAxis=frameHeight-margin;
		xAxisLength=frameWidth-2*margin; //Here the axis length is independent of where its actual limits are
		
		yStartVert=margin-35; 			//-35 add some space after the last -- on the y axis
		yStopVert=frameHeight-margin;
		xOnyAxis=margin;	  
		yAxisLength=frameHeight-2*margin;  //Here the axis length is independent of where its actual limits are
				
		//this.setBackground(Color.RED); THIS AIN'T WORKING WHEN ENABLED. Don't know why
		xAxis = new Line2D.Double(xStartHoriz,yOnxAxis,xStopHoriz,yOnxAxis);
		yAxis = new Line2D.Double(xOnyAxis,yStartVert,xOnyAxis,yStopVert);
		window.getContentPane().add(this,"Center");
		window.setSize(frameWidth,frameHeight);
		window.setLocation(100,200);
		window.setTitle("Graph Window - Team 6 (ECE 309-Fall 2012)");
		window.setVisible(true);
		window.addMouseListener(this);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Closing this window won't turn off GraphingCalculator_06
	}
	
	public void paint(Graphics g) {
		Graphics2D graph = (Graphics2D)g;
		graph.setStroke(drawingStroke);		
		graph.setPaint(Color.red);
		graph.draw(xAxis);
		graph.setPaint(Color.BLACK);
		graph.drawString("X",xStopHoriz+10,yOnxAxis+4);
		graph.setPaint(Color.red);
		graph.draw(yAxis);		
		graph.setPaint(Color.BLACK);
		graph.drawString("Y",xOnyAxis-4,yStartVert-10); 
		graph.setColor(Color.BLACK);
		graph.drawString("Graph for y= "+function, 150, 50);
		graph.drawString("Hold down the mouse click to read a corresponding curve y-value ", 15, 12);
		
		int xBump=(int)xAxisLength/(numberOfXs-1);
		int xOffset=xStartHoriz; 
		int[] xOffsets=new int[numberOfXs]; 
		
		int yBump=(int)yAxisLength/(numberOfExpressions-1);
		int yOffset=yStopVert; 
		int[] yOffsets=new int[numberOfExpressions]; 
		
		float[] yScaleValues=new float[numberOfExpressions];
		String[] yScaleStrings= new String[numberOfExpressions];
		
		float smallestYvalue= ordinates[0];
		float biggestYvalue=  ordinates[0];
		
		for(int i=0; i<numberOfXs;i++)
		{
			xOffsets[i] = xOffset;
			graph.drawString("|", xOffset-2, yOnxAxis + 5);
			graph.drawString(abscissaeStrings[i], xOffset-5, yOnxAxis + 25);
			xOffset += xBump;
		}
		
		//Determinate the biggest and the smallest y value
		for(float yValue: ordinates)
		{
			if(yValue>biggestYvalue)
				biggestYvalue=yValue;
			if(yValue<smallestYvalue)
				smallestYvalue=yValue;
		}
		
		yScaleValues[0]=smallestYvalue;
		yScaleStrings[0]=String.valueOf(smallestYvalue);
		
		float yScaleIncrement=(biggestYvalue-smallestYvalue)/numberOfExpressions;
		
		for (int i = 1; i < yScaleValues.length; i++) //Start at [1] because [0] contains the smallest value
		{
			yScaleValues[i] = (yScaleValues[(i - 1)] + yScaleIncrement); 
			yScaleStrings[i] = String.format("%.01f",yScaleValues[i]); //To make the labels look neat by... 
																		//...limiting the values to 2 decimal points
		}
		
		for (int i = 0; i < numberOfExpressions; i++)
		{
			yOffsets[i] = yOffset;
			graph.drawString("--", xOnyAxis - 5, yOffset+2);
			
			if(!functionIsNotConstant)
				graph.drawString(yScaleStrings[0], 30, yOffset+2); //Label only one y coordinate 
			else{
			graph.drawString(yScaleStrings[i], 30, yOffset+2); //Label all applicable y coordinates 
			yOffset -= yBump;
			
			}
		}		
		//==================Drawing the curve======================
		int[] yPixelRelatives = new int[numberOfExpressions];
		double yValueRange = biggestYvalue - smallestYvalue;
		int yPixelRange = (int) yAxisLength;
				
		//Converting expression values into pixel values
		for (int i = 0; i < numberOfExpressions; i++)
		{
			double yValuePercent = (double)((ordinates[i] - smallestYvalue) / yValueRange);
			int yPixelAbsolute = (int)(yPixelRange * yValuePercent);
			yPixelRelatives[i] = (yStopVert - yPixelAbsolute);
		}		
		graph.setColor(Color.BLUE);
		for (int i = 0; i < yPixelRelatives.length; i++)
		{
			graph.drawOval(xOffsets[i] - 2, yPixelRelatives[i] - 2, 4, 4);
			
			if (i > 0)
				graph.drawLine(xOffsets[(i - 1)], yPixelRelatives[(i - 1)], 
						   xOffsets[i], yPixelRelatives[i]);
		}
				
	}
	
	
	public void mouseClicked(MouseEvent e) {		
		// TODO Auto-generated method stub
	}

	//===========When a mouse is pressed=======
	public void mousePressed(MouseEvent e) {
	
						
			clickedX=e.getX();
			clickedY=e.getY();
							
			xPixelRelative=clickedX-xStartHoriz;
			yPixelRelative=clickedY-margin;
			
			float resultClicked;
			
			//Determinate the biggest and the smallest x value
			double smallestXvalue=abscissae[0];
			double biggestXvalue=abscissae[0];
			
			//Converting pixel values into expression values
			for(double xValue: abscissae)
			{
				if(xValue>biggestXvalue)
					biggestXvalue=xValue;
				if(xValue<smallestXvalue)
					smallestXvalue=xValue;
			}
			
			double xValueRange = biggestXvalue-smallestXvalue;
			double xPixelRange =268; //=xAxisLength;		
					
			double xPixelPercent=(xPixelRelative-smallestXvalue)/xPixelRange;
			float xValueClicked=(float)(xValueRange*xPixelPercent);			
			
			try{
			   //Determine the corresponding y based on the given function and the x resulting from pixel-to-value conversion
			   resultClicked = ComplexExpressionCalculator.evaluateExpression(function,String.valueOf(xValueClicked)); 
				}
			 catch(IllegalArgumentException iae)
		       {
				  JOptionPane.showMessageDialog(this,
						    ""+iae.getMessage()+"",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				  return;
		       }
			
			//Limit decimal points
			displayableX=String.format("%.02f",xValueClicked);
			displayableY=String.format("%.02f",resultClicked);
					
		
		if(e.getSource()==window)
		{
		 if(clickedX>=2 && xPixelRelative<=270 && yPixelRelative>=28 && yPixelRelative<=340)
		 {
			 System.out.println("The mouse clicked at pixel x="+xPixelRelative+" based on xy reference. Originally it's at pixel x= "+e.getX());
			 System.out.println("The mouse clicked at pixel y="+yPixelRelative+" based on xy reference. Originally it's at pixel y= "+e.getY());
				
			coordinatesLabel.setText("For x="+displayableX+", y="+displayableY+" on the curve");
			popUpPanel.add(coordinatesLabel,"North");
			popUpWindow.getContentPane().add(popUpPanel,"Center");
			popUpWindow.setSize(300,70);
			popUpWindow.setLocation(100+clickedX,200+yPixelRelative);
			popUpWindow.setTitle("Figure Coordinates");
			popUpWindow.setVisible(true);
		  }
		 else
			 System.out.println("Clicked outside the prescribed range.");
		}
	}

	
	public void mouseReleased(MouseEvent e) {
		if(e.getSource()==window)
		{
		//Make the pop-up window invisible
		popUpWindow.setVisible(false);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
  
}