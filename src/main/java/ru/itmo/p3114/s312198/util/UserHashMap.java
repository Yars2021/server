package ru.itmo.p3114.s312198.util;

import ru.itmo.p3114.s312198.exception.NoSuchUserException;
import ru.itmo.p3114.s312198.transmission.structures.user.User;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserHashMap {
    private static final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public static void add(User user) {
        users.put(user.getUsername(), user);
    }

    public static boolean containsUsername(String username) {
        return users.containsKey(username);
    }

    public static User get(String username) throws NoSuchUserException {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new NoSuchUserException(username);
        }
    }

    public static void remove(String username) throws NoSuchUserException {
        if (users.containsKey(username)) {
            users.remove(username);
        } else {
            throw new NoSuchUserException(username);
        }
    }

    public static int size() {
        return users.size();
    }

    public static Set<String> getKeys() {
        return users.keySet();
    }

    public static ArrayList<String> getKeyList() {
        return new ArrayList<>(getKeys());
    }

    public static void clear() {
        users.clear();
    }
}
