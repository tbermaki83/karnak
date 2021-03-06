package org.karnak.ui.gateway;

import java.io.Serializable;
import java.util.Collection;

import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.SourceNode;

/**
 * Back-end service interface for retrieving and updating data.
 */
public abstract class DataService implements Serializable {
    private static final long serialVersionUID = -1402338736361739563L;

    public abstract Collection<ForwardNode> getAllForwardNodes();

    public abstract ForwardNode getForwardNodeById(Long dataId);

    public abstract ForwardNode updateForwardNode(ForwardNode data);

    public abstract void deleteForwardNode(Long dataId);

    public abstract Collection<Destination> getAllDestinations(ForwardNode forwardNode);

    public abstract Destination getDestinationById(ForwardNode forwardNode, Long dataId);

    public abstract Destination updateDestination(ForwardNode forwardNode, Destination data);

    public abstract void deleteDestination(ForwardNode forwardNode, Destination data);

    public abstract Collection<SourceNode> getAllSourceNodes(ForwardNode forwardNode);

    public abstract SourceNode getSourceNodeById(ForwardNode forwardNode, Long dataId);

    public abstract SourceNode updateSourceNode(ForwardNode forwardNode, SourceNode data);

    public abstract void deleteSourceNode(ForwardNode forwardNode, SourceNode data);
}
