package ru.bsuedu.cad.lab.csv.parser;

import java.util.Map;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.csv.dto.CustomerCsvRow;


@Component("customerParser")
public class CustomerCsvParser extends AbstractCsvParser<CustomerCsvRow> {
    private static final String[] REQUIRED_COLUMNS = {
            "customer_id",
            "name",
            "email",
            "phone",
            "address"
    };

    @Override
    protected String[] requiredColumns() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected CustomerCsvRow mapRow(String[] cols, Map<String, Integer> headerIndex, int lineNumber) {
        Long customerId = parseLong(getValue(cols, headerIndex, "customer_id"), lineNumber, "customer_id");
        String name = getValue(cols, headerIndex, "name");
        String email = getValue(cols, headerIndex, "email");
        String phone = getValue(cols, headerIndex, "phone");
        String address = getValue(cols, headerIndex, "address");

        return new CustomerCsvRow(customerId, name, email, phone, address);
    }
}
