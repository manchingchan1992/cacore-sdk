/*L
 *  Copyright Ekagra Software Technologies Ltd.
 *  Copyright SAIC, SAIC-Frederick
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/cacore-sdk/LICENSE.txt for details.
 */

package gov.nih.nci.codegen.validator;

import gov.nih.nci.codegen.GenerationException;
import gov.nih.nci.codegen.GeneratorError;
import gov.nih.nci.codegen.GeneratorErrors;
import gov.nih.nci.codegen.Validator;
import gov.nih.nci.codegen.util.TransformerUtils;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDatatype;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependencyEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralizable;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLInterface;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLOperation;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Validates the logical model (Object model) of the <code>model</code> passed in as a parameter
 * Validation rules are as follows
 * 
 * <UL>
 * <LI>Package name should not contain spaces or hyphens</LI>
 * <LI>Model should not contain duplicate classes</LI>
 * <LI>Class name should be present</LI>
 * <LI>Class name should not contain empty spaces</LI>
 * <LI>Class name should start with a character</LI>
 * <LI>Navigable association end must have a role name</LI>
 * <LI>Association end must not contain spaces</LI>
 * <LI>Association end name should start with a character</LI> 
 * <LI>Associated class should be present in the included package and not excluded</LI>
 * <LI>Association can not be made to classes in java.lang and java.util packages</LI>
 * <LI>Class should not contain duplicate associations (association ends which are not navigable are not counted)</LI>
 * <LI>Attribute name should be present</LI>
 * <LI>Attribute name should not contain empty spaces</LI>
 * <LI>Attribute name should start with a character</LI>
 * <LI>Class should not have duplicate attributes</LI>
 * <LI>Attribute type should be present</LI>
 * <LI>Attribute type should not contain empty spaces</LI>
 * <LI>Attribute type should be one of the valid values</LI>
 * <LI>Parent class of any class should be in the included package and not excluded</LI>
 * </UL>
 * 
 * @author Satish Patel, Dan Dumitru
 *
 */
public class UMLLogicalModelValidator implements Validator
{

	private static Logger log = Logger.getLogger(UMLLogicalModelValidator.class);
	
	private boolean enabled = true;
	
	private String name = UMLLogicalModelValidator.class.getName();	
	
	private TransformerUtils transformerUtils;
	
	
	public void setTransformerUtils(TransformerUtils transformerUtils) {
		this.transformerUtils = transformerUtils;
	}
	/**
	 * See description for the this class above
	 *  
	 * @see gov.nih.nci.codegen.Validator#validate(gov.nih.nci.ncicb.xmiinout.domain.UMLModel)
	 */
	public GeneratorErrors validate(UMLModel model)
	{
		GeneratorErrors errors = new GeneratorErrors();
		
		if (model == null){
			errors.addError(new GeneratorError(getName() + ": No Model element found within XMI file"));
			return errors;
		}
		
		validateClasses(model, errors);
		validateInterfaces(model, errors);
		
		return errors;
	}
	
	private void validateClasses(UMLModel model, GeneratorErrors errors) {
		Collection<UMLClass> classes = null;

		try {
			classes = transformerUtils.getAllClasses(model);

			if (classes == null || classes.isEmpty()){
				errors.addError(new GeneratorError(getName() + ": No qualifying classes found within the model to process"));
			}
		} catch(GenerationException ge){
			errors.addError(new GeneratorError(getName() + ": No qualifying classes found within the model to process"));
		}

		if (classes != null){
			for(UMLClass klass1:classes)
			{
				
				String class1Name = transformerUtils.getFQCN(klass1);
				if(class1Name ==null || class1Name.trim().length()==0)
					class1Name = "";
				else
					class1Name = class1Name.trim();
				
				if(class1Name.length()==0)
					errors.addError(new GeneratorError(getName() + ": Class name is empty: "+class1Name));
				if(class1Name.indexOf(' ')>0)
					errors.addError(new GeneratorError(getName() + ": Class/package name contains empty spaces: "+class1Name));
				if(class1Name.indexOf("..")>0)
					errors.addError(new GeneratorError(getName() + ": Class name contains an empty package name: "+class1Name));
				if(class1Name.indexOf('-')>0)
					errors.addError(new GeneratorError(getName() + ": Class/package name contains an invalid hyphen ('-'): "+class1Name));				
				if(class1Name.length() >0 && !Character.isLetter(class1Name.charAt(0)))
					errors.addError(new GeneratorError(getName() + ": Class name starts with a non-character value: "+class1Name));
				for(UMLClass klass2:classes)
				{
					String class2Name = transformerUtils.getFQCN(klass2);
					if(klass1!=klass2 && class1Name!=null && class1Name.equals(class2Name))
						errors.addError(new GeneratorError(getName() + ": Duplicate class found for "+class1Name));					
				}
				validateClass(klass1, errors);
			}
		}
	}
	
