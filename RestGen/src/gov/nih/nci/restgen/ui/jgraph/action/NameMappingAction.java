/*L
 *  Copyright Ekagra Software Technologies Ltd.
 *  Copyright SAIC, SAIC-Frederick
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/cacore-sdk/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caCore SDK Software License (the "License").  
 * A copy of the License is available at:
 * [caCore SDK CVS home directory]\etc\license\caCore SDK_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caCore SDK/indexContent
 * /docs/caCore SDK_License
 */


package gov.nih.nci.restgen.ui.jgraph.action;


import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;


import gov.nih.nci.restgen.ui.actions.DefaultAbstractJgraphAction;
import gov.nih.nci.restgen.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.restgen.ui.main.MainFrame;
import gov.nih.nci.restgen.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.restgen.ui.tree.DefaultSourceTreeNode;
import gov.nih.nci.restgen.ui.tree.DefaultTargetTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class defines the action to delete selected graphic cells.
 * @author Prakash Vinjamuri
 * @author Prakash Vinjamuri LAST UPDATE
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2013-01-11
 */
public class NameMappingAction extends DefaultAbstractJgraphAction
{
	private static final String COMMAND_NAME = "Name Mapping";
	private static final Character COMMAND_MNEMONIC = new Character('D');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NameMappingAction(MiddlePanelJGraphController controller)
	{
		this(null, controller);
	}

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NameMappingAction(MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(COMMAND_NAME, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public NameMappingAction(String name, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(name, null, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public NameMappingAction(String name, Icon icon, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		super(name, icon, middlePanel, controller);
		setMnemonic(COMMAND_MNEMONIC);
//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 * @throws Exception 
	 * @throws HeadlessException 
	 */
	protected boolean doAction(ActionEvent e) throws HeadlessException, Exception
	{
//		Log.logInfo(this, "GraphDeleteAction's actionPerformed() is called.");
		JGraph graph = getController().getMiddlePanel().getGraph();
		if (!graph.isSelectionEmpty())
		{
			Object[] cells = graph.getSelectionCells();
			DefaultGraphCell graphCell = (DefaultGraphCell) cells[0];
			if (graphCell instanceof DefaultEdge)
			{
				DefaultEdge linkEdge = (DefaultEdge)graphCell;
				// get the source node and examine the mapping Name
				DefaultPort tgtPort=(DefaultPort)linkEdge.getTarget();
				Object targetNode = tgtPort.getUserObject();
				DefaultTargetTreeNode tgtNode = (DefaultTargetTreeNode) targetNode;
				
				//
				String prevVal = (String)getController().getMiddlePanel().getGraph().getModel().getValue(linkEdge);
				String currVal = null;
				char[] specialChars = {'?','|','{','}','~','`','(',')','!','@',']','[','#','$','%','^','&','*','\\',' ',':',';','"','=','\''};
				if(prevVal!=null)
				{
					currVal = JOptionPane.showInputDialog(null, "Please enter the name for mapping : ",
							prevVal.trim());
				}
				else
				{
					currVal = JOptionPane.showInputDialog(null, "Please enter the name for mapping : ", 
							"");
				}
				
				if(currVal!=null)
				{
					char[] inputStringChars = currVal.toCharArray();
					boolean specialCharIsFound = false;  
					String patternString = "[/]+";
					Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
				    Matcher matcher = pattern.matcher(currVal);
				    while(matcher.find())
				    {
				    	if(matcher.start()>0 && (matcher.end()-matcher.start()>1))
				    	{
				    	   //System.out.println("matcher start...."+matcher.start());
				    	   JOptionPane.showMessageDialog(MainFrame.getMappingMainPanel(), "Please enter a valid path...", "Invalid Path Entry!!!", JOptionPane.ERROR_MESSAGE);
				    	   return false;
				    	}
				    	
				    }
					 for(int x = 0; x < inputStringChars.length; x++)  
					 {  
						 
					   for(int y = 0; y < specialChars.length; y++)
					   {
						   
							   if(inputStringChars[x]==specialChars[y]){  
								   specialCharIsFound = true;  
								   break;  
							   }	 
						   
					   }
					   
					 }

				       if( specialCharIsFound){
				         
				    	   JOptionPane.showMessageDialog(MainFrame.getMappingMainPanel(), "Please enter a valid path...", "Invalid Path Entry!!!", JOptionPane.ERROR_MESSAGE);
				    	   return false;
				       }
					
					getController().getMiddlePanel().getGraph().getModel().valueForCellChanged(linkEdge,currVal);
					getController().getMiddlePanel().getGraph().getSelectionModel().clearSelection();
					tgtNode.setMappingName(currVal);
				}
				
			}
		}
		else
		{
			JOptionPane.showMessageDialog(getMiddlePanel(), "No graph is currently selected.", "No selection", JOptionPane.WARNING_MESSAGE);
		}
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return getMiddlePanel();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
