package com.ash.bookworm.helpers.utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Util {
    public static boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static String getExtension(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            if (string.charAt(i) == '.') {
                return string.substring(i);
            }
        }
        return "";
    }

    public static String getLocationName(android.content.Context context, Double latitude, Double longitude) {
        try {
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;

            addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                return "Waiting for Location";
            } else {
                String featureName = addresses.get(0).getFeatureName();
                String localityName = addresses.get(0).getLocality();
                String adminAreaName = addresses.get(0).getAdminArea();
                String countryName = addresses.get(0).getCountryName();
                String locationName = "";

                if (featureName != null) {
                    locationName += featureName;
                }
                if (localityName != null) {
                    locationName += ", " + localityName;
                }
                if (adminAreaName != null) {
                    locationName += ", " + adminAreaName;
                }
                if (localityName != null) {
                    locationName += ", " + countryName;
                }

                return locationName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Waiting for Location";
    }

    public static float dpToPixels(Context context, float dpValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
    }
}
