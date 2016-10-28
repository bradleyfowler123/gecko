package com.auton.bradley.myfe;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Bradley on 27/10/2016.
 * class used to send login request data nicely
 */

class LoginRequest extends StringRequest {
    private  static final String LOGIN_REQUEST_URL = "https://myfe.000webhostapp.com/Login_User.php";
    private Map<String, String> params;

    LoginRequest(String email, String password, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL,listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
