package com.aiFinanceTracker.track.views;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.aiFinanceTracker.track.entities.Savings;
import com.aiFinanceTracker.track.enums.SavingsType;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;

public class SavingsForm extends FormLayout {

    private final NumberField amount = new NumberField("Amount");
    private final ComboBox<SavingsType> type = new ComboBox<>("Type");
    private final DatePicker date = new DatePicker("Date");
    private final DatePicker maturityDate = new DatePicker("Maturity date");
    private final NumberField interestRate =
            new NumberField("Interest rate (%)");

    private final Button save = new Button("Save");

    private final Binder<Savings> binder = new Binder<>(Savings.class);

    public interface SaveListener {
        void onSave(Savings savings);
    }

    private final SaveListener listener;

    public SavingsForm(SaveListener listener) {
        this.listener = listener;

        amount.setMin(0);
        interestRate.setMin(0);
        type.setItems(SavingsType.values());
        date.setValue(LocalDate.now());

        add(amount, type, date, maturityDate, interestRate, save);

        binder.bindInstanceFields(this);
        binder.setBean(new Savings());

        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                Savings s = binder.getBean();
                s.setAmount(BigDecimal.valueOf(
                        amount.getValue() == null ? 0 : amount.getValue()));
                s.setInterestRate(BigDecimal.valueOf(
                        interestRate.getValue() == null ? 0 : interestRate.getValue()));
                listener.onSave(s);
                clearForm();
            }
        });
    }

    public void setSavings(Savings s) {
        if (s == null) {
            binder.setBean(new Savings());
            clearForm();
        } else {
            binder.setBean(s);
            amount.setValue(s.getAmount() == null
                    ? 0d : s.getAmount().doubleValue());
            type.setValue(s.getType());
            date.setValue(s.getDate());
            maturityDate.setValue(s.getMaturityDate());
            interestRate.setValue(s.getInterestRate() == null
                    ? 0d : s.getInterestRate().doubleValue());
        }
    }

    private void clearForm() {
        binder.setBean(new Savings());
        amount.clear();
        type.clear();
        date.setValue(LocalDate.now());
        maturityDate.clear();
        interestRate.clear();
    }
}