	private void validateInterfaces(UMLModel model, GeneratorErrors errors) {
		Collection<UMLInterface> interfaces = null;

		try {
			interfaces = transformerUtils.getAllInterfaces(model);
		} catch(GenerationException ge){
			errors.addError(new GeneratorError(getName() + ": Unable to retrieve interfaces from model"));
		}

		if (interfaces != null){
			for(UMLInterface interface1:interfaces)
			{
				String interface1Name = transformerUtils.getFQCN(interface1);
				if(interface1Name ==null || interface1Name.trim().length()==0)
					interface1Name = "";
				else
					interface1Name = interface1Name.trim();

				if(interface1Name.length()==0)
					errors.addError(new GeneratorError(getName() + ": Interface name empty "+interface1Name));
				if(interface1Name.indexOf(' ')>0)
					errors.addError(new GeneratorError(getName() + ": Interface/package name contains empty spaces "+interface1Name));
				if(interface1Name.indexOf("..")>0)
					errors.addError(new GeneratorError(getName() + ": Interface name contains empty package name "+interface1Name));
				if(interface1Name.indexOf("..")>0)
					errors.addError(new GeneratorError(getName() + ": Interface name contains empty package name "+interface1Name));
				if(interface1Name.indexOf('-')>0)
					errors.addError(new GeneratorError(getName() + ": Interface/package name contains an invalid hyphen ('-'): "+interface1Name));					
				if(interface1Name.length() >0 && !Character.isLetter(interface1Name.charAt(0)))
					errors.addError(new GeneratorError(getName() + ": Interface name starts with non-character value "+interface1Name));
				for(UMLInterface interface2:interfaces)
				{
					String interface2Name = transformerUtils.getFQCN(interface2);
					if(interface1!=interface2 && interface1Name!=null && interface1Name.equals(interface2Name))
						errors.addError(new GeneratorError(getName() + ": Duplicate interface found for "+interface1Name));					
				}
				validateInterface(interface1, errors);
			}
		}
	}

	private void validateClass(UMLClass klass,GeneratorErrors errors) {
		//validateSuperClass(klass, errors);
		validateAttributes(klass, errors);
		validateAssociations(klass, errors);
		validateOperations(klass, errors);
	}
	
	private void validateInterface(UMLInterface interfaze,GeneratorErrors errors) {
		validateDependencies(interfaze, errors);
		validateAssociations(interfaze, errors);
		validateGeneralizations(interfaze, errors);
		validateGeneralizations(interfaze, errors);
		validateAttributes(interfaze, errors);
	}
	
	private void validateDependencies(UMLInterface interfaze, GeneratorErrors errors) 
	{
		
		for(UMLDependency dependency: interfaze.getDependencies())
		{
			try 
			{
				UMLDependencyEnd clientEnd = dependency.getClient();
				UMLDependencyEnd supplierEnd = dependency.getSupplier();
				
				if (!(clientEnd instanceof UMLClass)){
					errors.addError(new GeneratorError(getName() + ": Source (client) end of dependency to interface " + interfaze.getName() + " needs to be a class"));
				}
				
				if (!(supplierEnd instanceof UMLInterface && ((UMLInterface)supplierEnd).getName().equalsIgnoreCase(interfaze.getName()))){
					errors.addError(new GeneratorError(getName() + ": Target (supplier) end of dependency to interface " + interfaze.getName() + " needs to be the interface itself"));
				}			
			}
			catch (Exception e) 
			{
				//errors.addError(new GeneratorError(getName() + ": Interface  validation failed", e));
			}
		}
	}
	
	private void validateAssociations(UMLInterface interfaze, GeneratorErrors errors) 
	{
		if (!interfaze.getAssociations().isEmpty()){
			errors.addError(new GeneratorError(getName() + ": Association between two interfaces or between an interface and a class is not supported for interface "+interfaze.getName()));
		}
	}	
	
	private void validateGeneralizations(UMLInterface interfaze, GeneratorErrors errors) 
	{
		for(UMLGeneralization generalization: interfaze.getGeneralizations())
		{
			UMLGeneralizable subtype = generalization.getSubtype();
			UMLGeneralizable supertype = generalization.getSupertype();
			if (subtype instanceof UMLClass){
				errors.addError(new GeneratorError(getName() + ": Generalization between class " +subtype.getName() + " and interface " + supertype.getName() + " is not supported"));
			}
			
			if (supertype instanceof UMLClass){
				errors.addError(new GeneratorError(getName() + ": Generalization between interface " +subtype.getName() + " and class " + supertype.getName() + " is not supported"));
			}
		}
	}	

