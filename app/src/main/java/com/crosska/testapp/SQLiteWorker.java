package com.crosska.testapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SQLiteWorker extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "excel_data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "SQLiteWorker";
    private final Context context;
    private final List<Integer> warlordsId = Arrays.asList(1, 7001, 7009, 7014, 7005, 3000, 7002, 7006, 7013, 7010, 3001, 7007, 7003, 7015, 7011, 3002, 7008, 7004, 7012, 7016, 7017);


    public SQLiteWorker(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Map<String, String> getAllRows(String tableName) {
        Map<String, String> rowData = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Формируем запрос: выбрать одну строку, пропустив первые rowNumber строк
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);

        cursor.close();
        return rowData;
    }

    public Map<String, String> getRowByOrdinal(String tableName, int rowNumber) {
        Map<String, String> rowData = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Формируем запрос: выбрать одну строку, пропустив первые rowNumber строк
        String query = "SELECT * FROM " + tableName + " LIMIT 1 OFFSET " + rowNumber;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                rowData.put(columnName, value);
            }
        } else {
            Log.d("DB_QUERY", "Строка с порядковым номером " + rowNumber + " не найдена в таблице " + tableName);
        }

        cursor.close();
        return rowData;
    }

    public Map<String, String> getFirstRow(String tableName) {
        Map<String, String> rowData = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Выполняем запрос, который возвращает первую строку
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);

        if (cursor.moveToFirst()) {
            // Проходим по всем столбцам и сохраняем данные в map
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                rowData.put(columnName, value);
            }
        } else {
            Log.d("DB_QUERY", "Таблица " + tableName + " пуста или не существует");
        }

        cursor.close();
        return rowData;
    }

    public String getCardData(String cardParam) {

        String cardInfo;
        String[] cardParamParsed = cardParam.split(":");
        Log.d("DECK_QUERY_PARAMETERS", "Параметры колоды: \n" + Arrays.toString(cardParamParsed));

        if (cardParamParsed.length == 1) {

        } else if (cardParamParsed.length == 2) {

        } else {

        }

        switch (cardParamParsed.length) {
            case 1:
                Map<String, String> cardInfoMap = getCardInfo(Integer.parseInt(cardParamParsed[0]), 1, 0);
                StringBuilder stringBuilder = new StringBuilder();
                if (!cardInfoMap.values().isEmpty()) {
                    for (String s : cardInfoMap.values()) {
                        stringBuilder.append(s);
                        stringBuilder.append(" .");
                    }
                    cardInfo = stringBuilder.toString();
                } else {
                    cardInfo = "Not found";
                }
                break;
            case 2:
                if (Integer.parseInt(cardParamParsed[1]) == 0) {
                    cardInfoMap = getCardInfo(Integer.parseInt(cardParamParsed[0]), 1, 0);
                    stringBuilder = new StringBuilder();
                    for (String s : cardInfoMap.values()) {
                        stringBuilder.append(s);
                        stringBuilder.append(" .");
                    }
                    cardInfo = stringBuilder.toString();
                } else {
                    cardInfoMap = getCardInfo(Integer.parseInt(cardParamParsed[0]), Integer.parseInt(cardParamParsed[1]) + 1, 0);
                    stringBuilder = new StringBuilder();
                    for (String s : cardInfoMap.values()) {
                        stringBuilder.append(s);
                        stringBuilder.append(" .");
                    }
                    cardInfo = stringBuilder.toString();
                }
                break;
            case 3:
                if (Integer.parseInt(cardParamParsed[1]) == 0) {
                    cardInfoMap = getCardInfo(Integer.parseInt(cardParamParsed[0]), 1, Integer.parseInt(cardParamParsed[2]));
                    stringBuilder = new StringBuilder();
                    for (String s : cardInfoMap.values()) {
                        stringBuilder.append(s);
                        stringBuilder.append(" .");
                    }
                    cardInfo = stringBuilder.toString();
                } else {
                    cardInfoMap = getCardInfo(Integer.parseInt(cardParamParsed[0]), Integer.parseInt(cardParamParsed[1]) + 1, Integer.parseInt(cardParamParsed[2]));
                    stringBuilder = new StringBuilder();
                    for (String s : cardInfoMap.values()) {
                        stringBuilder.append(s);
                        stringBuilder.append(" .");
                    }
                    cardInfo = stringBuilder.toString();
                }
                break;
            default:
                cardInfo = "Error";
                break;
        }
        return cardInfo;
    }

    public Map<String, String> getCardInfo(int id, int promote, int reborn) {
        Map<String, String> rowData = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT rarity, race, class, gender, конфигурация_персонажей.power, image, is_event, id " +
                        "FROM конфигурация_персонажей " +
                        "JOIN конфигурация_реборнов ON " + reborn + " = конфигурация_реборнов.monster_id " +
                        "WHERE " +
                        "конфигурация_персонажей.id = " + id + " AND " +
                        "конфигурация_персонажей.inner_id = " + promote +
                        " LIMIT 1";

        Log.d("DB_QUERY", "Запрос " + query);

        Cursor cursor = db.rawQuery(query, null);

        /*
    "SELECT users.id, users.name, users.email, cities.name AS city_name " +
    "FROM users " +
    "JOIN cities ON users.city_id = cities.id " +
    "WHERE users.age > ? AND cities.name = ?",
    new String[]{"25", "Moscow"}
);

        */

        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                rowData.put(columnName, value);
            }
        } else {
            Log.d("DB_QUERY", "Строка с такими параметрами не найдена\n" + query);
        }

        cursor.close();
        db.close();
        return rowData;
    }

    public void createTempTable(List<String> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS tempTable");
        db.execSQL("CREATE TABLE tempTable(id INTEGER, promotes_count INTEGER, reborn_count INTEGER)");

        for (String s : dataList) {
            String[] parsedData = s.split(":");
            if (parsedData.length == 1) {
                db.execSQL("INSERT INTO tempTable(id, promotes_count, reborn_count) " +
                        "VALUES(" + Integer.parseInt(parsedData[0]) + "," + 1 + "," + 0 + ")");
                Log.d("DB_QUERY", "INSERT INTO tempTable(id, promotes_count, reborn_count) " +
                        "VALUES(" + Integer.parseInt(parsedData[0]) + "," + 1 + "," + 0 + ")");
            } else if (parsedData.length == 2) {
                if (parsedData[1].equals("0")) {
                    db.execSQL("INSERT INTO tempTable(id, promotes_count, reborn_count) " +
                            "VALUES(" + Integer.parseInt(parsedData[0]) + "," + 1 + "," + 0 + ")");
                    Log.d("DB_QUERY", "INSERT INTO tempTable(id, promotes_count, reborn_count) " +
                            "VALUES(" + Integer.parseInt(parsedData[0]) + "," + 1 + "," + 0 + ")");
                } else {
                    db.execSQL("INSERT INTO tempTable(id, promotes_count, reborn_count) " +
                            "VALUES(" + Integer.parseInt(parsedData[0]) + "," + (Integer.parseInt(parsedData[1]) + 1) + "," + 0 + ")");
                    Log.d("DB_QUERY", "INSERT INTO tempTable(id, promotes_count, reborn_count) " +
                            "VALUES(" + Integer.parseInt(parsedData[0]) + "," + (Integer.parseInt(parsedData[1]) + 1) + "," + 0 + ")");
                }
            } else {
                db.execSQL("INSERT INTO tempTable(id, promotes_count, reborn_count) " +
                        "VALUES(" + Integer.parseInt(parsedData[0]) + "," + (Integer.parseInt(parsedData[1]) + 1) + "," + Integer.parseInt(parsedData[2]) + ")");
                Log.d("DB_QUERY", "INSERT INTO tempTable(id, promotes_count, reborn_count) " +
                        "VALUES(" + Integer.parseInt(parsedData[0]) + "," + (Integer.parseInt(parsedData[1]) + 1) + "," + Integer.parseInt(parsedData[2]) + ")");
            }
        }
        db.close();
    }

    public List<String> getCardsInfo() {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT конфигурация_персонажей.rarity, " +
                "конфигурация_персонажей.race, " +
                "конфигурация_персонажей.class, " +
                "конфигурация_персонажей.gender, " +
                "конфигурация_персонажей.power, " +
                "конфигурация_персонажей.image, " + // 5
                "конфигурация_персонажей.is_event,  " +
                "конфигурация_персонажей.hp, " +
                "конфигурация_персонажей.attack, " +
                "конфигурация_персонажей.skills_id, " +
                "конфигурация_персонажей.skill_value, " + // 10
                "конфигурация_персонажей.promote_type, " +
                "конфигурация_персонажей.promote_value, " +
                "tempTable.promotes_count, " +
                "tempTable.id, " +
                "tempTable.reborn_count, " +
                "конфигурация_персонажей.inner_id " +
                "FROM tempTable " +
                "JOIN конфигурация_персонажей ON tempTable.id = конфигурация_персонажей.id ";
        //                "ORDER BY конфигурация_персонажей.id, inner_id";

        Log.d("DB_QUERY", "Запрос " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        int currRow = 0;
        int skips = 0;
        List<String> resultList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(14);
            int promotes = cursor.getInt(13);

            do {
                if (id != cursor.getInt(14)) {
                    promotes = cursor.getInt(13);
                    id = cursor.getInt(14);
                    skips = 0;
                }
                boolean promoteFail = false;

                Card card = new Card();
                card.setRarity(cursor.getString(0));
                card.setRace(cursor.getString(1));
                card.setClass_(cursor.getString(2));
                card.setGender(cursor.getString(3));
                card.setPower(cursor.getInt(4));
                card.setImage(cursor.getString(5));
                card.setEvent(cursor.getInt(6) == 1);
                card.setHp(cursor.getInt(7));
                card.setAttack(cursor.getInt(8));

                try {
                    card.setSkillsNames(Arrays.asList(cursor.getString(9).split(",")));
                } catch (NullPointerException ex) {
                    card.setSkillsNames(new ArrayList<>());
                }

                try {
                    card.setSkillsValues(Arrays.asList(cursor.getString(10).split(",")));
                } catch (NullPointerException ex) {
                    card.setSkillsValues(new ArrayList<>());
                }

                try {
                    card.setPromoteTypes(Arrays.asList(cursor.getString(11).split(",")));
                } catch (NullPointerException ex) {
                    card.setPromoteTypes(new ArrayList<>());
                    promoteFail = true;
                }

                try {
                    card.setPromoteValues(Arrays.asList(cursor.getString(12).split(",")));
                } catch (NullPointerException ex) {
                    card.setPromoteTypes(new ArrayList<>());
                    promoteFail = true;
                }

                if (!promoteFail) {
                    Log.d("CARD_CHECK", "Card has promote values in column: " + card.getPromoteValues().size() + " promotes need: " + (promotes - 1));
                    if (promotes > 1 && promotes <= card.getPromoteValues().size()) {
                        for (int i = 0; i < promotes - 1; i++) { // Цикл по промоутам
                            switch (card.getPromoteTypes().get(i)) {
                                case "hp":
                                    Log.d("HP_INCREASE", "HP has been increased " + card.getHp() + " + " + Integer.parseInt(card.getPromoteValues().get(i)));
                                    card.setHp(card.getHp() + Integer.parseInt(card.getPromoteValues().get(i)));
                                    break;
                                case "attack":
                                    Log.d("ATTACK_INCREASE", "Attack has been increased " + card.getAttack() + " + " + Integer.parseInt(card.getPromoteValues().get(i)));
                                    card.setAttack(card.getAttack() + Integer.parseInt(card.getPromoteValues().get(i)));
                                    break;
                                default:
                                    if (card.getSkillsNames().contains(card.getPromoteTypes().get(i))) { // В существующих скиллах уже есть этот
                                        Log.d("SKILL_ADDED", "Skill has been increased ");
                                        int skillIndex = card.getPromoteTypes().indexOf(card.getPromoteTypes().get(i)); //
                                        try {
                                            int skillVal = Integer.parseInt(card.getSkillsValues().get(skillIndex));
                                            card.setSkillsValue(i, String.valueOf(skillVal + Integer.parseInt(card.getPromoteValues().get(i))));
                                        } catch (NumberFormatException ex) {
                                            card.setSkillsValue(i, card.getSkillsValues().get(skillIndex) + "+" + card.getPromoteValues().get(i));
                                        }
                                    } else { // Это новый скилл
                                        Log.d("SKILL_ADDED", "Skill has been added " + card.getPromoteTypes().get(i));
                                        List<String> list = card.getSkillsNames();
                                        list.add(card.getPromoteTypes().get(i));
                                        card.setSkillsValues(list);
                                        list = card.getSkillsValues();
                                        list.add(card.getPromoteValues().get(i));
                                        card.setSkillsValues(list);
                                    }
                                    break;
                            }

                            if (currRow < cursor.getCount()) { // Проверка на конец таблицы
                                cursor.moveToNext();
                                if (id == cursor.getInt(14)) { // Проверка на тот же ID что и был
                                    int power_NEXT = cursor.getInt(4); // Power next
                                    Log.d("POWER_NEXT", "POWER NEXT = " + power_NEXT);
                                    cursor.moveToPrevious();
                                    int power_part = (power_NEXT - cursor.getInt(4)) / cursor.getString(11).split(",").length; // (500 - 300) / 10 = 20
                                    int power_interpolated = (power_part * (cursor.getInt(13) - 1) + cursor.getInt(4));
                                    Log.d("POWER_INTERPOLATED", "New power with interpolation = " + power_interpolated);
                                    card.setPower(power_interpolated);
                                } else {
                                    cursor.moveToPrevious();
                                }
                            }

                        }
                        skips++;
                        while (currRow < cursor.getCount() && cursor.getInt(14) == id) {
                            Log.d("MOVE_TO_NEXT_ROW", "Move to next row to get to the next id");
                            cursor.moveToNext();
                            currRow++;
                        }
                    } else if (promotes > card.getPromoteValues().size()) {
                        promotes = promotes - card.getPromoteValues().size();
                        skips++;
                    }
                }
                if (promotes == 1) {
                    while (currRow < cursor.getCount() && cursor.getInt(14) == id) {
                        Log.d("MOVE_TO_NEXT_ROW", "Move to next row after skip because promote = 0");
                        cursor.moveToNext();
                        currRow++;
                    }
                }
                StringBuilder s = new StringBuilder();
                for (int j = 0; j < card.getSkillsNames().size(); j++) {
                    s.append(card.getSkillsNames().get(j)).append(" - ").append(card.getSkillsValues().get(j)).append("\n");
                }
                resultList.add("--- CARD ---\nRarity: " + card.getRarity() + "\nRace: " + card.getRace() + "\nClass: " + card.getClass_() + "\nGender: " + card.getGender() + "\nPower: " + card.getPower() + "\nImage: " + card.getImage() + "\nEvent: " + card.isEvent() + "\nType: " + (warlordsId.contains(card.getId()) ? "Warlord" : "Handed card") + "\n\nHP: " + card.getHp() + "\nAttack: " + card.getAttack() + "\nSkills:\n" + s.toString());
            } while (currRow < cursor.getCount());
        }

        cursor.close();
        db.close();
        return resultList;
    }

}
