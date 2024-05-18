package app.main.database.base;

public class Column {
    private String name;
    private String attributes;

    public Column(String name, String... attributes) {
        this.name = name;
        this.attributes = Attribute.buildString(attributes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", attributes='" + attributes + '\'' +
                '}';
    }
}
