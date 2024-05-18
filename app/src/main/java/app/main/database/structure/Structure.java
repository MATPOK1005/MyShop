package app.main.database.structure;

import java.util.ArrayList;
import java.util.Arrays;

import app.main.database.base.Attribute;
import app.main.database.base.Column;
import app.main.database.base.Table;

public class Structure {
    public static final Table USERS = new Table("users",
                new ArrayList<>(Arrays.asList(
                        new Column("id", Attribute.INTEGER, Attribute.PRIMARY_KEY),
                        new Column("username", Attribute.TEXT, Attribute.UNIQUE),
                        new Column("password", Attribute.TEXT),
                        new Column("cart", Attribute.TEXT) // JSON
                ))
            );

    public static final Table ITEMS = new Table("items",
                new ArrayList<>(Arrays.asList(
                        new Column("id", Attribute.INTEGER, Attribute.PRIMARY_KEY),
                        new Column("name", Attribute.TEXT),
                        new Column("description", Attribute.TEXT),
                        new Column("price", Attribute.REAL),
                        new Column("amount", Attribute.INTEGER),
                        new Column("picture", Attribute.TEXT), // Base64
                        new Column("seller", Attribute.TEXT)
                ))
            );

    public static final Table ORDERS = new Table("orders",
                new ArrayList<>(Arrays.asList(
                        new Column("id", Attribute.INTEGER, Attribute.PRIMARY_KEY),
                        new Column("buyer_id", Attribute.INTEGER),
                        new Column("date", Attribute.TEXT),
                        new Column("items", Attribute.TEXT), // JSON
                        new Column("delivery_address", Attribute.TEXT),
                        new Column("delivery_name", Attribute.TEXT),
                        new Column("price", Attribute.REAL)

                ))
            );
}
