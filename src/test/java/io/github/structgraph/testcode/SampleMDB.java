/* 
 * Copyright 2015 Patrik Duditš <structgraph@dudits.net>.
 *
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
 */
package io.github.structgraph.testcode;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.enterprise.context.Initialized;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author Patrik Duditš
 */
@MessageDriven(
        activationConfig = {
                        @ActivationConfigProperty(propertyName = "destinationType",
                                propertyValue = "topic"),
                        @ActivationConfigProperty(propertyName = "destination", propertyValue = "lm.session"),
                        @ActivationConfigProperty(propertyName = "durability", propertyValue = "durable"),
                        @ActivationConfigProperty(
                                propertyName = "subscriptionName",
                                propertyValue = "com.rwe.icon.oc.processing.chargesession.DiscoverSessionConstraintsProcessor"),
                        @ActivationConfigProperty(
                                propertyName = "clientId",
                                propertyValue = "com.rwe.icon.oc.processing.chargesession.DiscoverSessionConstraintsProcessor")},
        messageListenerInterface = MessageListener.class        
 )
@AccessTimeout(value = 2, unit = TimeUnit.HOURS)
public class SampleMDB implements MessageListener {

    @EJB(lookup = "cms")
    ChargeManagementService chargeManagementService;

    List<String> strings;

    Map<String, File> mapping;
    
    @Override
    public void onMessage(Message message) {
        try {
            chargeManagementService.process(message.getBody(String.class));
        } catch (JMSException ex) {
            Logger.getLogger(SampleMDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static class Parametrized implements Callable<Integer>, Comparable<Parametrized> {
        @Override
        public Integer call() throws Exception {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int compareTo(Parametrized o) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class Parametrized2<T extends Number> extends AbstractMap<Integer, T> {

        @Override
        public Set<Entry<Integer, T>> entrySet() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <X extends Throwable> void magic() throws X {

        }
    }

    
}
