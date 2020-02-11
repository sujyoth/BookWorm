package com.ash.bookworm.Utilities;

import android.widget.EditText;

import java.util.regex.Pattern;

public final class Util {
    private Util() {}

    public static boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
