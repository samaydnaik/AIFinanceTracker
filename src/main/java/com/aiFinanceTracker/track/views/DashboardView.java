package com.aiFinanceTracker.track.views;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.repositories.ExpenditureRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")  // Root path
public class DashboardView extends VerticalLayout {
    
    public DashboardView(ExpenditureRepository repo) {
        add(new H1("AI Finance Tracker"));
        add(new Span("Expenditures count: " + repo.count()));
        Grid<Expenditure> grid = new Grid<>(Expenditure.class);
        grid.setItems(repo.findAll());
        add(grid);
    }
}
