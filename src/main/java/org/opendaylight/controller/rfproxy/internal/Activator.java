package org.opendaylight.controller.rfproxy.internal;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;

import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.switchmanager.IInventoryListener;

import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.sal.packet.IListenDataPacket;

import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;

import org.opendaylight.controller.bgpsec.IBGPSecHandler;

import org.apache.felix.dm.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator extends ComponentActivatorAbstractBase {
    protected static final Logger logger = LoggerFactory
            .getLogger(Activator.class);

    /**
     * Function that is used to communicate to dependency manager the
     * list of known implementations for services inside a container
     *
     *
     * @return An array containing all the CLASS objects that will be
     * instantiated in order to get a fully working implementation Object
     */
    public Object[] getImplementations() {
        Object[] res = { RFProxy.class };
        return res;
    }
    
    /**
     * Function that is called when configuration of the dependencies
     * is required.
     *
     * @param c dependency manager Component object, used for
     * configuring the dependencies exported and imported
     * @param imp Implementation class that is being configured,
     * needed as long as the same routine can configure multiple
     * implementations
     * @param containerName The containerName being configured, this allow
     * also optional per-container different behavior if needed, usually
     * should not be the case though.
     */
    public void configureInstance(Component c, Object imp, String containerName) {
        if (imp.equals(RFProxy.class)) {

            //Name is required to register IListenDataPacket.class 
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("salListenerName", "rfproxy");
            props.put("salListenerDependency", "bgpsec");

            //Register Listeners
            c.setInterface(new String[] { IInventoryListener.class.getName(),
                                IListenDataPacket.class.getName() }, props);

            //Create container dependencies
            c.add(createContainerServiceDependency(containerName).
                    setService(IBGPSecHandler.class).
                    setCallbacks("setBGPSecHandler", "unsetBGPSecHandler").
                    setRequired(true));

            c.add(createContainerServiceDependency(containerName).
                    setService(IFlowProgrammerService.class).
                    setCallbacks("setFlowProgrammerService", "unsetFlowProgrammerService").
                    setRequired(true));

            c.add(createContainerServiceDependency(containerName).
                    setService(ISwitchManager.class).
                    setCallbacks("setSwitchManager", "unsetSwitchManager").
                    setRequired(true));

            c.add(createContainerServiceDependency(containerName).
                    setService(IDataPacketService.class).
                    setCallbacks("setDataPacketService", "unsetDataPacketService").
                    setRequired(true));
            
        }
    }
}
