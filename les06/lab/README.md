# Отчет о лабораторной работе №3

## Цель работы
В данной работе необходимо научить приложение сохранять данные в базе данных. А также научить приложение выполнять SQL запросы и выводить их результаты в логи. В этом  поможет механизм JDBC (Java Database Connectivity), и такие инструменты Spring как DataSource, JDBCTemplate, RowMapper упрощающие работу с JDBC.


## Выполнение работы

**Скопировал результат выполнения лабораторной работы №2 в директорию [/les06/lab/](/les06/lab/).**

---

**Подключил к приложению встраиваемую базу данных H2 используя EmbeddedDatabaseBuilder.**

В класс `AppConfig` был добавлен метод по созданию бина DataSource через EmbeddedDatabaseBuilder, которму задаётся тип базы H2, задаётся имя и добавляется скрипт инициализации структуры. Код класса представлен ниже:

```java
// AppConfig.java
 @Bean
    public DataSource dataSource() {
        var dbBuilder = new EmbeddedDatabaseBuilder();
        return dbBuilder.setType(EmbeddedDatabaseType.H2)
                .setName("market.db")
                .addScript("classpath:db/schema.sql")
                .build();
    }

```

---

**Написал SQL скрипт создающий две таблицы "Продукты" (PRODUCTS) и "Категории" (CATEGORIES), добавляющий ограничения на столбцы и задающий внешний ключ. Код скрипта представлен ниже:**

```sql
CREATE TABLE IF NOT EXISTS CATEGORIES (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS PRODUCTS (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT,
    price DECIMAL NOT NULL CHECK (price >= 0),
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    image_url VARCHAR(500),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE,

    FOREIGN KEY (category_id) REFERENCES CATEGORIES(category_id) ON DELETE SET NULL
);
```

---

**Настроил EmbeddedDatabaseBuilder так, чтобы он при старте приложения выполнял данный скрипт и создавал в базе данных таблицы CATEGORIES и PRODUCTS, с помощью метода `.addScript("classpath:db/schema.sql")`.**

---

**Для таблицы "Категории" создал Java класс Category, для моделирования данной сущности (аналогичный классу Product). И класс ConcreteCategoryProvider, аналогичный ConcreteProductProvider, данный класс предоставляет данные из CSV файла category.csv. CSV-файл располагается в директории src/main/resources приложения.**

Класс `Category` представляет собой обычный класс для хранения информации о категории, содержит приватные поля, конструктор, и методы для получения и установки значений полей. Код класса представлен ниже:

```java
// Category.java
package ru.bsuedu.cad.lab;


public class Category {
    private long categoryId;
    private String name;
    private String description;

    public Category(long categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }
    

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
}
```

Вместе с добавлением новой сущности, возникла необходимость в модификации связанных компонентов.

Класс `CSVParser` был завязан на сущности Product, поэтому было принято решение вынести основную логику парсинга файла в абстрактный класс, и создать отдельные классы для парсинга Product и Category, которые содержат поля с требуемыми заголовками и метод маппинга строки в конкретный объект.

Интерфейс `Parser` был сделан параметризованным (*generic*), как и его метод `parse()`, который теперь возвращает обобщенный список. Код интерфейса представлен ниже:

```java
// Parser.java
package ru.bsuedu.cad.lab.intfs;

import java.util.ArrayList;


public interface Parser<T> {
    public ArrayList<T> parse(String str);
}

```

Класс `AbstractCSVParser`, как было сказано ранее, содержит базовую абстрактную функциональность по парсингу файла, которая схожа с той, что содержалась ранее в классе `CSVParser`, и имплементируют описанный выше интерфейс `Parser`. Некоторые методы претерпели небольшие изменения и стали дженериками для работы с различными типами данных. Код класса представлен ниже:

```java
// AbstractCSVParser.java
package ru.bsuedu.cad.lab.impls;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ru.bsuedu.cad.lab.DataProcessingException;
import ru.bsuedu.cad.lab.intfs.Parser;
import ru.bsuedu.cad.lab.intfs.ThrowingParser;


public abstract class AbstractCSVParser<T> implements Parser<T> {

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

    protected int parseInt(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "integer", Integer::parseInt);
    }

    protected long parseLong(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "long", Long::parseLong);
    }

    protected BigDecimal parseBigDecimal(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "decimal", BigDecimal::new);
    }

    protected java.util.Date parseDate(String value, SimpleDateFormat formatter, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "date", formatter::parse);
    }
}
```

