package org.karnak.ui.gateway;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.karnak.data.gateway.ForwardNode;
import org.karnak.data.gateway.SourceNode;

import com.vaadin.flow.data.provider.ListDataProvider;

@SuppressWarnings("serial")
public class SourceNodeDataProvider extends ListDataProvider<SourceNode> {
    private final DataService dataService;
    private Set<SourceNode> backend;
    private boolean hasChanges;

    private ForwardNode forwardNode; // Current forward node

    /** Text filter that can be changed separately. */
    private String filterText = "";

    public SourceNodeDataProvider(DataService dataService) {
        this(dataService, new HashSet<>());
    }

    private SourceNodeDataProvider(DataService dataService, Set<SourceNode> backend) {
        super(backend);
        this.dataService = dataService;
        this.backend = backend;
    }

    public void setForwardNode(ForwardNode forwardNode) {
        this.forwardNode = forwardNode;
        Collection<SourceNode> sourceNodes = this.dataService.getAllSourceNodes(forwardNode);

        this.backend.clear();
        this.backend.addAll(sourceNodes);

        hasChanges = false;
    }

    /**
     * Store given SourceNode to the backing data service.
     * 
     * @param data the updated or new data
     */
    public void save(SourceNode data) {
        boolean newData = data.isNewData();

        SourceNode dataUpdated = this.dataService.updateSourceNode(forwardNode, data);
        if (newData) {
            refreshAll();
        } else {
            refreshItem(dataUpdated);
        }
        hasChanges = true;
    }

    /**
     * Delete given data from the backing data service.
     * 
     * @param data the data to be deleted
     */
    public void delete(SourceNode data) {
        this.dataService.deleteSourceNode(forwardNode, data);
        refreshAll();
        hasChanges = true;
    }

    /**
     * Sets the filter to use for this data provider and refreshes data.
     * <p>
     * Filter is compared for allowed properties.
     * 
     * @param filterTextInput the text to filter by, never null.
     */
    public void setFilter(String filterTextInput) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");

        final String filterText = filterTextInput.trim();

        if (Objects.equals(this.filterText, filterText)) {
            return;
        }
        this.filterText = filterText;

        setFilter(data -> matchesFilter(data, filterText));
    }

    @Override
    public Object getId(SourceNode data) {
        Objects.requireNonNull(data, "Cannot provide an id for a null item.");

        return data.hashCode();
    }

    private boolean matchesFilter(SourceNode data, String filterText) {
        return data != null && data.matchesFilter(filterText);
    }

    @Override
    public void refreshAll() {
        backend.clear();
        if (forwardNode != null) {
            backend.addAll(forwardNode.getSourceNodes());
        }
        super.refreshAll();
    }

    public boolean hasChanges() {
        return hasChanges;
    }
}
