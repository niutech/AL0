package computer.fuji.al0.models;

public class Media {
    public enum Type { Image, Video }

    private String id;
    private String name;
    private String path;
    private Type type;

    public Media (String id, String name, String path, Type type) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
    }

    // getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    // setters
    public void setName (String name) {
        this.name = name;
    }

    public void setPath (String path) {
        this.path = path;
    }
}