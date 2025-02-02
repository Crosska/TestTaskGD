package com.crosska.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelToSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "excel_data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "ExcelToSQLite";
    private final Context context;
    private TextView processShower;

    public ExcelToSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public void setProcessShower(TextView processShower) {
        this.processShower = processShower;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS temp_table");
        onCreate(db);
    }

    public void importExcelToDatabase(String excelFilePath) {
        try (FileInputStream fis = new FileInputStream(new File(excelFilePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            SQLiteDatabase db = getWritableDatabase();

            int part = 100 / workbook.getNumberOfSheets();
            int progress = 0;
            processShower.setText(progress + "%");
            for (Sheet sheet : workbook) {
                progress += part;
                processShower.setText(progress + "%");
                String tableName = sheet.getSheetName().toLowerCase();
                Log.d(TAG, "Создание таблицы: " + tableName);

                if (tableName.contains(" ")) {
                    tableName = tableName.replace(" ", "_");
                }
                db.execSQL("DROP TABLE IF EXISTS " + tableName);

                Iterator<Row> rowIterator = sheet.iterator();
                if (!rowIterator.hasNext()) continue;


                StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + tableName);
                switch (tableName) {
                    case "колоды":
                        createTableQuery.append("(deck_id INTEGER, ")
                                .append("deck TEXT");
                        break;
                    case "локализация":
                        createTableQuery.append("(key TEXT, ")
                                .append("en TEXT");
                        break;
                    case "конфигурация_персонажей":
                        createTableQuery.append("(id INTEGER, ")
                                .append("inner_id INTEGER, ")
                                .append("attack INTEGER, ")
                                .append("hp INTEGER, ")
                                .append("skills_id TEXT, ")
                                .append("skill_value TEXT, ")
                                .append("image TEXT, ")
                                .append("rarity TEXT, ")
                                .append("gender TEXT, ")
                                .append("race TEXT, ")
                                .append("class TEXT, ")
                                .append("promote_type TEXT, ")
                                .append("promote_value TEXT, ")
                                .append("is_event INTEGER, ")
                                .append("power INTEGER");
                        break;
                    case "конфигурация_реборнов":
                        createTableQuery.append("(monster_id INTEGER, ")
                                .append("reset_num INTEGER, ")
                                .append("unlock_type TEXT, ")
                                .append("unlock_value INTEGER, ")
                                .append("bonus_type TEXT, ")
                                .append("bonus_value TEXT, ")
                                .append("power INTEGER");
                        break;
                }
                createTableQuery.append(");");
                Log.d("Создание БД ", createTableQuery.toString());
                db.execSQL(createTableQuery.toString());
                Row headerRow = null;
                if (!tableName.equals("колоды")) {
                    headerRow = rowIterator.next();
                }

                // Вставка данных
                while (rowIterator.hasNext()) { // Цикл по строкам
                    Row row = rowIterator.next();

                    // Проверка: если строка пустая, пропускаем её
                    boolean isRowEmpty = true;
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                            isRowEmpty = false;
                            break;
                        }
                    }
                    if (isRowEmpty) {
                        Log.d(TAG, "Пропускаем пустую строку");
                        continue;
                    }

                    ContentValues values = new ContentValues();
                    for (int i = 0; i < row.getLastCellNum(); i++) { // Цикл по столбцам
                        String columnName;
                        if (tableName.equals("колоды")) {

                            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                            if (i == 0) {
                                columnName = "deck_id";
                                Log.d(TAG, "Импорт " + columnName + " со знач. " + cell.getNumericCellValue());
                                values.put(columnName, cell.getNumericCellValue());
                            } else {
                                columnName = "deck";
                                Log.d(TAG, "Импорт " + columnName + " со знач. " + cell.getStringCellValue());
                                values.put(columnName, cell.toString());
                            }

                        } else {

                            Cell headerCell = headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                            if (headerCell.getCellType() == CellType.STRING) {
                                columnName = headerCell.getStringCellValue();
                            } else {
                                columnName = String.valueOf((int) headerCell.getNumericCellValue()); // Конвертируем в строку (без .0)
                            }

                            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            try {
                                Log.d(TAG, "Импорт " + columnName + " со знач. " + cell.toString());
                            } catch (FormulaParseException ex) { // Ошибка, в ячейке формула
                                Log.e(TAG, "Импорт с ошибкой " + columnName + " в строке " + cell.getRow());
                            }

                            try {
                                switch (cell.getCellType()) {
                                    case STRING:
                                        values.put(columnName, cell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        values.put(columnName, cell.getNumericCellValue());
                                        break;
                                }
                            } catch (FormulaParseException ex) { // Ошибка, в ячейке формула
                                try {
                                    values.put(columnName, cell.getCellFormula());
                                } catch (FormulaParseException exs) {
                                    Log.e(TAG, "Импорт формулы с ошибкой " + columnName + " в строке" + cell.getRow());
                                }
                                Log.e(TAG, "Импорт с ошибкой " + columnName + " в строке" + cell.getRow());
                                values.put(columnName, "");
                            }

                        }

                    }
                    db.insert(tableName, null, values);
                }
            }
            db.close();
            processShower.setText("100%");
            Log.d(TAG, "Импорт завершен!");

        } catch (IOException e) {
            Log.e(TAG, "Ошибка при чтении Excel: " + e.getMessage());
        }
    }

}
