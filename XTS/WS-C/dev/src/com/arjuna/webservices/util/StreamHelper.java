/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag.  All rights reserved. 
 * See the copyright.txt in the distribution for a full listing 
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
package com.arjuna.webservices.util;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.arjuna.webservices.logging.WSCLogger;
import com.arjuna.webservices.stax.ElementContent;

/**
 * Helper class for Stream operations.
 * @author kevin
 * 
 * @message com.arjuna.webservices.util.StreamHelper_1 [com.arjuna.webservices.util.StreamHelper_1] - Unexpected end element: {0}
 * @message com.arjuna.webservices.util.StreamHelper_2 [com.arjuna.webservices.util.StreamHelper_2] - Unexpected end of document reached
 * @message com.arjuna.webservices.util.StreamHelper_3 [com.arjuna.webservices.util.StreamHelper_3] - Unexpected start element: {0}
 */
public class StreamHelper
{
    /**
     * Pseudo namespace used to unbind a prefix.
     */
    private static final String UNBOUND_NAMESPACE = "http://www.arjuna.com/stax/parsing/unboundnamespace" ;
    
    /**
     * Write a start element with appropriate namespace declarations if necessary
     * @param out The output stream.
     * @param elementName The element name.
     * @return The previous namespace if written, null otherwise.
     * @throws XMLStreamException for errors during writing.
     */
    public static String writeStartElement(final XMLStreamWriter out, final QName elementName)
        throws XMLStreamException
    {
        final String namespaceURI = QNameHelper.getNormalisedValue(elementName.getNamespaceURI()) ;
        final String localName = elementName.getLocalPart() ;
        
        final NamespaceContext namespaceContext = out.getNamespaceContext() ;
        final boolean writeNamespace = (namespaceContext.getPrefix(namespaceURI) == null) ;
        
        if (writeNamespace)
        {
            final String prefix = QNameHelper.getNormalisedValue(elementName.getPrefix()) ;
            final String origNamespace = QNameHelper.getNormalisedValue(namespaceContext.getNamespaceURI(prefix)) ;
            if (prefix.length() == 0)
            {
                out.setDefaultNamespace(namespaceURI) ;
                out.writeStartElement(namespaceURI, localName) ;
                out.writeDefaultNamespace(namespaceURI) ;
            }
            else
            {
                out.setPrefix(prefix, namespaceURI) ;
                out.writeStartElement(namespaceURI, localName) ;
                out.writeNamespace(prefix, namespaceURI) ;
            }
            return origNamespace ;
        }
        else
        {
            out.writeStartElement(namespaceURI, localName) ;
            return null ;
        }
    }
    
    /**
     * Write an end element removing the namespace binding if necessary
     * @param out The output stream.
     * @param prefix The element prefix.
     * @param namespaceURI The previous binding for the prefix.
     * @throws XMLStreamException for errors during writing.
     */
    public static void writeEndElement(final XMLStreamWriter out, final String prefix,
        final String namespaceURI)
        throws XMLStreamException
    {
        out.writeEndElement() ;
        if (namespaceURI != null)
        {
            final String resetNamespace = (namespaceURI.length() == 0 ? UNBOUND_NAMESPACE : namespaceURI) ;
            
            if (prefix.length() == 0)
            {
                out.setDefaultNamespace(resetNamespace) ;
            }
            else
            {
                out.setPrefix(prefix, resetNamespace) ;
            }
        }
    }
    
    /**
     * Write the element to the stream.
     * @param out The output stream.
     * @param elementName The name of the element.
     * @param elementContent The element contents.
     * @throws XMLStreamException Thrown for errors during writing.
     */
    public static void writeElement(final XMLStreamWriter out, final QName elementName,
        final ElementContent elementContent)
        throws XMLStreamException
    {
        final String origNamespace = writeStartElement(out, elementName) ;
        elementContent.writeContent(out) ;
        writeEndElement(out, elementName.getPrefix(), origNamespace) ;
    }

