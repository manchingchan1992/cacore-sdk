/*L
 *  Copyright Ekagra Software Technologies Ltd.
 *  Copyright SAIC, SAIC-Frederick
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/cacore-sdk/LICENSE.txt for details.
 */

package test.xml.data;

import gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Address;
import gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Person;
import gov.nih.nci.iso21090.Ii;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.Collection;
import java.util.Iterator;



public class O2OUnidirectionalXMLData2Test extends SDKXMLDataTestBase
{
	public static String getTestCaseName()
	{
		return "One to One Unidirectional XML Data Test Case";
	}

	/**
	 * Uses Nested Search Criteria for search
	 * Verifies that the results are returned 
	 * Verifies size of the result set
	 * Verifies that none of the attribute is null
	 * Verifies that the associated object has required Id
	 * 
	 * @throws Exception
	 */
	public void testOneAssociatedObjectNestedSearch1() throws Exception
	{
		Person searchObject = new Person();
		Ii ii = new Ii();
		ii.setExtension("1");
		searchObject.setId(ii);
		Collection results = getApplicationService().search("gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Person",searchObject );

		assertNotNull(results);
		assertEquals(1,results.size());
		
		Iterator i = results.iterator();
		Person result = (Person)i.next();
		toXML(result);
		Person result2 = (Person)fromXML(result);
		
		assertNotNull(result2);
		assertNotNull(result2.getId().getExtension()); 
		assertEquals(II_ROOT_GLOBAL_CONSTANT_VALUE,result2.getId().getRoot());
		assertNotNull(result2.getName());
		
		Address address = result2.getLivesAt();
		assertNotNull(address);
		assertNotNull(address.getId());
		assertNotNull(address.getZip());
		assertEquals("1",address.getId().getExtension());
	}

	/**
	 * Uses Nested Search Criteria for search to get associated object
	 * Verifies that the results are returned 
	 * Verifies size of the result set
	 * Verifies that none of the attribute is null
	 * Verified the Id attribute's value of the returned object 
	 * 
	 * @throws Exception
	 */
	public void testOneAssociatedObjectNestedSearch2() throws Exception
	{
		Person searchObject = new Person();
		Ii ii = new Ii();
		ii.setExtension("1");
		searchObject.setId(ii);
		Collection results = getApplicationService().search("gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Address",searchObject );

		assertNotNull(results);
		assertEquals(1,results.size());
		
		Iterator i = results.iterator();
		
		Address result = (Address)i.next();
		toXML(result);
		Address result2 = (Address)fromXML(result);
		
		assertNotNull(result2);
		assertNotNull(result2.getId().getExtension());  
		assertEquals(II_ROOT_GLOBAL_CONSTANT_VALUE,result2.getId().getRoot());
		assertNotNull(result2.getZip());
		assertEquals("1",result2.getId().getExtension());
	}
	
	public void testGetAssociation() throws Exception
	{

		Person searchObject = new Person();
		Collection results = getApplicationService().search("gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Person",searchObject );

		assertNotNull(results);
		assertEquals(5,results.size());
		
		Address address;
		for(Iterator i = results.iterator();i.hasNext();)
		{
			Person result = (Person)i.next();
			toXML(result);
			Person result2 = (Person)fromXML(result);
			
			assertNotNull(result2);
			assertNotNull(result2.getId().getExtension());  
			assertEquals(II_ROOT_GLOBAL_CONSTANT_VALUE,result2.getId().getRoot());
			assertNotNull(result2.getName());
			
			if (new Integer(result2.getId().getExtension()) < 4){//Person id=1,2,3 have an associated Address; the others don't
				
				validateAssociation(result,"Address","livesAt", false);
				
				address = result2.getLivesAt();
				assertNotNull(address);
				assertNotNull(address.getId());
				assertNotNull(address.getZip());
			}
		}
	}	
	
	public void testGetAssociationHQL() throws Exception {
		HQLCriteria hqlCriteria = new HQLCriteria(
				"from gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Person");
		Collection results = search(hqlCriteria,
				"gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Person");
		assertNotNull(results);
		assertEquals(5, results.size());

		Address address;
		for (Iterator i = results.iterator(); i.hasNext();) {
			Person result = (Person) i.next();
			toXML(result);
			Person result2 = (Person) fromXML(result);

			assertNotNull(result2);
			assertNotNull(result2.getId().getExtension()); 
			assertEquals(II_ROOT_GLOBAL_CONSTANT_VALUE,result2.getId().getRoot());
			assertNotNull(result2.getName());

			if (new Integer(result2.getId().getExtension()) < 4) {//Person id=1,2,3 have an associated Address; the others don't
				validateAssociation(result, "Address", "livesAt", false);
				address = result2.getLivesAt();
				assertNotNull(address);
				assertNotNull(address.getId());
				assertNotNull(address.getZip());
			}
		}
	}
}