	private void validateAttributes(UMLInterface interfaze, GeneratorErrors errors) 
	{
		if (!interfaze.getAttributes().isEmpty()){
			errors.addError(new GeneratorError(getName() + ": Attributes not supported for interface "+interfaze.getName()));
		}
	}
	
	private void validateOperations(UMLClass klass, GeneratorErrors errors) 
	{
		List<UMLOperation> operations = klass.getOperations();
		if(operations == null || operations.size() == 0)
			return;
	
		log.debug("Validating operations for: "+klass.getName());
		UMLClass superKlass = null;
		try
		{
			superKlass = transformerUtils.getSuperClass(klass);
		}
		catch(GenerationException e)
		{
			errors.addError(new GeneratorError(klass.getName() + ": Failed to get Super class"));
		}
		
		UMLInterface[] superInterface = ModelUtil.getInterfaces(klass);
		
		//If this class doesn't extend or implement
		//no need to validate operations. Operations on this class are validated at compile time
		if(superKlass == null && (superInterface == null || superInterface.length == 0))
			return;
		
		log.debug("Validating operations:  superclass: "+superKlass.getName());
		//If this class is abstract, it is not obligated to implement operations from extending abstract or implmenting interface
		//if(superKlass.getAbstractModifier().isAbstract())
		//	return;
		
		//Get all abstract operations of super classes and verify if they are implemented in this class
		//Super class abstract operation could also be implemented in another child extended by this class
		Map<String, List<UMLOperation>> superClassOperations = new HashMap();
		try
		{
			transformerUtils.getSuperClassOperations(superClassOperations, klass);
		}
		catch(GenerationException e)
		{
			log.debug("Failed to get super class operations for "+klass.getName());
		}
		log.debug("Validating operations:  superclass operations: "+superClassOperations);
		if(superClassOperations.size() > 0)
		{
			Iterator iterator = superClassOperations.keySet().iterator();
			List<UMLOperation> allSuperOperations; 
			while(iterator.hasNext())
			{
				String fullName = (String)iterator.next();
				log.debug("Validating operations:  superclass fullName: "+fullName);
				allSuperOperations = superClassOperations.get(fullName);
				if(allSuperOperations.size() == 0)
					continue;

				//Get all operations except for the current class
				List<UMLOperation> filteredOperations = filterOperations(superClassOperations, fullName);
				log.debug("Validating operations:  filtered: "+filteredOperations);
				for(UMLOperation operation : allSuperOperations)
				{
					if(!operation.getAbstractModifier().isAbstract())
						continue;
					
					String signature = ModelUtil.getOperationName(operation, false);
					log.debug("Validating operations:  signature: "+signature);
					UMLOperation matchedOperationFromImpl = transformerUtils.matchOperationSignature(signature, operations);
					log.debug("Validating operations:  matchedOperationFromImpl: "+matchedOperationFromImpl);
					if(matchedOperationFromImpl == null)
					{
						UMLOperation matchedOperation = transformerUtils.matchOperationSignature(signature, filteredOperations);
						log.debug("Validating operations:  matchedOperation: "+matchedOperation);
						if(matchedOperation == null)
							errors.addError(new GeneratorError("Abstract operation: "+signature + "in class: " + fullName + " should be implemeted"));
					}
					
				}
			}
		}
		
		//Get all operations of interfaces and verify if they are implemented in this class
		//Super interface operation could also be implemented in another child extended by this class
		Map<String, List<UMLOperation>> superInterfaceOperations = new HashMap();
		try
		{
			transformerUtils.getSuperInterfaceOperations(superInterfaceOperations, klass);
		}
		catch(GenerationException e)
		{
			log.debug("Failed to get super class operations for "+klass.getName());
		}
		
		log.debug("Validating operations:  superInterfaceOperations: "+superInterfaceOperations);
		if(superInterfaceOperations.size() > 0)
		{
			Iterator iterator = superInterfaceOperations.keySet().iterator();
			List<UMLOperation> currentClassOperations; 
			while(iterator.hasNext())
			{
				String fullName = (String)iterator.next();
				log.debug("Validating operations:  superInterface fullName: "+fullName);
				currentClassOperations = superInterfaceOperations.get(fullName);
				if(currentClassOperations == null || currentClassOperations.size() == 0)
					continue;

				for(UMLOperation operation : currentClassOperations)
				{
					String signature = ModelUtil.getOperationName(operation, false);
					log.debug("Validating operations:  superInterface signature: "+signature);
					UMLOperation matchedOperationFromImpl = transformerUtils.matchOperationSignature(signature, operations);
					log.debug("Validating operations:  superInterface matchedOperationFromImpl: "+matchedOperationFromImpl);
					if(matchedOperationFromImpl == null)
						errors.addError(new GeneratorError("Abstract operation: "+signature + "in class: " + fullName + " should be implemeted"));
				}
			}
		}
	}
	
