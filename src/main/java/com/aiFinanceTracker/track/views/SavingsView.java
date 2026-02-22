package com.aiFinanceTracker.track.views;

import com.aiFinanceTracker.track.entities.Savings;
import com.aiFinanceTracker.track.repositories.SavingsRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("savings")
public class SavingsView extends VerticalLayout {

    private final SavingsRepository repo;
    private final Grid<Savings> grid = new Grid<>(Savings.class);
    private final SavingsForm form;

    public SavingsView(SavingsRepository repo) {
        this.repo = repo;
        this.form = new SavingsForm(this::saveSavings);

        setSizeFull();

        grid.setColumns("id", "amount", "type", "date",
                        "maturityDate", "interestRate");
        grid.addComponentColumn(s ->
                new Button("Delete", e -> deleteSavings(s))
        ).setHeader("Actions");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        grid.setSizeFull();

        add(nav(), new H1("Savings"), content);

        refreshGrid();
    }

    private HorizontalLayout nav() {
        return new HorizontalLayout(
            new RouterLink("Dashboard", DashboardView.class),
            new RouterLink("Income", IncomeView.class),
            new RouterLink("Savings", SavingsView.class)
        );
    }

    private void saveSavings(Savings s) {
        repo.save(s);
        refreshGrid();
    }

    private void deleteSavings(Savings s) {
        repo.delete(s);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(repo.findAll());
    }
}
