package com.rpn.adminmosque.ui.state

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val userNameError: Int? = null,
    val userEmailError: Int? = null,
    val phoneError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)