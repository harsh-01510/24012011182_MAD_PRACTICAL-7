package com.example.a24012011182_mad_practical_7

import org.json.JSONObject
import java.io.Serializable

data class Person(
    var id: String = "",
    var name: String = "",
    var number: String = "",
    var emailId: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : Serializable {

    constructor(json: JSONObject) : this() {
        id = json.optString("id")

        emailId = when {
            json.has("emailId") -> json.optString("emailId")
            json.has("email") -> json.optString("email")
            else -> json.optJSONObject("profile")?.optString("email") ?: ""
        }

        number = when {
            json.has("Number") -> json.optString("Number")
            json.has("number") -> json.optString("number")
            json.has("phone") -> json.optString("phone")
            json.has("phoneNo") -> json.optString("phoneNo")
            json.has("phone_no") -> json.optString("phone_no")
            json.has("mobile") -> json.optString("mobile")
            else -> json.optJSONObject("profile")?.optString("phone")
                ?: json.optJSONObject("profile")?.optString("number")
                ?: ""
        }

        val profile = json.optJSONObject("profile")
        name = profile?.optString("name") ?: json.optString("name")
        address = profile?.optString("address") ?: json.optString("address")
        val loc = profile?.optJSONObject("location")
        latitude = loc?.optDouble("latitude") ?: json.optDouble("latitude")
        longitude = loc?.optDouble("longitude") ?: json.optDouble("longitude")
    }
}