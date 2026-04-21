package ru.bsuedu.cad.lab.renderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.service.CsvDataImportService;


@Component("dataBaseRenderer")
public class DataBaseRenderer implements Renderer{
    
    private final CsvDataImportService importService;


    @Autowired
    public DataBaseRenderer(CsvDataImportService importService) {
        this.importService = importService;
    }

    @Override
    public void render() {
        importService.importAll();
    }
}