Класс `CSVCategoryParser` является своеобразной оберткой для конкретнной сущности. Он расширяет абстрактный класс, описанный выше, задавая ему конкретный параметризованный тип. Содержит константный массив требуемых заголовков, геттер для их получения и перегруженный базовый метод `mapRow()`, который парсит столбцы в переменные и создаёт объект Category. Код класса представлен ниже:

```java
// CSVCategoryParser.java
package ru.bsuedu.cad.lab.impls;

import java.util.Map;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Category;


@Component("categoryParser")
public class CSVCategoryParser extends AbstractCSVParser<Category> {

    private static final String[] REQUIRED_COLUMNS = {
            "category_id",
            "name",
            "description"
    };

    @Override
    protected String[] requiredColumns() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected Category mapRow(String[] cols, Map<String, Integer> headerIndex, int lineNumber) {
        int categoryId = parseInt(getValue(cols, headerIndex, "category_id"), lineNumber, "category_id");
        String name = getValue(cols, headerIndex, "name");
        String description = getValue(cols, headerIndex, "description");

        return new Category(categoryId, name, description);
    }
}
```

Класс `CSVProductParser` аналогичен классу `CSVCategoryParser`, за исключением отличающегося поля с заголовками и метода маппинга, который, в данном случае, парсит столбцы в другие требуемые переменные и создаёт объект типа Product. Код класса представлен ниже:

```java
// CSVProductParser.java
package ru.bsuedu.cad.lab.impls;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Product;


@Component("productParser")
public class CSVProductParser extends AbstractCSVParser<Product> {

    private static final String[] REQUIRED_COLUMNS = {
            "product_id",
            "name",
            "description",
            "category_id",
            "price",
            "stock_quantity",
            "image_url",
            "created_at",
            "updated_at"
    };

    @Override
    protected String[] requiredColumns() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected Product mapRow(String[] cols, Map<String, Integer> headerIndex, int lineNumber) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

        long productId = parseLong(getValue(cols, headerIndex, "product_id"), lineNumber, "product_id");
        String name = getValue(cols, headerIndex, "name");
        String description = getValue(cols, headerIndex, "description");
        int categoryId = parseInt(getValue(cols, headerIndex, "category_id"), lineNumber, "category_id");
        BigDecimal price = parseBigDecimal(getValue(cols, headerIndex, "price"), lineNumber, "price");
        int stockQuantity = parseInt(getValue(cols, headerIndex, "stock_quantity"), lineNumber, "stock_quantity");
        String imageUrl = getValue(cols, headerIndex, "image_url");
        Date createdAt = parseDate(getValue(cols, headerIndex, "created_at"), formatter, lineNumber, "created_at");
        Date updatedAt = parseDate(getValue(cols, headerIndex, "updated_at"), formatter, lineNumber, "updated_at");

        return new Product(
                productId,
                name,
                description,
                categoryId,
                price,
                stockQuantity,
                imageUrl,
                createdAt,
                updatedAt);
    }
}
```

Класс `ResourceFileReader` ранее брал конкретную строку с именем входного файла из компонента конфигурации, теперь же такой способ не подходил, т.к. класс может использоваться для чтения различных файлов, поэтому теперь поле с именем файла задаётся в конструкторе, что позволяет создавать и использовать объект `Reader` для конкретного файла. Код класса представлен ниже:  

```java
// ResourceFileReader.java
package ru.bsuedu.cad.lab.impls;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bsuedu.cad.lab.App;
import ru.bsuedu.cad.lab.intfs.Reader;
import ru.bsuedu.cad.lab.DataProcessingException;


public class ResourceFileReader implements Reader {

    private static final Logger logger = LoggerFactory.getLogger(CategoryRequest.class);
    
    private String inputFileName;

    
    public ResourceFileReader(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    @PostConstruct
    public void init() {
        logger.info("\nResourceFileReader полностью инициализирован: " + "\n");
    }

    @Override
    public String read() {

        try (InputStream is = App.class.getResourceAsStream(inputFileName)) {
            if (is == null) {
                throw new DataProcessingException("Resource not found: " + inputFileName);
            }
            
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);            
        } catch (IOException e) {
            throw new DataProcessingException("Failed to read resource: " + inputFileName, e);
        }
    }
}
```

