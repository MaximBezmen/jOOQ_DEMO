package com.jooq.demo;


public record Account(
    Long id, String lastName, String firstName, String userName,  String email, String password,
    String role
) {

}
