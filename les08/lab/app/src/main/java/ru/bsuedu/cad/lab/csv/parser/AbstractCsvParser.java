package ru.bsuedu.cad.lab.csv.parser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ru.bsuedu.cad.lab.util.DataProcessingException;

public abstract class AbstractCsvParser<T> implements Parser<T> {

    @Override
    public ArrayList<T> parse(String text) {
        if (text == null) {
            throw new DataProcessingException("CSV text is null");
        }

        ArrayList<T> result = new ArrayList<>();
        if (text.isBlank()) {
            return result;
        }

        String[] lines = text.split("\\R");
        if (lines.length == 0) {
            return result;
        }

        String[] headerCols = lines[0].trim().split(",", -1);
        Map<String, Integer> headerIndex = buildHeaderIndex(headerCols);

        validateRequiredColumns(headerIndex);

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] cols = line.split(",", -1);
            int lineNumber = i + 1;
            result.add(mapRow(cols, headerIndex, lineNumber));
        }

        return result;
    }

    protected abstract String[] requiredColumns();

    protected abstract T mapRow(String[] cols, Map<String, Integer> headerIndex, int lineNumber);

    protected Map<String, Integer> buildHeaderIndex(String[] headerCols) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < headerCols.length; i++) {
            index.put(headerCols[i].trim().toLowerCase(Locale.ROOT), i);
        }
        return index;
    }

    protected void validateRequiredColumns(Map<String, Integer> headerIndex) {
        for (String col : requiredColumns()) {
            if (!headerIndex.containsKey(col)) {
                throw new DataProcessingException("CSV header missing required column: " + col);
            }
        }
    }

    protected String getValue(String[] cols, Map<String, Integer> headerIndex, String columnName) {
        Integer idx = headerIndex.get(columnName);
        if (idx == null || idx < 0 || idx >= cols.length) {
            return "";
        }
        return cols[idx].trim();
    }

    protected <V> V parseValue(
            String value,
            int lineNumber,
            String columnName,
            String typeName,
            ThrowingParser<V> parser) {
        if (value == null || value.isBlank()) {
            throw new DataProcessingException(
                    "Empty " + typeName + " value in column '" + columnName + "' at line " + lineNumber);
        }

        try {
            return parser.parse(value);
        } catch (Exception e) {
            throw new DataProcessingException(
                    "Invalid " + typeName + " in column '" + columnName + "' at line " + lineNumber + ": " + value,
                    e);
        }
    }

    protected Integer parseInt(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "integer", Integer::parseInt);
    }

    protected Long parseLong(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "long", Long::parseLong);
    }

    protected BigDecimal parseBigDecimal(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "decimal", BigDecimal::new);
    }

    protected LocalDate parseDate(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "date",
                v -> LocalDate.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
