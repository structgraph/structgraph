/* 
 * Copyright (C) 2015 Patrik Duditš <structgraph@dudits.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
