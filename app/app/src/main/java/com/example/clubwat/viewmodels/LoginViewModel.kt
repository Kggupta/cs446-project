package com.example.clubwat.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.example.clubwat.BuildConfig
import com.example.clubwat.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var allValuesError = mutableStateOf<String?>(null)
    val loginError = mutableStateOf<String?>(null)

    fun areAllValuesFilled(email: String, password: String) {
        if (email != "" && password != "") {
            allValuesError.value = null
        } else {
            allValuesError.value = "Please fill in all values."
        }
    }

    private fun updateUserInfoBasedOnToken(userToken: String) {
        try {
            val decodedJWT = JWT.decode(userToken)
            decodedJWT?.let { jwt ->
                firstName.value = jwt.getClaim("first_name").asString()
                lastName.value = jwt.getClaim("last_name").asString()
                userRepository.createUser(firstName, lastName, email, password)
            }
        } catch (e: Exception) {
            println(e.printStackTrace())
        }
    }

    fun login(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var loggedIn = false
            try {
                val url = URL(BuildConfig.LOGIN_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val body = """{"email": "${email.value}", "password": "${password.value}"}"""
                    OutputStreamWriter(outputStream).use { it.write(body) }

                    val responseCode = responseCode
                    loggedIn = responseCode == HttpURLConnection.HTTP_OK
                    if (loggedIn) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val token = jsonResponse.optString("data", null.toString())
                        updateUserInfoBasedOnToken(token)
                        userRepository.setUserId(token)
                        println("Response: $response")
                    } else {
                        loginError.value = "Invalid Email or Password"
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Error Response: $response")
                    }
                }
            } catch (e: Exception) {
                loginError.value = "Invalid Email or Password"
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(loggedIn)
            }
        }
    }
}
