package com.rpn.adminmosque.ui.viewmodel


import android.app.Application
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.rpn.adminmosque.R
import com.rpn.adminmosque.repository.AuthAppRepository
import com.rpn.adminmosque.ui.state.LoginFormState
import com.rpn.adminmosque.utils.Event
import com.rpn.adminmosque.utils.Resource
import kotlinx.coroutines.Dispatchers


class LoginRegisterViewModel(application: Application, val authAppRepository: AuthAppRepository) :
    CoroutineViewModel(Dispatchers.IO) {
    //    private val authAppRepository: AuthAppRepository
    var userLiveData: MutableLiveData<FirebaseUser?>


    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    fun login(email: String?, password: String?) {
        authAppRepository.login(email, password)
    }

    fun register(name: String?, email: String?, password: String?,
                 onComplete: (Event<Resource<String>>) -> Unit) {
        authAppRepository.register(name, email, password){
            onComplete(it)
        }
    }

    fun logOut() {
        authAppRepository.logOut()
    }


    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(userEmailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun registerDataChanged(username: String, email: String, phone: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(userNameError = R.string.invalid_username)
        } else if (!isUserNameValid(email)) {
            _loginForm.value = LoginFormState(userEmailError = R.string.invalid_email)
        } else if (!isPhoneValid(phone)) {
            _loginForm.value = LoginFormState(phoneError = R.string.invalid_phone)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    // A placeholder password validation check
    private fun isPhoneValid(phone: String): Boolean {
        return phone.length > 9
    }



    init {
//        authAppRepository = AuthAppRepository(application)
        userLiveData = authAppRepository.userLiveData
    }
}