Класс `AppConfig` теперь содержит методы с анотацией @Bean, которые создают объекты типов JdbcTemplate и NamedParameterJdbcTemplate, необходимые для работы с БД, и являющиеся удобной оболочкой над "ручным" взаимодействием с базой. Также были добавлены методы по созданию конкретных экземпляров `Reader` для каждого типа сущностей. Внедрение параметра с именем входного файла было перенесено прямо в инициализацию объекта. Код класса представлен ниже:

```java
// AppConfig.java
package ru.bsuedu.cad.lab;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.bsuedu.cad.lab.impls.ResourceFileReader;
import ru.bsuedu.cad.lab.intfs.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@Configuration
@ComponentScan({ "ru.bsuedu.cad.lab.impls", "ru.bsuedu.cad.lab.aspects" })
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        var dbBuilder = new EmbeddedDatabaseBuilder();
        return dbBuilder.setType(EmbeddedDatabaseType.H2)
                .setName("market.db")
                .addScript("classpath:db/schema.sql")
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean("productReader")
    public Reader productReader(@Value("#{property.productInputFileName}") String fileName) {
        return new ResourceFileReader(fileName);
    }

    @Bean("categoryReader")
    public Reader categoryReader(@Value("#{property.categoryInputFileName}") String fileName) {
        return new ResourceFileReader(fileName);
    }
}
```

Класс `ConcreteCategoryProvider` аналогичен раннему классу `ConcreteProductProvider`, за исключением добавленной типизации к поля `Parser`, аннотаций @Qualifier к параметрам конструктора и дженерик метода `getCategories()`, который теперь возвращает список категорий. Код класса представлен ниже:

```java
// ConcreteCategoryProvider.java
package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.intfs.*;;


@Component
public class ConcreteCategoryProvider implements CategoryProvider {
    private final Reader reader;
    private final Parser<Category> parser;
    
    
    @Autowired
    public ConcreteCategoryProvider(
            @Qualifier("categoryReader")Reader reader, 
            @Qualifier("categoryParser")Parser<Category> parser) {
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public ArrayList<Category> getCategories() {
        String resourceContent = reader.read();
        ArrayList<Category> categories = parser.parse(resourceContent); 

        return categories;
    }
}

```

---

**Добавил еще одну имплементацию интерфейса Renderer - DataBaseRenderer которая сохраняет данные считанные из CSV-файлов в таблицы базы данных. Реализация DataBaseRenderer используется по умолчанию благодаря строке в App `Renderer renderer = context.getBean("dataBaseRenderer", Renderer.class);`, где теперь указывается другое имя компонента.**

Класс `DataBaseRenderer` содержит четыре константных поля - два провайдера и два DAO для для каждой сущности. В конструкторе инициализируются все поля с помощью автосвязывания. А метод `render()` просто получает списки категорий и продуктов через провайдеров, а затем с помощью DAO-объектов эти списки сохраняются в БД. Код класса представлен ниже:

```java
// DataBaseRenderer.java
package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.CategoryDao;
import ru.bsuedu.cad.lab.intfs.CategoryProvider;
import ru.bsuedu.cad.lab.intfs.ProductDao;
import ru.bsuedu.cad.lab.intfs.ProductProvider;
import ru.bsuedu.cad.lab.intfs.Renderer;


@Component("dataBaseRenderer")
public class DataBaseRenderer implements Renderer{
    
    private final CategoryProvider categoryProvider;
    private final ProductProvider productProvider;
    private final CategoryDao categoryDAO;
    private final ProductDao productDAO;


    @Autowired
    public DataBaseRenderer(ProductProvider productProvider, CategoryProvider categoryProvider, CategoryDao categoryDAO, ProductDao productDAO) {
        this.productProvider = productProvider;
        this.categoryProvider = categoryProvider;
        this.categoryDAO = categoryDAO;
        this.productDAO = productDAO;
    }

    @Override
    public void render() {
        ArrayList<Product> products = productProvider.getProducts();
        ArrayList<Category> categories = categoryProvider.getCategories();

        categoryDAO.saveAll(categories);
        productDAO.saveAll(products);
    }
}
```

