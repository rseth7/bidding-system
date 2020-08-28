package com.cars24.biddingsystem.service;

public interface UserService {
    void loadAllLoggedInUsers();
    boolean addUser(final String userToken);
    boolean removeUser(final String userToken);
    boolean isUserLoggedIn(final String userToken);
}
