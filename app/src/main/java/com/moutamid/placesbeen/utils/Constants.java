package com.moutamid.placesbeen.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Constants {
    public static final String IS_LOGGED_IN = "isloggedin";
    public static final String NULL = "null";

    public static final String FOR_CHARTS = "for_charts";
    public static final String AIRPORTS_JSON = "airports.json";
    public static final String CULTURAL_SITES_JSON = "cultural_sites.json";
    public static final String WORLD_CITIES_JSON = "worldcities.json";
    public static final String PARAMS_Continent = "params_Continent";
    public static final String PARAMS_Country = "params_Country";
    public static final String PARAMS_States = "params_States";
    public static final String PARAMS_City = "params_City";
    public static final String PARAMS_CulturalSites = "params_CulturalSites";
    public static final String PARAMS_NationalParks = "params_NationalParks";
    public static final String PARAMS_Airports = "params_Airports";
    public static final String IS_FIRST_TIME = "is_first_time";
    public static final String SAVED_ITEMS_PATH = "saved_items_path";
    public static final String BEEN_ITEMS_PATH = "been_items_path";
    public static final String WANT_TO_ITEMS_PATH = "want_to_items_path";
    public static final String ITEM_CHANGED = "item_changed";
    public static final String CURRENT_MODEL_CLASS = "current_model_class";
    public static final String PROFILE_URL = "profileUrl";
    public static final String SAVED_LIST = "savedlist";
    public static final String POLYGON = "polygon";
    public static final String CURRENT_QUERY_LAT = "current_query_lat";
    public static final String CURRENT_QUERY_LONG = "current_query_long";
    public static final String PARAMS = "params";
    public static final String PARAMS_WORLD_MAP = "map";
    public static final String PARAMS_CHARTS = "params_charts";
    public static final String PARAMS_PROFILE = "params_profile";
    public static final String USER_NAME = "username";

//    public static final String COUNTRIES_OPTION = "countries";
//    public static final String CONTINENTS_OPTION = "countries";
//    public static final String STATES_OPTION = "countries";
//    public static final String CITIES_OPTION = "countries";
//    public static final String CULTURAL_SITES_OPTION = "countries";
//    public static final String NATIONAL_PARKS_OPTION = "countries";

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("PlacesBeenApp");
        db.keepSynced(true);
        return db;
    }

    public static String GET_POSITION_URL(String query) {
        return "http://api.positionstack.com/v1/forward?access_key=ecd00653e072c8636fd32b0c7dcd6a67&limit=1&query="
                + query;
    }

    public static String GET_PIXABAY_URL(String query) {
        return "https://pixabay.com/api/?key=25707240-9c632bb3ee0bdb5ae65d78d7d&q=" +
                query +
                "&image_type=photo&pretty=true";
    }

    public static String GET_BOUNDARY_URL(String q) {
        String query = null;
        try {
            query = URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e) {
            query = "china";
            e.printStackTrace();
        }
        return "https://nominatim.openstreetmap.org/search?country=" +
                query +
                "&polygon_geojson=1&format=json";
    }

    public static String GET_COUNTRY_FLAG(String q) {
        String query = null;
        try {
            query = URLEncoder.encode(q, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            query = "china";
            e.printStackTrace();
        }
        Log.d("FISH", "GET_COUNTRY_FLAG: QUERY: " + query);
        return "https://countryflagsapi.com/png/" +
                query;
    }

    public static String GET_BOUNDARY_URL_FOR_CITY(String q) {
        String query = null;
        try {
            query = URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e) {
            query = "china";
            e.printStackTrace();
        }
        return "https://nominatim.openstreetmap.org/search?city=" +
                query +
                "&polygon_geojson=1&format=json";
    }


    public static final ArrayList<String> COUNTRIES_LIST = new ArrayList<>(Arrays.asList("Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe", "Palestine"));

    public static final ArrayList<String> CONTINENTS_LIST = new ArrayList<>(Arrays.asList("Asia", "Africa", "North America", "South America", "Antarctica", "Europe", "Australia"));

    public static ArrayList<String> STATES_LIST() {
        ArrayList<String> LIST = new ArrayList<>(Arrays.asList("Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe", "Palestine"));
        Collections.reverse(LIST);
        return LIST;
    }


}