Интерфейс `ProductDao` предоставляет метод `saveAll()` с параметром в виде списка продуктов для сохранения.

```java
// ProductDao.java
package ru.bsuedu.cad.lab.intfs;

import java.util.ArrayList;

import ru.bsuedu.cad.lab.Product;


public interface ProductDao {
    public void saveAll(ArrayList<Product> products);
}
```

Интерфейс `CategoryDao` предоставляет метод `saveAll()` с параметром в виде списка категорий для сохранения.

```java
// CategoryDao.java
package ru.bsuedu.cad.lab.intfs;

import java.util.ArrayList;

import ru.bsuedu.cad.lab.Category;


public interface CategoryDao {
    public void saveAll(ArrayList<Category> categories);
}
```

Класс `AbstractBatchDao` предоставляет базовую логику для DAO классов, содержит константное поле типа *NamedParameterJdbcTemplate* и конструктор для его инициализации, а также метод `batchInsert()`, который принимает sql-запрос и список продуктов для вставки в БД. В методе с помощью *SqlParameterSourceUtils* создаётся батч из списка объектов, и вставляется в БД с помощью метода `batchUpdate()`. Код класса представлен ниже:

```java
// AbstractBatchDao.java
package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;


public abstract class AbstractBatchDao<T> {
    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    protected AbstractBatchDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    protected void batchInsert(String sql, ArrayList<T> items) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(items.toArray());
        namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }
}
```

Класс `ProductDaoJdbcTemplate` расширяет класс `AbstractBatchDao` с конкретным типом и реализует интерфейс `ProductDao`, помечен аннотацией `@Repository`, т.к. работатает с БД.
В конструкторе вызывается базовый метод, передавая ему параметр, который автоматически внедряется. Переопределенный метод `saveAll()`, содержит sql-скрипт для вставки значений в таблицу БД и вызывает, описанный ранее, метод `batchInsert()`. Код класса представлен ниже:

```java
// ProductDaoJdbcTemplate.java
package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.ProductDao;


@Repository
public class ProductDaoJdbcTemplate extends AbstractBatchDao<Product> implements ProductDao {

    @Autowired
    public ProductDaoJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    @Override
    public void saveAll(ArrayList<Product> products) {
        String sql = """
                INSERT INTO PRODUCTS
                (product_id, name, description, category_id, price, stock_quantity, image_url, created_at, updated_at)
                VALUES
                (:productId, :name, :description, :categoryId, :price, :stockQuantity, :imageUrl, :createdAt, :updatedAt)
                """;

        batchInsert(sql, products);
    }
}
```

Класс `CategoryDaoJdbcTemplate` аналогичен классу `ProductDaoJdbcTemplate`, за исключением sql-скрипта для вставки значений. Код класса представлен ниже:

```java
// CategoryDaoJdbcTemplate.java
package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.intfs.CategoryDao;


@Repository
public class CategoryDaoJdbcTemplate extends AbstractBatchDao<Category> implements CategoryDao{

    @Autowired
    public CategoryDaoJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    @Override
    public void saveAll(ArrayList<Category> categories) {
        String sql = """
                INSERT INTO CATEGORIES
                (category_id, name, description)
                VALUES
                (:categoryId, :name, :description)
                """;
        
        batchInsert(sql, categories);
    }
}
```

---

**Реализовал класс CategoryRequest, данный класс выполняет запрос к базе данных получающий следующую информацию - список категорий, количество товаров в которых больше единицы. Данная информация выводится в консоль с помощью библиотеки для логирования SLF4J, предоставляющей интерфейс , и библиотеки с реализацией интерфейса - logback, уровень лога INFO.**

Класс `CategoryRequest` также помечен аннотацией `@Repository`, содержит статическую константу типа `Logger`, которая инициализируется с помощью статического метода `getLogger()` класса `LoggerFactory`, и константу типа `JdbcTemplate`, а не `NamedParameterJdbcTemplate`, как было в классах для вставки значений, т.к. для оператора `SELECT` не нужны параметры. В конструкторе происходит инициализация jdbcTemplate через автоматическое связывание. Также, в классе определена "запись" (*Record*), которая дополняет класс Category ещё одним полем со значением количества товаров, и используется для простого хранения данных. Метод `categoryStatRowMapper()`, как и описанные выше мапперы, служит для преобразования результата запроса в java-объект. .В методе `execute()` содержится sql-скрипт для выборки необходимых значений из таблиц БД, затем выполняется запрос к БД с передачей маппера, из которого получается список категорий, который после проверяется на пустоту, если список не пустой, то его содержимое логируется в консоль. Код класса представлен ниже:  

