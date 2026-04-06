package com.aiFinanceTracker.track.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.entities.IncomeSource;
import com.aiFinanceTracker.track.enums.Category;
import com.aiFinanceTracker.track.repositories.ExpenditureRepository;
import com.aiFinanceTracker.track.repositories.IncomeSourceRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class StatementImportService {

    private final ExpenditureRepository expRepo;
    private final IncomeSourceRepository incomeRepo;

    public StatementImportService(ExpenditureRepository expRepo,
                                  IncomeSourceRepository incomeRepo) {
        this.expRepo = expRepo;
        this.incomeRepo = incomeRepo;
    }

    @Transactional
    public void importSbiCsv(InputStream in) {
        try (CSVReader csv = new CSVReader(new InputStreamReader(in))) {

            String[] row;

            // 1) Skip all lines until we hit the header
            while ((row = csv.readNext()) != null) {
                if (row.length >= 6
                        && "Date".equalsIgnoreCase(row[0].trim())
                        && "Details".equalsIgnoreCase(row[1].trim())) {
                    break; // found header
                }
            }
            if (row == null) {
                // no header -> nothing to import
                return;
            }

            // 2) Data rows
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/uuuu");

            while ((row = csv.readNext()) != null) {
                if (row.length < 6) {
                    continue;
                }

                String dateStr   = row[0].trim();
                String details   = row[1].trim();
                String refNo     = row[2].trim();
                String debitStr  = row[3].trim();
                String creditStr = row[4].trim();
                String balance   = row[5].trim(); // currently unused

                if (dateStr.isEmpty() || "Date".equalsIgnoreCase(dateStr)) {
                    continue;
                }

                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr, df);
                } catch (DateTimeParseException ex) {
                    // if some weird line slips through, skip it
                    continue;
                }

                boolean hasDebit  = !debitStr.isEmpty();
                boolean hasCredit = !creditStr.isEmpty();

                if (hasDebit && !hasCredit) {
                    BigDecimal amount = new BigDecimal(debitStr);
                    Expenditure exp = new Expenditure();
                    exp.setDate(date);
                    exp.setAmount(amount);
                    exp.setDescription(details + (refNo.isEmpty() ? "" : (" (" + refNo + ")")));
                    exp.setCategory(Category.OTHER);
                    expRepo.save(exp);
                } else if (hasCredit && !hasDebit) {
                    BigDecimal amount = new BigDecimal(creditStr);
                    IncomeSource income = new IncomeSource();
                    income.setDate(date); // use your actual date field
                    income.setAmount(amount);
                    income.setName(shorten(details));
                    income.setDescription(details + (refNo.isEmpty() ? "" : (" (" + refNo + ")")));
                    incomeRepo.save(income);
                }
            }

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Failed to import SBI CSV", e);
        }
    }

    private String shorten(String text) {
        return text.length() > 50 ? text.substring(0, 50) : text;
    }
}