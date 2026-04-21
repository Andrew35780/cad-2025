package ru.bsuedu.cad.lab.csv.provider;

import java.util.ArrayList;

import ru.bsuedu.cad.lab.csv.dto.CustomerCsvRow;


public interface CustomerProvider {
    public ArrayList<CustomerCsvRow> getCustomers();
}
