/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.cxf.client;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.DynamicPortTestCase;

public class GeneratedClientTestCase extends DynamicPortTestCase
{
    public void testEchoService() throws Exception
    {
        // URL wsdl = getClass().getResource("/wsdl/hello_world.wsdl");
        // assertNotNull(wsdl);
        // SOAPService service = new SOAPService(wsdl, null);
        // Greeter soapPort = service.getSoapPort();
        //        
        String msg = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                     + "<soap:Body>" + "<test> foo </test>" + "</soap:Body>" + "</soap:Envelope>";

        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("http://localhost:" + getPorts().get(0) + "/services/Echo", msg, null);
        byte[] res = (byte[]) result.getPayload();
        String resString = new String(res);

        assertTrue(resString.indexOf("<test> foo </test>") != -1);
    }

    protected String getConfigResources()
    {
        return "proxy-conf.xml";
    }

    @Override
    protected int getNumPortsToFind()
    {
        return 1;
    }

}
