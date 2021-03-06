package org.karnak.ui.gateway;

import org.karnak.data.gateway.SourceNode;
import org.karnak.ui.util.UIS;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A form for editing a single soure node.
 */
@SuppressWarnings("serial")
public class SourceNodeForm extends Div {
    private SourceNodeLogic viewLogic;

    private VerticalLayout content;

    private final TextField description;
    private final TextField aeTitle;
    private final TextField hostname;
    private final Checkbox checkHostname;

    private Button update;
    private Button discard;
    private Button cancel;
    private Button remove;

    private Binder<SourceNode> binder;
    private SourceNode currentSourceNode;

    public SourceNodeForm(SourceNodeLogic viewLogic) {
        this.viewLogic = viewLogic;

        setClassName("sourcenode-form");

        content = new VerticalLayout();
        content.setSizeFull();
        add(content);

        aeTitle = new TextField("AETitle");
        aeTitle.setRequired(true);
        aeTitle.setWidth("30%");
        aeTitle.setValueChangeMode(ValueChangeMode.EAGER);
        aeTitle.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });

        description = new TextField("Description");
        description.setWidth("70%");
        description.setValueChangeMode(ValueChangeMode.EAGER);
        description.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        description.addValueChangeListener(e -> {
            TextField tf = e.getSource();
            UIS.setTooltip(tf, tf.getValue());
        });

        hostname = new TextField("Hostname");
        hostname.setWidth("70%");

        hostname.setValueChangeMode(ValueChangeMode.EAGER);
        hostname.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });

        checkHostname = new Checkbox("Check the hostname");
        UIS.setTooltip(checkHostname,
            "if \"true\" check the hostname during the DICOM association and if not match the connection is abort");

        content.add(UIS.setWidthFull(new HorizontalLayout(aeTitle, description)));
        content.add(UIS.setWidthFull(new HorizontalLayout(hostname)));
        content.add(UIS.setWidthFull(checkHostname));

        binder = new BeanValidationBinder<>(SourceNode.class);
        binder.bindInstanceFields(this);

        // enable/disable update button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            update.setEnabled(hasChanges && isValid);
            discard.setEnabled(hasChanges);
            remove.setEnabled(!hasChanges);
        });

        update = new Button("Update");
        update.setWidthFull();
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addClickListener(event -> {
            if (currentSourceNode != null && binder.writeBeanIfValid(currentSourceNode)) {
                this.viewLogic.saveSourceNode(currentSourceNode);
            }
        });
        update.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(event -> this.viewLogic.editSourceNode(currentSourceNode));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> this.viewLogic.cancelSourceNode());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement().addEventListener("keydown", event -> this.viewLogic.cancelSourceNode())
            .setFilter("event.key == 'Escape'");

        remove = new Button("Remove");
        remove.setWidth("100%");
        remove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        remove.addClickListener(event -> {
            if (currentSourceNode != null) {
                this.viewLogic.deleteSourceNode(currentSourceNode);
            }
        });

        content.add(UIS.setWidthFull( //
            new HorizontalLayout(update, discard, remove, cancel)));
    }

    public void editSourceNode(SourceNode data) {
        remove.setVisible(data != null);
        cancel.setVisible(data == null);
        if (data == null) {
            data = SourceNode.ofEmpty();
        }
        currentSourceNode = data;
        binder.readBean(data);
    }
}
