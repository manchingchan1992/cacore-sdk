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

package gov.nih.nci.restgen.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * This class is a basic utility class.
 *
 * @author Prakash Vinjamuri
 * @author Prakash Vinjamuri LAST UPDATE
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2013-01-11
 *
 */
public class SingleFileFilter extends FileFilter
{
	private String extension = null;

	public SingleFileFilter(String extension)
	{
		this.extension = extension;
	}

	public boolean accept(File f)
	{
		if (f.isDirectory())
		{
			return true;
		}

        if (extension.endsWith("*"))
        {
            String ext = extension;
            ext = ext.substring(0, ext.length()-1);
            String name = f.getName().toLowerCase();
            int idx = name.indexOf(ext);

            if (idx <= 0) return false;
            name = name.substring(idx + ext.length());
            if (name.indexOf(".") >= 0) return false;
            else return true;
        }
        else return f.getName().toLowerCase().endsWith(extension.toLowerCase());
	}

	public String getDescription()
	{
		return extension;
	}

	/**
	 * Override so as to provide enhanced equal() comparison.
	 *
	 * @param o
	 * @return true if given object is equal to this object; false, otherwise.
	 */
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final SingleFileFilter that = (SingleFileFilter) o;

		if (extension != null ? !extension.equals(that.extension) : that.extension != null)
		{
			return false;
		}

		return true;
	}

	/**
	 * Return hash code representation of this object.
	 *
	 * @return hash code representation of this object.
	 */
	public int hashCode()
	{
		return (extension != null ? extension.hashCode() : 0);
	}

	public String toString()
	{
		return this.extension;
	}

	public String getExtension() {
		return extension;
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

