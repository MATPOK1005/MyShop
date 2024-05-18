package app.main.controller;

import app.main.database.base.Column;
import app.main.database.base.Table;

public class QueryBuilder {
    public static String buildCreateTableQuery(Table table) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        query.append(table.getName());
        query.append(" (");
        for (Column column : table.getColumns()) {
            query.append(column.getName()).append(" ").append(column.getAttributes()).append(", ");
        }
        query = new StringBuilder(query.substring(0, query.length() - 2));
        query.append(");");
        return query.toString();
    }

    public static String buildDropTableQuery(Table table) {
        StringBuilder query = new StringBuilder("DROP TABLE IF EXISTS ");
        query.append(table.getName()).append(";");
        return query.toString();
    }

}
