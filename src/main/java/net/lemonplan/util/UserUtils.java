package net.lemonplan.util;

import net.lemonplan.pojo.User;

public class UserUtils {
    static public User toSafeUser(User user) {
        user.setCid(null);
        user.setPassword(null);
        return user;
    }
}