	private List<UMLOperation> filterOperations(Map<String, List<UMLOperation>> operationsMap, String exclude)
	{
		List<UMLOperation> allSuperOperations = new ArrayList(); 
		if(operationsMap != null && operationsMap.size() > 0)
		{
			Iterator iterator = operationsMap.keySet().iterator();
			while(iterator.hasNext())
			{
				String fullName = (String)iterator.next();
				if(exclude != null && exclude.equals(fullName))
					continue;
				
				allSuperOperations.addAll(operationsMap.get(fullName));
			}
		}
		return allSuperOperations;
	}
	
	private void validateAssociations(UMLClass klass, GeneratorErrors errors) 
	{
		for(UMLAssociation association: klass.getAssociations())
		{
			try 
			{
				List <UMLAssociationEnd>ends = association.getAssociationEnds();
				UMLAssociationEnd thisEnd = transformerUtils.getThisEnd(klass, ends);
				UMLAssociationEnd otherEnd = transformerUtils.getOtherEnd(klass, ends);
				String thisClassName = transformerUtils.getFQCN(((UMLClass)thisEnd.getUMLElement()));
				String otherClassName = transformerUtils.getFQCN(((UMLClass)otherEnd.getUMLElement()));
				if(!transformerUtils.isIncluded((UMLClass)otherEnd.getUMLElement()))
					errors.addError(new GeneratorError(getName() + ": Association belongs to the not included or excluded package for association between "+thisClassName +" and "+ otherClassName));
				if(thisEnd.getRoleName()!=null && thisEnd.getRoleName().length()>0 && !Character.isLetter(thisEnd.getRoleName().charAt(0)))
					errors.addError(new GeneratorError(getName() + ": Association role name starts with non-character value for association between "+thisClassName +" and "+ otherClassName));
				if(otherEnd.getRoleName()!=null && otherEnd.getRoleName().length()>0 && !Character.isLetter(otherEnd.getRoleName().charAt(0)))
					errors.addError(new GeneratorError(getName() + ": Association role name starts with non-character value for association between "+thisClassName +" and "+ otherClassName));
				
				if((thisEnd.isNavigable() && (thisEnd.getRoleName()==null || thisEnd.getRoleName().trim().length()==0 ))|| (otherEnd.isNavigable() && (otherEnd.getRoleName()==null || otherEnd.getRoleName().trim().length()==0 )))
					errors.addError(new GeneratorError(getName() + ": Association end name not specified for association between "+thisClassName +" and "+ otherClassName));
				if((thisEnd.getRoleName()!= null && thisEnd.getRoleName().indexOf(' ') > 0 ) || (otherEnd.getRoleName()!= null && otherEnd.getRoleName().indexOf(' ') > 0 ))
					errors.addError(new GeneratorError(getName() + ": Association end name contains empty spaces for association between "+thisClassName +" and "+ otherClassName));
				if(thisClassName.startsWith("java.lang") || thisClassName.startsWith("java.util"))
					errors.addError(new GeneratorError(getName() + ": Association to the wrapper class not allowed for association between "+thisClassName +" and "+ otherClassName));
				if(otherClassName.startsWith("java.lang") || otherClassName.startsWith("java.util"))
					errors.addError(new GeneratorError(getName() + ": Association to the wrapper class not allowed for association between "+thisClassName +" and "+ otherClassName));

				//validate multiplicity
				if (thisEnd.isNavigable())
				{
					if (!transformerUtils.isMultiplicityValid(thisEnd)){
						errors.addError(new GeneratorError(getName() + ": The association multiplicity value of "+ transformerUtils.getMultiplicityValue(thisEnd) + " between " + thisClassName +" and "+ otherClassName + " is invalid."));
					}
				}				
				
				if (otherEnd.isNavigable())
				{
					for(UMLAssociation assoc: klass.getAssociations())
					{
						List <UMLAssociationEnd>ends2 = assoc.getAssociationEnds();
						if(ends!=ends2)
						{
							UMLAssociationEnd thisEnd2 = transformerUtils.getThisEnd(klass, ends2);
							UMLAssociationEnd otherEnd2 = transformerUtils.getOtherEnd(klass, ends2);
							String thisClassName2 = transformerUtils.getFQCN(((UMLClass)thisEnd2.getUMLElement()));
							String otherClassName2 = transformerUtils.getFQCN(((UMLClass)otherEnd2.getUMLElement()));
							
							log.debug("otherEnd.getRoleName(): "+ otherEnd.getRoleName()+"; otherEnd2.getRoleName(): "+otherEnd2.getRoleName());
							if(otherEnd.getRoleName()!=null && otherEnd.getRoleName().equals(otherEnd2.getRoleName()))
								errors.addError(new GeneratorError(getName() + ": Duplicate association between "+thisClassName2 +" and "+ otherClassName2));
						}
					}
				}
			
			}
			catch (GenerationException e) 
			{
				errors.addError(new GeneratorError(getName() + ": Association validation failed", e));
			}
		}
	}