```java
// CategoryRequest.java
package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Repository
public class CategoryRequest {
    private static final Logger logger = LoggerFactory.getLogger(CategoryRequest.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryRequest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void execute() {

        String sql = """
            SELECT c.category_id,
                   c.name,
                   c.description,
            COUNT(p.product_id) AS product_count
            FROM categories c
            JOIN products p ON p.category_id = c.category_id
            GROUP BY c.category_id, c.name, c.description
            HAVING COUNT(p.product_id) > 1
            ORDER BY c.category_id
                    """;
        
        ArrayList<CategoryStat> categoriesStat = new ArrayList<>(jdbcTemplate.query(sql, categoryStatRowMapper()));

        if (categoriesStat.isEmpty()) {
            logger.info("Категории, в которых больше одного товара - отсутствуют");
            return;
        }

        for (var category : categoriesStat) {
            logger.info("category_id = {}, name = {}, description = {}, product_count = {} \n\n", 
            category.categoryId(), category.name(), category.description(), category.productCount());
        }

    }

    private RowMapper<CategoryStat> categoryStatRowMapper() {
        return (rs, rowNum) -> new CategoryStat(
                rs.getLong("category_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("product_count"));
    }

    public record CategoryStat(long categoryId, String name, String description, int productCount) {}
}
```

---

**Приложение запускается с помощью команды gradle run, выводит необходимую информацию в консоль и успешно завершается:**

![alt text](images/image.png)

---

## Выводы
В данной работе приложение было научено сохранять данные в базе данных. А также научено выполнять SQL запросы и выводить их результаты в логи. В этом помог механизм JDBC (Java Database Connectivity), и такие инструменты Spring как DataSource, JDBCTemplate, RowMapper упрощающие работу с JDBC.

---

## Контрольные вопросы


### 1. Что такое Spring JDBC и какие преимущества оно предоставляет по сравнению с традиционным JDBC?

Spring JDBC — это поддержка JDBC, интегрированная в Spring Framework через несколько компонентов, что упрощает работу с базами данных, снижает уровень шума и повышает читаемость и надежность кода.

**Преимущества по сравнению с традиционным JDBC:**
- **Простота использования** – весь процесс работы с JDBC значительно упрощен, Spring избавляет от необходимости писать код для получения соединения, создания и выполнения SQL-запросов, обработки исключений и освобождения ресурсов.
- **Обработка исключений** – JdbcTemplate обрабатывает все исключения JDBC, преобразуя их в более удобные исключения Spring.
- **Управление ресурсами** – внутри JdbcTemplate используются механизмы управления ресурсами (соединения автоматически закрываются), избегая утечек памяти.

---

### 2. Какой основной класс в Spring используется для работы с базой данных через JDBC?

Основной класс – **`JdbcTemplate`**. Это основное средство для работы с JDBC в Spring. Он инкапсулирует работу с соединениями, управляет обработкой исключений и упрощает выполнение SQL-запросов и обновлений.

---

### 3. Какие шаги необходимо выполнить для настройки JDBC в Spring-приложении?

Для настройки JDBC в Spring-приложении необходимо:
- Настроить **`DataSource`** (источник данных). Spring поддерживает различные типы DataSource: Basic DataSource, Apache DBCP, HikariCP, C3P0.
- Создать бин `DataSource` (например, через `SimpleDriverDataSource` или `HikariConfig` + `HikariDataSource`).
- Затем использовать этот `DataSource` для создания `JdbcTemplate`.

Пример настройки `DataSource` с HikariCP:
```java
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        return new HikariDataSource(config);
    }
}
```

---

### 4. Что такое JdbcTemplate и какие основные методы он предоставляет?

**JdbcTemplate** – это один из ключевых компонентов Spring Framework, предназначенный для упрощения работы с базой данных. Он предоставляет удобный и мощный API для выполнения SQL-запросов, обработки результатов и управления транзакциями.

**Основные методы** (из примеров методички):
- `update()` – для выполнения операций вставки (INSERT), обновления (UPDATE), удаления (DELETE).
- `queryForObject()` – для выполнения запроса на выборку (SELECT) и получения результата в виде объекта (с использованием `RowMapper`).

