package JVM;

class User {
    private String userName;
    private String password;

    User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    String getUserName() {
        return userName;
    }
}
