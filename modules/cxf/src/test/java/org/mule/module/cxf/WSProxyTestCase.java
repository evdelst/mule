/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.cxf;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.DynamicPortTestCase;

import java.util.HashMap;
import java.util.Map;

public class WSProxyTestCase extends DynamicPortTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "mule-proxy-config.xml";
    }

    public void testDirectRequest() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("wsdl-cxf:http://localhost:" + getPorts().get(0) + "/WebService?wsdl&method=echo", 
            new DefaultMuleMessage("mule", muleContext));
        assertEquals ("mule", result.getPayloadAsString());
    }


    public void testWsdlProxyRequest() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Map<String, String> props = new HashMap<String, String>();
        props.put("http.method", "GET");
        MuleMessage replyMessage = client.send("http://localhost:" + getPorts().get(1) + "/webServiceProxy?wsdl", 
            "/services/webServiceProxy?WSDL", props);
        assertNotNull(replyMessage);
        
        String wsdl = replyMessage.getPayloadAsString();
        assertNotNull(wsdl);
        System.out.println(wsdl);
        assertTrue(wsdl.indexOf("<wsdl:definitions") != -1);
        assertTrue(wsdl.indexOf("<wsdl:message name=\"echoResponse\">") != -1);
        assertTrue(wsdl.indexOf("<wsdl:message name=\"echo\">") != -1);
    }
    
    public void testProxyRequest() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("wsdl-cxf:http://localhost:" + getPorts().get(1) + "/webServiceProxy?wsdl&method=echo", 
            new DefaultMuleMessage("mule", muleContext));
        assertEquals ("mule", result.getPayloadAsString());
    }
    
    public void testWsdlFileRequest() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Map<String, String> props = new HashMap<String, String>();
        props.put("http.method", "GET");
        MuleMessage replyMessage = client.send("http://localhost:" + getPorts().get(2) + "/webServiceProxy?wsdl", 
            "/services/webServiceProxy?WSDL", props);
        assertNotNull(replyMessage);
        
        String wsdl = replyMessage.getPayloadAsString();
        assertNotNull(wsdl);
        assertTrue(wsdl.indexOf("<wsdl:definitions") != -1);
        assertTrue(wsdl.indexOf("<wsdl:message name=\"echoResponse\">") != -1);
        assertTrue(wsdl.indexOf("<wsdl:message name=\"echo\">") != -1);
    }
    
    public void testWsdlFileProxyRequest() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("wsdl-cxf:http://localhost:" + getPorts().get(2) + "/webServiceProxy?wsdl&method=echo", 
            new DefaultMuleMessage("mule", muleContext));
        assertEquals ("mule", result.getPayloadAsString());
    }

    @Override
    protected int getNumPortsToFind()
    {
        return 3;
    }
    
}
