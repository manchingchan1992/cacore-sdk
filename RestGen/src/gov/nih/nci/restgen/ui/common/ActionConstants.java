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

package gov.nih.nci.restgen.ui.common;


/**
 * Define a list of constants that are used by menu and some of action definition.
 *
 * @author Prakash Vinjamuri
 * @author Prakash Vinjamuri LAST UPDATE
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2013-01-11
 */
public interface ActionConstants
{
	//The naming convention:
	//NEW_MAP_FILE is used as the key to the text, while the NEW_MAP_FILE_TXT is used as the action name;
	//this is because the action name in display may be the same but we really need to distinguish them in a map
	//between a new and an open command.

	String OPEN = "Open...";
	String OPENPOJO = "Open POJO JAR";
	String OPEN_MAPPING = "Open Mapping";
	String SAVE = "Save";
	String OPTIONS = "Options";
	String CLOSE = "Close";
	String EXIT = "Exit";
	String HELP_TOPIC = "HELP_TOPIC";
	String RESTFUL = "RESTful Resource";

	String HELP = "Help ";
	String GENERATE_REPORT = "Generate Report...";
	String VALIDATE = "Validate";
	String ANOTATE = "Anotate";
	String REFRESH = "Refresh";
	String NEW_MAP_FILE = "Open POJO file";
	String OPEN_MAP_FILE = "Open Transformation Mapping";
	String NEW_XML_Transformation = "XML to XML Transformation";
	String NEW_XSLT_STYLESHEET="XSLT Stylesheet Artifact";
	String NEW_XQUERY_STATEMENT="XQuery Artifact";
    //String NEW_ADD_SCENARIO_TAG = "***";
    String NEW_CSV_Transformation = "CSV to XML Transformation";
	String NEW_HL7_V2_Transformation = "HL7 v2 to XML Transformation";
	String NEW_XML_CDA_Transformation = "XML to CDA Transformation";
	String NEW_CSV_CDA_Transformation = "CSV to CDA Transformation";
	String NEW_HL7_V2_CDA_Transformation = "HL7 v2 to CDA Transformation";
    String FILE_NAME_UNTITLED_TAG = "Untitled_";
    String MESSAGE_NOT_A_MAPPING_FILE = "NOT A MAPPING FILE....";
	String SCENARIO_DELETE_SECURITY_CONFIRMATION_CODE_FILE = "SecurityCode.txt";
    //String CMTS_HELP_MENU_CONTENT_URL = "https://wiki.nci.nih.gov/display/caCORE/caCore SDK+Common+Mapping+and+Transformation+Service+v1.0+User%27s+Guide";
    String CMTS_HELP_MENU_CONTENT_URL = "https://wiki.nci.nih.gov/x/GQBwBg";
	String UPLOADWSDL = "WSDL";
	String GENERATERESTFUL = "RESTful Resource";
	String NEW_POJO_FILE = "Open POJO";
	String UPLOADEJBJAR = "EJB JAR";
	String DELETENODE = "Delete Node";
	String EDITNODEPATH = "Edit Path";

}

/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY : UI update.
 * HISTORY :
 * HISTORY : Revision 1.1  2008/11/04 15:58:57  linc
 * HISTORY : updated.
 * HISTORY :
 */
