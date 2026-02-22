package com.aiFinanceTracker.track.views;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.enums.Category;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class ExpenditureForm extends FormLayout {

    private final NumberField amount = new NumberField("Amount");
    private final TextField merchant = new TextField("Merchant");
    private final DatePicker date = new DatePicker("Date");
    private final ComboBox<Category> category = new ComboBox<>("Category");

    private final Button save = new Button("Save");

    private final Binder<Expenditure> binder = new Binder<>(Expenditure.class);

    public interface SaveListener {
        void onSave(Expenditure expenditure);
    }

    public ExpenditureForm(SaveListener listener) {
        amount.setMin(0);
        date.setValue(LocalDate.now());

        category.setItems(Category.values());
        category.setItemLabelGenerator(Enum::name);

        add(amount, merchant, category, date, save);

        binder.bindInstanceFields(this); // now types match: Category ↔ ComboBox<Category>
        binder.setBean(new Expenditure());

        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                Expenditure exp = binder.getBean();
                exp.setAmount(BigDecimal.valueOf(amount.getValue() == null ? 0 : amount.getValue()));
                listener.onSave(exp);
                binder.setBean(new Expenditure());
                amount.clear();
                merchant.clear();
                category.clear();
                date.setValue(LocalDate.now());
            }
        });
    }
    
    public void setExpenditure(Expenditure exp) {
        if (exp == null) {
            binder.setBean(new Expenditure());
        } else {
            binder.setBean(exp);
            amount.setValue(exp.getAmount() != null ? exp.getAmount().doubleValue() : 0d);
            category.setValue(exp.getCategory());
            date.setValue(exp.getDate());
            merchant.setValue(exp.getMerchant());
        }
    }


}
