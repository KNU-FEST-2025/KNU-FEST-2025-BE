package knu.fest.knu.fest.domain.user.entity;

public enum UserRole {
    USER("USER"),
    BOOTH_MANAGER("BOOTH_MANAGER"),
    ADMIN("ADMIN")
    ;

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return "ROLE_" + authority;
    }
}
