package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.intfs.ProductProvider;
import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.Renderer;


@Component("ConsoleRenderer")
public class ConsoleTableRenderer implements Renderer {

    private final ProductProvider provider;

    private final String[] headers = {
            "product_id", "name", "description", "category_id",
            "price", "stock_quantity", "image_url", "created_at", "updated_at"
        };

    @Autowired
    public ConsoleTableRenderer(ProductProvider provider) {
        this.provider = provider;
    }

    @Override
    public void render() {
        final ArrayList<Product> products = provider.getProducts();

        int[] widths = calcWidths(products);

        printBorder(widths);
        printRow(headers, widths);
        printBorder(widths);

        for (Product p : products) {
            String[] row = {
                    String.valueOf(p.productId),
                    p.name,
                    p.description,
                    String.valueOf(p.categoryId),
                    String.format("%.2f", p.price),
                    String.valueOf(p.stockQuantity),
                    p.imageUrl,
                    String.format("%tF", p.createdAt),
                    String.format("%tF", p.updatedAt),
            };
            printRow(row, widths);
        }

        printBorder(widths);
    }

    private void printBorder(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int width : widths) {
            sb.append("-".repeat(width + 2)).append("+");
        }
        System.out.println(sb);
    }

    private void printRow(String[] values, int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (int i = 0; i < values.length; i++) {
            sb.append(" ")
              .append(String.format("%-" + widths[i] + "s", values[i]))
              .append(" |");
        }
        System.out.println(sb);
    }

    private int[] calcWidths(ArrayList<Product> products) {
        int[] widths = new int[headers.length];
        
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }

        for (Product p : products) {
            widths[0] = Math.max(widths[0], String.valueOf(p.productId).length());
            widths[1] = Math.max(widths[1], p.name.length());
            widths[2] = Math.max(widths[2], p.description.length());
            widths[3] = Math.max(widths[3], String.valueOf(p.categoryId).length());
            widths[4] = Math.max(widths[4], String.format("%.2f", p.price).length());
            widths[5] = Math.max(widths[5], String.valueOf(p.stockQuantity).length());
            widths[6] = Math.max(widths[6], p.imageUrl.length());
        }

        return widths;
    }
}