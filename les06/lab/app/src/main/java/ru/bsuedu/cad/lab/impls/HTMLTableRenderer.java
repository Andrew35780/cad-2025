package ru.bsuedu.cad.lab.impls;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.ProductProvider;
import ru.bsuedu.cad.lab.intfs.Renderer;


@Component("HTMLRenderer")
@PropertySource("application.properties")
public class HTMLTableRenderer implements Renderer {

    @Value("#{T(java.nio.file.Path).of('${outputFilePath}')}")
    //@Value("#{property.outputFilePath}")
    private Path outputFilePath;

    private final ProductProvider provider;

    private final String[] headers = {
            "product_id", "name", "description", "category_id",
            "price", "stock_quantity", "image_url", "created_at", "updated_at"
    };

    @Autowired
    public HTMLTableRenderer(ProductProvider provider) {
        this.provider = provider;
    }

    @Override
    public void render() {
        final ArrayList<Product> products = provider.getProducts();

        try {
            Files.createDirectories(outputFilePath.getParent());

            try (BufferedWriter bw = Files.newBufferedWriter(
                    outputFilePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                writeHTMLStart(bw);
                writeTableStart(bw);
                writeHeaderRow(bw);

                for (Product p : products) {
                    writeProductRow(bw, p);
                }

                writeTableEnd(bw);
                writeHTMLEnd(bw);

            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось записать HTML-файл: " + outputFilePath, e);
        }
    }

    private void writeHTMLStart(BufferedWriter writer) throws IOException {
        writer.write("""
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Products</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            margin: 20px;
                        }
                        table {
                            border-collapse: collapse;
                            width: 100%;
                        }
                        th, td {
                            border: 1px solid #333;
                            padding: 8px 10px;
                            text-align: left;
                            vertical-align: top;
                        }
                        th {
                            background: #f2f2f2;
                        }
                    </style>
                </head>
                <body>
                <h1>Products</h1>
                """);
    }

    private void writeTableStart(BufferedWriter writer) throws IOException {
        writer.write("<table>\n");
    }

    private void writeHeaderRow(BufferedWriter writer) throws IOException {
        writer.write("<tr>");
        for (String header : headers) {
            writer.write("<th>");
            writer.write(escapeHTML(header));
            writer.write("</th>");
        }
        writer.write("</tr>\n");
    }

    private void writeProductRow(BufferedWriter writer, Product p) throws IOException {
        writer.write("<tr>");

        writeCell(writer, String.valueOf(p.getProductId()));
        writeCell(writer, p.getName());
        writeCell(writer, p.getDescription());
        writeCell(writer, String.valueOf(p.getCategoryId()));
        writeCell(writer, String.format("%.2f", p.getPrice()));
        writeCell(writer, String.valueOf(p.getStockQuantity()));
        writeCell(writer, p.getImageUrl());
        writeCell(writer, String.format("%tF", p.getCreatedAt()));
        writeCell(writer, String.format("%tF", p.getUpdatedAt()));

        writer.write("</tr>\n");
    }

    private void writeCell(BufferedWriter writer, String value) throws IOException {
        writer.write("<td>");
        writer.write(escapeHTML(value));
        writer.write("</td>");
    }

    private void writeTableEnd(BufferedWriter writer) throws IOException {
        writer.write("</table>\n");
    }

    private void writeHTMLEnd(BufferedWriter writer) throws IOException {
        writer.write("""
                </body>
                </html>
                """);
    }

    private String escapeHTML(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
