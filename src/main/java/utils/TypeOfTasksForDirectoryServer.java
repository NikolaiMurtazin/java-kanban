package utils;

public enum TypeOfTasksForDirectoryServer {
    TASK ("task"),
    EPIC ("epic"),
    SUBTASK ("subtask");

    private String alias;

    TypeOfTasksForDirectoryServer(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
