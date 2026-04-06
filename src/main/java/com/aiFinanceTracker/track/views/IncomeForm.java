package com.aiFinanceTracker.track.views;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.aiFinanceTracker.track.entities.IncomeSource;
import com.aiFinanceTracker.track.enums.Frequency;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class IncomeForm extends FormLayout {

    private final TextField name = new TextField("Name");
    private final NumberField amount = new NumberField("Amount");
    private final ComboBox<Frequency> frequency =
            new ComboBox<>("Frequency");
    private final DatePicker date = new DatePicker("Start date");

    private final Button save = new Button("Save");

    private final Binder<IncomeSource> binder =
            new Binder<>(IncomeSource.class);

    public interface SaveListener {
        void onSave(IncomeSource income);
    }

    private final SaveListener listener;

    public IncomeForm(SaveListener listener) {
        this.listener = listener;

        amount.setMin(0);
        frequency.setItems(Frequency.values());
        date.setValue(LocalDate.now());

        add(name, amount, frequency, date, save);

        binder.bindInstanceFields(this);
        binder.setBean(new IncomeSource());

        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                IncomeSource income = binder.getBean();
                income.setAmount(BigDecimal.valueOf(
                        amount.getValue() == null ? 0 : amount.getValue()));
                listener.onSave(income);
                clearForm();
            }
        });
    }

    public void setIncome(IncomeSource income) {
        if (income == null) {
            binder.setBean(new IncomeSource());
            clearForm();
        } else {
            binder.setBean(income);
            name.setValue(income.getName() == null ? "" : income.getName());
            amount.setValue(income.getAmount() == null
                    ? 0d : income.getAmount().doubleValue());
            frequency.setValue(income.getFrequency());
            date.setValue(income.getDate());
        }
    }

    private void clearForm() {
        binder.setBean(new IncomeSource());
        name.clear();
        amount.clear();
        frequency.clear();
        date.setValue(LocalDate.now());
    }
}
