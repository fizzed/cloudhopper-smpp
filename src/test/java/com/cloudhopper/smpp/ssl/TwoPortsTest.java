
package com.cloudhopper.smpp.ssl;

/*
 * #%L
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.cloudhopper.smpp.*;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppServer;
import com.cloudhopper.smpp.impl.DefaultSmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.*;
import com.cloudhopper.smpp.type.SmppProcessingException;
import org.jboss.netty.channel.Channel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author ruwen
 */
public class TwoPortsTest {

    private static final int PORT_UNENCRYPTED = 9784;
    private static final int PORT_ENCRYPTED = 9785;
    private static final String SYSTEMID = "smppclient1";
    private static final String PASSWORD = "password";

    private TestSmppServerHandler serverHandler;

    @Before
    public void setUp()  {
        serverHandler = new TestSmppServerHandler();
    }


    private SmppSessionConfiguration createClientConfigurationNoSSL() {
        SmppSessionConfiguration configuration = new SmppSessionConfiguration();
        configuration.setWindowSize(1);
        configuration.setName("Tester.Session.0");
        configuration.setType(SmppBindType.TRANSCEIVER);
        configuration.setHost("localhost");
        configuration.setPort(PORT_UNENCRYPTED);
        configuration.setConnectTimeout(200);
        configuration.setBindTimeout(200);
        configuration.setSystemId(SYSTEMID);
        configuration.setPassword(PASSWORD);
        configuration.getLoggingOptions().setLogBytes(true);
        return configuration;
    }

    private SmppSessionConfiguration createClientConfigurationSSL() {
        SmppSessionConfiguration configuration = createClientConfigurationNoSSL();
        SslConfiguration sslConfig = new SslConfiguration();
        configuration.setUseSsl(true);
        configuration.setSslConfiguration(sslConfig);
        configuration.setPort(PORT_ENCRYPTED);
        return configuration;
    }


    private static class TestSmppServerHandler implements SmppServerHandler {
        private Set<SmppServerSession> sessions = new HashSet<>();
        private final Map<Integer, AtomicInteger> portConnectionCounter = new HashMap<>();

        private TestSmppServerHandler() {
            portConnectionCounter.put(PORT_ENCRYPTED, new AtomicInteger(0));
            portConnectionCounter.put(PORT_UNENCRYPTED, new AtomicInteger(0));
        }

        @Override
        public void sessionBindRequested(Long sessionId, SmppSessionConfiguration sessionConfiguration, final BaseBind bindRequest) throws
                SmppProcessingException {
            if (!SYSTEMID.equals(bindRequest.getSystemId())) {
                throw new SmppProcessingException(SmppConstants.STATUS_INVSYSID);
            }
            if (!PASSWORD.equals(bindRequest.getPassword())) {
                throw new SmppProcessingException(SmppConstants.STATUS_INVPASWD);
            }
        }

        @Override
        public void sessionCreated(Long sessionId, SmppServerSession session, BaseBindResp preparedBindResponse) {
            sessions.add(session);
            Channel channel = ((DefaultSmppSession) session).getChannel();
            portConnectionCounter.get(((InetSocketAddress)channel.getLocalAddress()).getPort()).incrementAndGet();
            session.serverReady(new TestSmppSessionHandler());
        }

        @Override
        public void sessionDestroyed(Long sessionId, SmppServerSession session) {
            sessions.remove(session);
        }
    }

    public static class TestSmppSessionHandler extends DefaultSmppSessionHandler {
        @Override
        public PduResponse firePduRequestReceived(PduRequest pduRequest) {
            return pduRequest.createResponse();
        }
    }

    @Test
    public void connectViaTwoPorts() throws Exception {
        SslConfiguration sslConfig = new SslConfiguration();
        sslConfig.setKeyStorePath("src/test/resources/keystore");
        sslConfig.setKeyStorePassword("changeit");
        sslConfig.setKeyManagerPassword("changeit");
        sslConfig.setTrustStorePath("src/test/resources/keystore");
        sslConfig.setTrustStorePassword("changeit");

        SmppServerConfiguration configuration = new SmppServerConfiguration();
        configuration.setPort(PORT_UNENCRYPTED);
        configuration.setSslPort(PORT_ENCRYPTED);
        configuration.setSystemId("cloudhopper");
        configuration.setUseSsl(true);
        configuration.setSslConfiguration(sslConfig);


        DefaultSmppServer server = new DefaultSmppServer(configuration, serverHandler);
        try {
            server.start();

            DefaultSmppClient clientNoSsl = new DefaultSmppClient();
            DefaultSmppClient clientSsl = new DefaultSmppClient();

            // this should actually work
            SmppSession clientNoSslSession = clientNoSsl.bind(createClientConfigurationNoSSL());
            SmppSession clientSslSession = clientSsl.bind(createClientConfigurationSSL());

            Thread.sleep(200);
            assertEquals(2, serverHandler.portConnectionCounter.size());
            assertEquals(1, serverHandler.portConnectionCounter.get(PORT_ENCRYPTED).get());
            assertEquals(1, serverHandler.portConnectionCounter.get(PORT_UNENCRYPTED).get());
            assertEquals(2, serverHandler.sessions.size());

            clientNoSslSession.close();
            clientSslSession.close();

            Thread.sleep(200);
            assertEquals(0, serverHandler.sessions.size());
        } finally {
            server.destroy();
        }
    }
}
