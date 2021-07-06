package io.github.mymatsubara.survivaltournament.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class HashUtils {
    private HashUtils() {}

    public static String hash(String input) {
        return BCrypt.withDefaults().hashToString(6, input.toCharArray());
    }

    public static Boolean check(String input, String hash) {
        return BCrypt.verifyer().verify(input.toCharArray(), hash).verified;
    }
}
