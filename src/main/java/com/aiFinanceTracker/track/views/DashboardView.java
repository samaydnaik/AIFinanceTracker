package com.aiFinanceTracker.track.views;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.interfaces.FinanceSummaryService;
import com.aiFinanceTracker.track.repositories.ExpenditureRepository;
import com.aiFinanceTracker.track.repositories.IncomeSourceRepository;
import com.aiFinanceTracker.track.repositories.SavingsRepository;
import com.aiFinanceTracker.track.service.FinanceAiService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
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
    private final FinanceSummaryService financeSummaryService;

    private final DatePicker fromDate = new DatePicker("From");
    private final DatePicker toDate = new DatePicker("To");
    
    private final Button analyzeButton = new Button("Analyze period");
    private final Grid<Expenditure> grid = new Grid<>(Expenditure.class);
    private final ExpenditureForm form;
    private HorizontalLayout summaryLayout;
    private final FinanceAiService aiService;

    public DashboardView(ExpenditureRepository expRepo,
                         IncomeSourceRepository incomeRepo,
                         SavingsRepository savingsRepo,
                         FinanceAiService aiService, FinanceSummaryService financeSummaryService) {
        this.expRepo = expRepo;
        this.incomeRepo = incomeRepo;
        this.savingsRepo = savingsRepo;
        this.aiService = aiService;
        this.financeSummaryService = financeSummaryService;

        setSizeFull();
        setSpacing(true);
        
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        fromDate.setValue(monthStart);
        toDate.setValue(today);
        
        analyzeButton.addClickListener(e -> analyzePeriod());
        fromDate.addValueChangeListener(e -> updateSummaryAndGrid());
        toDate.addValueChangeListener(e -> updateSummaryAndGrid());

        HorizontalLayout filters = new HorizontalLayout(fromDate, toDate, analyzeButton);
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
        
        Button importButton = new Button("Import SBI CSV",
                e -> UI.getCurrent().navigate("import"));
        add(importButton);

        // Grid + form (Expenditures CRUD)
        configureGrid();
        form = new ExpenditureForm(this::saveExpenditure);
        form.setWidth("400px");

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        grid.setSizeFull();

        add(nav, title, filters, summary, content);
        this.summaryLayout = summary;
        refreshGrid();
    }
    
    private void analyzePeriod() {
        LocalDate start = fromDate.getValue();
        LocalDate end = toDate.getValue();
        if (start == null || end == null || end.isBefore(start)) {
            Notification.show("Select a valid From/To range");
            return;
        }

        String analysis;
        try {
            analysis = aiService.analyzeSpending(start, end);
        } catch (Exception ex) {
            Notification.show("AI analysis failed: " + ex.getMessage(), 5000,
                              Notification.Position.MIDDLE);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("400px");

        H3 title = new H3("Spending analysis");
        Paragraph content = new Paragraph(analysis);
        content.getStyle().set("white-space", "pre-wrap");

        Button close = new Button("Close", e -> dialog.close());

        VerticalLayout layout = new VerticalLayout(title, content, close);
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);

        dialog.add(layout);
        dialog.open();
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
        BigDecimal net = defaultZero(financeSummaryService.getNetSavings(start, end));

        // If your FinanceSummaryService also has a totalSavings(from,to),
        // use that; otherwise keep the repo call as-is for now:
        BigDecimal totalSavings = defaultZero(savingsRepo.totalSavings());

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