	private void validateAttributes(UMLClass klass, GeneratorErrors errors) 
	{
		String thisClassName = transformerUtils.getFQCN(klass);
		for(UMLAttribute attribute: klass.getAttributes())
		{
			if(attribute.getName()==null || attribute.getName().trim().length()==0)
				errors.addError(new GeneratorError(getName() + ": Attribute name empty in "+thisClassName+": "+attribute.getName()));
			if(attribute.getName()!=null && attribute.getName().indexOf(' ')>0)
				errors.addError(new GeneratorError(getName() + ": Attribute name contains empty spaces in "+thisClassName+": "+attribute.getName()));
			if(attribute.getName().length()>1 && Character.isLowerCase(attribute.getName().charAt(0)) && Character.isUpperCase(attribute.getName().charAt(1)))
				errors.addError(new GeneratorError(getName() + ": Attribute name contains first character lower case and second character as upper case in "+thisClassName+": "+attribute.getName()+". This combination is currently not supported."));
			if(attribute.getName().length() >0 && !Character.isLetter(attribute.getName().charAt(0)))
				errors.addError(new GeneratorError(getName() + ": Attribute name starts with a non-character value in "+thisClassName+": "+attribute.getName()));

			UMLDatatype dataType = attribute.getDatatype();
			String name = dataType.getName();
			if(dataType instanceof UMLClass)
				name = transformerUtils.getFQCN((UMLClass)dataType);
			if(name == null) name = "";
			
			if(name.trim().length() == 0)
				errors.addError(new GeneratorError(getName() + ": Attribute type empty in "+thisClassName+": "+attribute.getName()));
			if(name.indexOf(' ')>0)
				errors.addError(new GeneratorError(getName() + ": Attribute type contains empty spaces in "+thisClassName+": "+attribute.getName()));
			if(name.indexOf('-')>0)
				errors.addError(new GeneratorError(getName() + ": Attribute type contains an invalid hyphen ('-') in : "+thisClassName+": "+attribute.getName()));
			
			if(name.startsWith("java.lang."))
				name = name.substring("java.lang.".length());

/*			if( !transformerUtils.javaDatatypeMap.containsKey(name))
			{
				if(transformerUtils.isISO21090Enabled() && !transformerUtils.isoDatatypeMap.containsKey(name))
					errors.addError(new GeneratorError(getName() + ": Invalid datatype for the "+attribute.getName()+" attribute in the "+thisClassName+" class"));
			}
*/
			if(transformerUtils.getDataType(attribute) == null || "".equals(transformerUtils.getDataType(attribute)))
				errors.addError(new GeneratorError(getName() + ": Invalid datatype for the "+attribute.getName()+" attribute in the "+thisClassName+" class"));
			
			for(UMLAttribute attr: klass.getAttributes())
				if(attr!=attribute &&  attr.getName()!=null && attr.getName().equals(attribute.getName()))
					errors.addError(new GeneratorError(getName() + ": Duplicate attributes found in the "+thisClassName+" class :"+attr.getName()));
		}
	}

	private void validateSuperClass(UMLClass klass, GeneratorErrors errors) {
		try {
			UMLClass superKlass = transformerUtils.getSuperClass(klass);
			if(superKlass!=null && !transformerUtils.isIncluded(superKlass))
				errors.addError(new GeneratorError(getName() + ": Parent of the class "+transformerUtils.getFQCN(klass)+" belongs to the not included or excluded package"));

		} catch (GenerationException e) {
			errors.addError(new GeneratorError(getName() + ": Superclass validation failed", e));
		}
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public Boolean isEnabled() {
		return enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}