    /**
     * Write the attributes to the stream.
     * @param out The output stream.
     * @param attributes The attributes.
     * @throws XMLStreamException Thrown for errors during writing.
     */
    public static void writeAttributes(final XMLStreamWriter out, final Map attributes)
        throws XMLStreamException
    {
        final Set entrySet = attributes.entrySet() ;
        final Iterator entryIter = entrySet.iterator() ;
        while(entryIter.hasNext())
        {
            final Map.Entry entry = (Map.Entry)entryIter.next() ;
            final QName name = (QName)entry.getKey() ;
            final Object value = entry.getValue() ;
            writeAttribute(out, name, value) ;
        }
    }
    
    /**
     * Write the attribute to the stream.
     * @param out The output stream.
     * @param attributeName The attribute name.
     * @param attributeValue The attribute value.
     * @throws XMLStreamException Thrown for errors during writing.
     */
    public static void writeAttribute(final XMLStreamWriter out, final QName attributeName,
        final Object attributeValue)
        throws XMLStreamException
    {
        if (attributeValue instanceof QName)
        {
            writeAttribute(out, attributeName, (QName)attributeValue) ;
        }
        else
        {
            writeAttribute(out, attributeName, attributeValue.toString()) ;
        }
    }
    
    /**
     * Write the attribute to the stream.
     * @param out The output stream.
     * @param attributeName The attribute name.
     * @param attributeValue The attribute value as a QName.
     * @throws XMLStreamException Thrown for errors during writing.
     */
    public static void writeAttribute(final XMLStreamWriter out, final QName attributeName,
        final QName attributeValue)
        throws XMLStreamException
    {
        final String namespaceURI = QNameHelper.getNormalisedValue(attributeValue.getNamespaceURI()) ;
        if (namespaceURI.length() == 0)
        {
            writeAttribute(out, attributeName, attributeValue.getLocalPart()) ;
        }
        else
        {
            final NamespaceContext namespaceContext = out.getNamespaceContext() ;
            final String origPrefix = namespaceContext.getPrefix(namespaceURI) ;
            
            if (origPrefix == null)
            {
                final String prefix = QNameHelper.getNormalisedValue(attributeValue.getPrefix()) ;
                writeNamespace(out, prefix, namespaceURI) ;
                writeAttribute(out, attributeName, QNameHelper.toQualifiedName(attributeValue)) ;
            }
            else
            {
                // KEV must handle clashes with default namespace
                writeAttribute(out, attributeName, QNameHelper.toQualifiedName(origPrefix, attributeValue.getLocalPart())) ;
            }
        }
    }
    
    /**
     * Write the attribute to the stream.
     * @param out The output stream.
     * @param attributeName The attribute name.
     * @param attributeValue The attribute value.
     * @throws XMLStreamException Thrown for errors during writing.
     */
    public static void writeAttribute(final XMLStreamWriter out, final QName attributeName,
        final String attributeValue)
        throws XMLStreamException
    {
        final String namespaceURI = QNameHelper.getNormalisedValue(attributeName.getNamespaceURI()) ;
        final String localName = attributeName.getLocalPart() ;
        
        if (namespaceURI.length() == 0)
        {
            out.writeAttribute(localName, attributeValue) ;
        }
        else
        {
            // KEV must handle clashes with default namespace
            final String prefix = QNameHelper.getNormalisedValue(attributeName.getPrefix()) ;
            writeNamespace(out, prefix, namespaceURI) ;
            out.writeAttribute(namespaceURI, localName, attributeValue) ;
        }
    }
    
    /**
     * Write the QName to the stream as text.
     * @param out The output stream.
     * @param qName The qualified name.
     * @throws XMLStreamException Thrown for errors during writing.
     */
    public static void writeQualifiedName(final XMLStreamWriter out, final QName qName)
        throws XMLStreamException
    {
        final String namespaceURI = QNameHelper.getNormalisedValue(qName.getNamespaceURI()) ;
        
        if (namespaceURI.length() == 0)
        {
            out.writeCharacters(QNameHelper.toQualifiedName(qName)) ;
        }
        else
        {
            final NamespaceContext namespaceContext = out.getNamespaceContext() ;
            final String origPrefix = namespaceContext.getPrefix(namespaceURI) ;
            
            if (origPrefix == null)
            {
                final String prefix = QNameHelper.getNormalisedValue(qName.getPrefix()) ;
                writeNamespace(out, prefix, namespaceURI) ;
                out.writeCharacters(QNameHelper.toQualifiedName(qName)) ;
            }
            else
            {
                // KEV must handle clashes with default namespace
                out.writeCharacters(QNameHelper.toQualifiedName(origPrefix, qName.getLocalPart())) ;
            }
        }
    }
    
