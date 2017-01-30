
package ru.javafx.musicbook.client.repository.operators;

public enum StringOperator {
    
    CONTAINS("contains"),
    CONTAINS_IGNORE_CASE("containsIgnoreCase"),
    ENDS_WITH("endsWith"),
    ENDS_WITH_IGNORE_CASE("endsWithIgnoreCase"),
    EQ("eq"),
    EQUALS_IGNORE_CASE("equalsIgnoreCase"),   
    LIKE("like"),
    LIKE_IGNORE_CASE("likeIgnoreCase"),
    NOT_EQUALS_IGNORE_CASE("notEqualsIgnoreCase"),
    NOT_LIKE("notLike"),
    STARTS_WITH("startsWith"),
    STARTS_WITH_IGNORE_CASE("startsWithIgnoreCase");
    
    private final String operator;

    private StringOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator;
    }
    
}
