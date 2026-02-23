package com.aiFinanceTracker.track.views;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.repositories.ExpenditureRepository;
import com.aiFinanceTracker.track.repositories.IncomeSourceRepository;
import com.aiFinanceTracker.track.repositories.SavingsRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")   // root path -> dashboard
public class DashboardView extends VerticalLayout {

    private final ExpenditureRepository expRepo;
    private final IncomeSourceRepository incomeRepo;
    private final SavingsRepository savingsRepo;
    private final DatePicker fromDate = new DatePicker("From");
    private final DatePicker toDate = new DatePicker("To");

    private final Grid<Expenditure> grid = new Grid<>(Expenditure.class);
    private final ExpenditureForm form;
    private HorizontalLayout summaryLayout;


    public DashboardView(ExpenditureRepository expRepo,
                         IncomeSourceRepository incomeRepo,
                         SavingsRepository savingsRepo) {
        this.expRepo = expRepo;
        this.incomeRepo = incomeRepo;
        this.savingsRepo = savingsRepo;

        setSizeFull();
        setSpacing(true);
        
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        fromDate.setValue(monthStart);
        toDate.setValue(today);

        fromDate.addValueChangeListener(e -> updateSummaryAndGrid());
        toDate.addValueChangeListener(e -> updateSummaryAndGrid());

        HorizontalLayout filters = new HorizontalLayout(fromDate, toDate);
        filters.setAlignItems(Alignment.END);

        
        // Navigation bar
        HorizontalLayout nav = new HorizontalLayout(
            new RouterLink("Dashboard", DashboardView.class),
            new RouterLink("Income", IncomeView.class),
            new RouterLink("Savings", SavingsView.class)
        );

        H1 title = new H1("AI Finance Tracker");

        // Summary cards
        HorizontalLayout summary = buildSummary();

        // Grid + form (Expenditures CRUD)
        configureGrid();
        form = new ExpenditureForm(this::saveExpenditure);
        form.setWidth("400px");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        grid.setSizeFull();

        add(nav, title, summary, content);
        this.summaryLayout = summary;
        refreshGrid();
    }

    private BigDecimal defaultZero(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    private Div summaryCard(String title, BigDecimal value) {
        Div card = new Div();
        card.getStyle()
            .set("border", "1px solid var(--lumo-contrast-20pct)")
            .set("border-radius", "8px")
            .set("padding", "1rem")
            .set("min-width", "180px");

        H3 t = new H3(title);
        Span v = new Span("₹ " + value.setScale(2, RoundingMode.HALF_UP));
        v.getStyle().set("font-size", "1.4rem").set("font-weight", "600");

        card.add(t, v);
        return card;
    }

    private void configureGrid() {
        grid.setColumns("id", "amount", "category", "date", "merchant");
        grid.addComponentColumn(exp ->
            new Button("Delete", e -> deleteExpenditure(exp))
        ).setHeader("Actions");
    }

    private void saveExpenditure(Expenditure exp) {
        expRepo.save(exp);
        refreshGrid();
    }

    private void deleteExpenditure(Expenditure exp) {
        expRepo.delete(exp);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(expRepo.findAll());
    }
    
    private void updateSummaryAndGrid() {
        // rebuild summary
        remove(summaryLayout);
        summaryLayout = buildSummary();
        addComponentAtIndex(3, summaryLayout); // index: nav(0), title(1), filters(2), summary(3)

        // filter expenditures grid by date range
        LocalDate start = fromDate.getValue();
        LocalDate end = toDate.getValue();
        if (start != null && end != null && !end.isBefore(start)) {
            grid.setItems(expRepo.findByDateBetween(start, end));
        } else {
            refreshGrid();
        }
    }
    
    private HorizontalLayout buildSummary() {
        LocalDate start = fromDate.getValue();
        LocalDate end = toDate.getValue();
        if (start == null || end == null || end.isBefore(start)) {
            return new HorizontalLayout(summaryCard("Select valid dates", BigDecimal.ZERO));
        }

        BigDecimal totalExpenses = defaultZero(expRepo.totalSpentBetween(start, end));
        BigDecimal totalIncome = defaultZero(incomeRepo.totalIncomeUpTo(end)); // or between
        BigDecimal totalSavings = defaultZero(savingsRepo.totalSavings());
        BigDecimal net = totalIncome.subtract(totalExpenses);

        HorizontalLayout summary = new HorizontalLayout(
            summaryCard("Income", totalIncome),
            summaryCard("Expenses", totalExpenses),
            summaryCard("Net", net),
            summaryCard("Total Savings", totalSavings)
        );
        summary.setWidthFull();
        return summary;
    }


}