    /**
     * Write the namespace if necessary.
     * @param out The output stream.
     * @param prefix The namespace prefix.
     * @param namespaceURI The namespaceURI.
     * @throws XMLStreamException Thrown for errors during writing.
     */
    public static void writeNamespace(final XMLStreamWriter out, final String prefix, final String namespaceURI)
        throws XMLStreamException
    {
        final NamespaceContext namespaceContext = out.getNamespaceContext() ;
            
        final boolean writeNamespace = (namespaceContext.getPrefix(namespaceURI) == null) ;
        if (writeNamespace)
        {
            out.setPrefix(prefix, namespaceURI) ;
            out.writeNamespace(prefix, namespaceURI) ;
        }
    }

    /**
     * Make sure the stream is at the start of the next element.
     * @param streamReader The current stream reader.
     * @throws XMLStreamException For parsing errors.
     */
    public static void skipToNextStartElement(final XMLStreamReader streamReader)
        throws XMLStreamException
    {
        if (streamReader.hasNext())
        {
            streamReader.nextTag() ;
            if (streamReader.isEndElement())
            {
                final String pattern = WSCLogger.log_mesg.getString("com.arjuna.webservices.util.StreamHelper_1") ;
                final String message = MessageFormat.format(pattern, new Object[] {streamReader.getName()}) ;
                throw new XMLStreamException(message) ;
            }
        }
        else
        {
            throw new XMLStreamException(WSCLogger.log_mesg.getString("com.arjuna.webservices.util.StreamHelper_2")) ;
        }
    }

    /**
     * Make sure the stream is at the start of an element.
     * @param streamReader The current stream reader.
     * @throws XMLStreamException For parsing errors.
     */
    public static void skipToStartElement(final XMLStreamReader streamReader)
        throws XMLStreamException
    {
        while (!streamReader.isStartElement())
        {
            if (streamReader.hasNext())
            {
                streamReader.next() ;
            }
            else
            {
                throw new XMLStreamException(WSCLogger.log_mesg.getString("com.arjuna.webservices.util.StreamHelper_2")) ;
            }
        }
    }

    /**
     * Check the next start tag is as expected.
     * @param streamReader The stream reader.
     * @param expected The expected qualified name.
     * @throws XMLStreamException For errors during parsing.
     */
    public static void checkNextStartTag(final XMLStreamReader streamReader, final QName expected)
        throws XMLStreamException
    {
        skipToNextStartElement(streamReader) ;
        checkTag(streamReader, expected) ;
    }
    
    /**
     * Compare the element tag with the expected qualified name.
     * @param streamReader The current stream reader.
     * @param expected The expected qualified name.
     * @throws XMLStreamException For errors during parsing.
     */
    public static void checkTag(final XMLStreamReader streamReader, final QName expected)
        throws XMLStreamException
    {
        final QName elementName = streamReader.getName() ;
        if (!expected.equals(elementName))
        {
            final String pattern = WSCLogger.log_mesg.getString("com.arjuna.webservices.util.StreamHelper_3") ;
            final String message = MessageFormat.format(pattern, new Object[] {elementName}) ;
            throw new XMLStreamException(message) ;
        }
    }

    /**
     * Check to see if the parent element is finished.
     * @param streamReader The stream reader.
     * @return true if it is finished, false otherwise.
     * @throws XMLStreamException For errors during parsing.
     */
    public static boolean checkParentFinished(final XMLStreamReader streamReader)
        throws XMLStreamException
    {
        return (streamReader.nextTag() == XMLStreamConstants.END_ELEMENT) ;
    }
}
