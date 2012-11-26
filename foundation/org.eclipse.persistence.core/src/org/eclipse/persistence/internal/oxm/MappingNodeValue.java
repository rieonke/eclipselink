/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* bdoughan - January 7/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;

/**
 * A node value corresponding to mapping. 
 */
public abstract class MappingNodeValue extends NodeValue {

    /**
     * Return the mapping associated with this node value. 
     */
    public abstract Mapping getMapping();
    
    public boolean isMappingNodeValue() {
        return true;
    }

    protected void updateNamespaces(QName qname, MarshalRecord marshalRecord, XMLField xmlField){
        if (qname != null){        
            if(xmlField != null){
                if(xmlField.isTypedTextField()){           
                    if(xmlField.getSchemaType() == null){
                        if(qname.equals(XMLConstants.STRING_QNAME)){
                            return;
                        }
                    }else{
                    	if(xmlField.isSchemaType(qname)){
                    		return;
                    	}
                    }
                }else{
                    return;
                }
            }
               
            String prefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(qname.getNamespaceURI());
            if ((prefix == null) || prefix.length() == 0) {
            	
            	if(XMLConstants.SCHEMA_URL.equals(qname.getNamespaceURI())){
                    prefix = marshalRecord.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_PREFIX);	
                }else{            	
                    prefix = marshalRecord.getNamespaceResolver().generatePrefix();              
                }
            	marshalRecord.namespaceDeclaration(prefix, qname.getNamespaceURI());
            }
            String typeValue = null;
            if(marshalRecord.isNamespaceAware()){
                typeValue = prefix + marshalRecord.getNamespaceSeparator() + qname.getLocalPart();
            }else{
            	typeValue = qname.getLocalPart();
            }

            addTypeAttribute(marshalRecord, typeValue);
        }
    }
    
    protected void addTypeAttribute(MarshalRecord marshalRecord, String typeValue) {        
        String xsiPrefix = null;
        if (marshalRecord.getNamespaceResolver() != null) {
            xsiPrefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        } else {
            xsiPrefix = XMLConstants.SCHEMA_INSTANCE_PREFIX;            
        	marshalRecord.namespaceDeclaration(xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);

        }
        if (xsiPrefix == null) {
            xsiPrefix = marshalRecord.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
        	marshalRecord.namespaceDeclaration(xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);

        }
        marshalRecord.attributeWithoutQName(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE, xsiPrefix, typeValue);     
    }

}