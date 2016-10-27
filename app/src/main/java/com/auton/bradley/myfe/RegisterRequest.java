package com.auton.bradley.myfe;

import java.util.HashMap;
import java.util.Map;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


/**
 * Created by Bradley on 27/10/2016.
 *
 */

class RegisterRequest extends StringRequest {
    private  static final String REGISTER_REQUEST_URL = "https://myfe.000webhostapp.com/Register_User.php";
    private Map<String, String> params;

    RegisterRequest(String email, String password, String name, String dob, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL,listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("name", name);
        params.put("dob", dob);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
