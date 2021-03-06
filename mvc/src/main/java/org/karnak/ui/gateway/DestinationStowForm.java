package org.karnak.ui.gateway;

import org.apache.commons.lang3.StringUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.ui.component.converter.HStringToIntegerConverter;
import org.karnak.ui.util.UIS;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A form for editing a single destination.
 */
@SuppressWarnings("serial")
public class DestinationStowForm extends Div {
    private DestinationLogic viewLogic;

    private VerticalLayout content;

    private final TextField description;
    private final TextField url;
    private final TextField urlCredentials;
    private final TextArea headers;

    private final TextField notify;
    private final TextField notifyObjectErrorPrefix;
    private final TextField notifyObjectPattern;
    private final TextField notifyObjectValues;
    private final TextField notifyInterval;

    private Button update;
    private Button discard;
    private Button cancel;
    private Button remove;

    private Binder<Destination> binder;
    private Destination currentDestination;

    public DestinationStowForm(DestinationLogic viewLogic) {
        this.viewLogic = viewLogic;

        setClassName("destination-form");

        content = new VerticalLayout();
        content.setSizeFull();
        add(content);

        description = new TextField("Description");
        description.setWidth("100%");
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

        url = new TextField("URL");
        url.setWidth("50%");
        url.setValueChangeMode(ValueChangeMode.EAGER);
        url.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        UIS.setTooltip(url, "The destination STOW-RS URL");

        urlCredentials = new TextField("URL credentials");
        urlCredentials.setWidth("50%");
        urlCredentials.setValueChangeMode(ValueChangeMode.EAGER);
        urlCredentials.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        UIS.setTooltip(urlCredentials, "Credentials of the STOW-RS service (format is \"user:password\")");

        headers = new TextArea("Headers");
        headers.setMinHeight("10em");
        headers.setWidth("100%");
        headers.setValueChangeMode(ValueChangeMode.EAGER);
        UIS.setTooltip(headers,
                "Headers for HTTP request. Example of format:\n<key>Authorization</key>\n<value>Bearer 1v1pwxT4Ww4DCFzyaMt0NP</value>");

        notify = new TextField("Notif.: list of emails");
        notify.setWidth("100%");
        notify.setValueChangeMode(ValueChangeMode.EAGER);
        notify.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        notify.addValueChangeListener(e -> {
            TextField tf = e.getSource();
            UIS.setTooltip(tf, tf.getValue());
        });

        notifyObjectErrorPrefix = new TextField("Notif.: error subject prefix");
        notifyObjectErrorPrefix.setWidth("24%");
        notifyObjectErrorPrefix.setValueChangeMode(ValueChangeMode.EAGER);
        notifyObjectErrorPrefix.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        UIS.setTooltip(notifyObjectErrorPrefix,
                "Prefix of the email object when containing an issue. Default value: **ERROR**");

        notifyObjectPattern = new TextField("Notif.: subject pattern");
        notifyObjectPattern.setWidth("24%");
        notifyObjectPattern.setValueChangeMode(ValueChangeMode.EAGER);
        notifyObjectPattern.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        UIS.setTooltip(notifyObjectPattern,
                "Pattern of the email object, see https://dzone.com/articles/java-string-format-examples. Default value: [Karnak Notification] %s %.30s");

        notifyObjectValues = new TextField("Notif.: subject values");
        notifyObjectValues.setWidth("24%");
        notifyObjectValues.setValueChangeMode(ValueChangeMode.EAGER);
        notifyObjectValues.addBlurListener(e -> {
            TextField tf = e.getSource();
            if (tf.getValue() != null) {
                tf.setValue(tf.getValue().trim());
            }
        });
        UIS.setTooltip(notifyObjectValues,
                "Values injected in the pattern [PatientID StudyDescription StudyDate StudyInstanceUID]. Default value: PatientID,StudyDescription");

        notifyInterval = new TextField("Notif.: interval");
        notifyInterval.setWidth("18%");
        notifyInterval.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        notifyInterval.setValueChangeMode(ValueChangeMode.EAGER);
        UIS.setTooltip(notifyInterval,
                "Interval in seconds for sending a notification (when no new image is arrived in the archive folder). Default value: 45");

        content.add(UIS.setWidthFull( //
                new HorizontalLayout(description)));
        content.add(UIS.setWidthFull( //
                new HorizontalLayout(url, urlCredentials)));
        content.add(UIS.setWidthFull( //
                headers));
        content.add(UIS.setWidthFull( //
                new HorizontalLayout(notify)));
        content.add(UIS.setWidthFull( //
                new HorizontalLayout(notifyObjectErrorPrefix, notifyObjectPattern, notifyObjectValues,
                        notifyInterval)));

        binder = new BeanValidationBinder<>(Destination.class);
        // Define the same validators as the Destination class, because the validation
        // bean doesn't work in Vaadin
        binder.forField(url) //
                .withValidator( //
                        StringUtils::isNotBlank, //
                        "URL is mandatory") //
                .bind(Destination::getUrl, Destination::setUrl);
        binder.forField(notifyInterval) //
                .withConverter(new HStringToIntegerConverter()) //
                .bind(Destination::getNotifyInterval, Destination::setNotifyInterval);
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
            if (currentDestination != null && binder.writeBeanIfValid(currentDestination)) {
                this.viewLogic.saveDestination(currentDestination);
            }
        });
        update.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button("Discard changes");
        discard.setWidth("100%");
        discard.addClickListener(event -> this.viewLogic.editDestination(currentDestination));

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> this.viewLogic.cancelDestination());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement().addEventListener("keydown", event -> this.viewLogic.cancelDestination())
                .setFilter("event.key == 'Escape'");

        remove = new Button("Remove");
        remove.setWidth("100%");
        remove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        remove.addClickListener(event -> {
            if (currentDestination != null) {
                this.viewLogic.deleteDestination(currentDestination);
            }
        });

        content.add(UIS.setWidthFull( //
                new HorizontalLayout(update, discard, remove, cancel)));
    }

    public void editDestination(Destination data) {
        remove.setVisible(data != null);
        cancel.setVisible(data == null);
        if (data == null) {
            data = Destination.ofStowEmpty();
        }
        currentDestination = data;
        binder.readBean(data);
    }
}