---

### 5. Как в Spring JDBC выполнить запрос на выборку данных (SELECT) и получить результат в виде объекта?

Для выполнения SELECT-запроса и получения результата в виде объекта используется метод `queryForObject()` класса `JdbcTemplate` вместе с `RowMapper`.

Пример в классе `PersonaJdbcDaoSupport`:
```java
public Persona getPersonaById(Long id) {
    String sql = "SELECT * FROM personas WHERE id = ?";
    return getJdbcTemplate().queryForObject(sql, new Object[] { id }, personaRowMapper());
}
```

---

### 6. Как использовать RowMapper в JdbcTemplate?
**RowMapper** – это функциональный интерфейс, используемый для преобразования строк из результата SQL-запроса в объекты Java. Он имеет один метод `mapRow()`, который вызывается для каждой строки результата.

Пример реализации `RowMapper`:

```java
private RowMapper<Persona> personaRowMapper() {
    return (rs, rowNum) -> new Persona(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("arcana"),
        rs.getInt("level"),
        rs.getInt("strength"),
        rs.getInt("magic"),
        rs.getInt("endurance"),
        rs.getInt("agility"),
        rs.getInt("luck"),
        rs.getLong("character_id"));
}
```
Затем этот `RowMapper` передаётся в методы `queryForObject()` или `query() JdbcTemplate`.

---

### 7. Как выполнить вставку (INSERT) данных в базу с использованием JdbcTemplate?

Для вставки данных используется метод `update()` класса `JdbcTemplate` с SQL-строкой `INSERT` и параметрами.

Хотя в теоретической части прямой пример `INSERT` через `JdbcTemplate` не приведён, принцип тот же, что и для `UPDATE` (метод `update()`). Например, для `PreparedStatement` показан INSERT:
```java
try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (name, age) VALUES (?, ?)")) {
    pstmt.setString(1, "Alice");
    pstmt.setInt(2, 25);
    pstmt.executeUpdate();
}
```
Аналогично через `JdbcTemplate`: `jdbcTemplate.update("INSERT INTO users (name, age) VALUES (?, ?)", "Alice", 25);`

---

### 8. Как выполнить обновление (UPDATE) или удаление (DELETE) записей через JdbcTemplate?
Используется метод `update()` класса `JdbcTemplate`. 

Пример обновления (UPDATE):

```java
public void updatePersona(Persona persona) {
    String sql = "UPDATE personas SET name = ?, arcana = ?, level = ?, strength = ?, magic = ?, endurance = ?, " +
            "agility = ?, luck = ?, character_id = ? WHERE id = ?";
    jdbcTemplate.update(sql, persona.getName(), persona.getArcana(), persona.getLevel(), persona.getStrength(),
            persona.getMagic(), persona.getEndurance(), persona.getAgility(), persona.getLuck(),
            persona.getCharacterId(), persona.getId());
}
```
Для удаления (DELETE) используется аналогичный вызов `jdbcTemplate.update()` с SQL-строкой DELETE.

---

### 9. Как в Spring JDBC обрабатывать исключения, возникающие при работе с базой данных?
Spring JDBC обрабатывает исключения следующим образом:

- **JdbcTemplate** обрабатывает все исключения JDBC, преобразуя их в более удобные исключения Spring.

- В пакете `org.springframework.jdbc.support` находится `SQLExceptionTranslator` для преобразования исключений и `DataAccessException` для работы с ошибками.
Таким образом, разработчик работает с иерархией `DataAccessException` вместо низкоуровневых `SQLException`.

---

### 10. Какие альтернативные способы работы с базой данных есть в Spring кроме JdbcTemplate?

Кроме `JdbcTemplate`, в Spring существуют следующие альтернативные способы работы с базой данных:
- **NamedParameterJdbcTemplate** – расширение `JdbcTemplate`, поддерживающее именованные параметры в SQL-запросах.
- **JdbcDaoSupport** – абстрактный класс, упрощающий использование `JdbcTemplate` в DAO-слое.
- Классы из пакета **`org.springframework.jdbc.object`** – для объектно-ориентированного подхода к выполнению SQL-запросов, такие как `SqlQuery` и `SqlUpdate`.