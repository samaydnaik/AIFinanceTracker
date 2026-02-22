package com.aiFinanceTracker.track.views;

import com.aiFinanceTracker.track.entities.IncomeSource;
import com.aiFinanceTracker.track.repositories.IncomeSourceRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("income")
public class IncomeView extends VerticalLayout {

    private final IncomeSourceRepository repo;
    private final Grid<IncomeSource> grid = new Grid<>(IncomeSource.class);
    private final IncomeForm form;

    public IncomeView(IncomeSourceRepository repo) {
        this.repo = repo;
        this.form = new IncomeForm(this::saveIncome);

        grid.setColumns("id", "name", "amount", "frequency", "startDate");
        grid.addComponentColumn(i ->
            new Button("Delete", e -> deleteIncome(i))
        ).setHeader("Actions");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();

        add(new H1("Income Sources"), content);
        setSizeFull();
        refreshGrid();
    }

    private void saveIncome(IncomeSource income) {
        repo.save(income);
        refreshGrid();
    }

    private void deleteIncome(IncomeSource income) {
        repo.delete(income);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(repo.findAll());
    }
}
