/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.polling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.processor.MessageProcessor;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.tck.SensingNullMessageProcessor;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.transport.NullPayload;
import org.mule.transport.polling.watermark.builder.NullWatermarkConfiguration;
import org.mule.transport.polling.watermark.builder.WatermarkConfiguration;

import org.junit.Test;
import org.mockito.Mockito;

public class MessageProcessorPollingMessageReceiverTestCase extends AbstractMuleContextTestCase
{

    @Test(expected = InitialisationException.class)
    public void testNotWatermarkConfigured() throws MuleException
    {
        createReceiver(new MessageProcessor()
        {
            public MuleEvent process(MuleEvent event) throws MuleException
            {
                return null;
            }
        }, null);

    }

    @Test
    public void testNullResponseFromNestedMP() throws Exception
    {

        MessageProcessorPollingMessageReceiver receiver = createReceiver(new MessageProcessor()
        {
            public MuleEvent process(MuleEvent event) throws MuleException
            {
                return null;
            }
        }, new NullWatermarkConfiguration());

        SensingNullMessageProcessor flow = getSensingNullMessageProcessor();
        receiver.setListener(flow);

        receiver.poll();

        assertNull(flow.event);
    }

    @Test
    public void testNullPayloadResponseFromNestedMP() throws Exception
    {

        MessageProcessorPollingMessageReceiver receiver = createReceiver(new MessageProcessor()
        {
            public MuleEvent process(MuleEvent event) throws MuleException
            {
                return new DefaultMuleEvent(new DefaultMuleMessage(NullPayload.getInstance(), muleContext),
                    event);
            }
        }, new NullWatermarkConfiguration());

        SensingNullMessageProcessor flow = getSensingNullMessageProcessor();
        receiver.setListener(flow);

        receiver.poll();

        assertNull(flow.event);
    }

    @Test
    public void testEmptyStringResponseFromNestedMP() throws Exception
    {

        MessageProcessorPollingMessageReceiver receiver = createReceiver(new MessageProcessor()
        {
            public MuleEvent process(MuleEvent event) throws MuleException
            {
                return new DefaultMuleEvent(new DefaultMuleMessage("", muleContext), event);
            }
        }, new NullWatermarkConfiguration());

        SensingNullMessageProcessor flow = getSensingNullMessageProcessor();
        receiver.setListener(flow);

        receiver.poll();

        assertNotNull(flow.event);
    }

    @Test
    public void testNestedOneWayEndpoint() throws Exception
    {

        try
        {
            createReceiver(muleContext.getEndpointFactory().getOutboundEndpoint("test://test2"), new NullWatermarkConfiguration());

            org.junit.Assert.fail("Exception expected");
        }
        catch (Exception e)
        {

            assertEquals(InitialisationException.class, e.getClass());
        }

    }

    private MessageProcessorPollingMessageReceiver createReceiver(MessageProcessor processor,
                                                                  WatermarkConfiguration watermarkConfiguration)
        throws MuleException
    {
        EndpointURIEndpointBuilder builder = new EndpointURIEndpointBuilder("test://test", muleContext);
        builder.setProperty(MessageProcessorPollingMessageReceiver.SOURCE_MESSAGE_PROCESSOR_PROPERTY_NAME,
            processor);
        builder.setProperty(MessageProcessorPollingMessageReceiver.WATERMARK_PROPERTY_NAME, watermarkConfiguration);

        InboundEndpoint inboundEndpoint = muleContext.getEndpointFactory().getInboundEndpoint(builder);

        MessageProcessorPollingMessageReceiver receiver = new MessageProcessorPollingMessageReceiver(
            inboundEndpoint.getConnector(), Mockito.mock(FlowConstruct.class), inboundEndpoint);

        receiver.initialise();
        return receiver;
    }


}
