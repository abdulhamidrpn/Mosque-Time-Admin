package com.rpn.adminmosque.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.rpn.adminmosque.MainActivity
import com.rpn.adminmosque.R
import com.rpn.adminmosque.databinding.FragmentRegisterBinding
import com.rpn.adminmosque.ui.viewmodel.LoginRegisterViewModel
import com.rpn.adminmosque.utils.GeneralUtils.loadingDialog
import com.rpn.exchangebook.extensions.afterTextChanged
import org.koin.core.component.inject

class RegisterFragment : CoroutineFragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }


    private val viewModel by inject<LoginRegisterViewModel>()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.signin.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.register.setOnClickListener {
            requireContext().loadingDialog {dialog->
                viewModel.register(
                    binding.etName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPassowrd.text.toString()
                ) {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), it.peekContent().message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        checkEmailpassword()

        viewModel.userLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                updateUiWithUser(it)
            }
        })

        viewModel.loginFormState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            binding.register.isEnabled = loginState.isDataValid

            if (loginState.userEmailError != null) {
                binding.etEmail.error = getString(loginState.userEmailError)
            }
            if (loginState.passwordError != null) {
                binding.etPassowrd.error = getString(loginState.passwordError)
            }
            if (loginState.userNameError != null) {
                binding.etName.error = getString(loginState.userNameError)
            }
            if (loginState.phoneError != null) {
                binding.etPhone.error = getString(loginState.phoneError)
            }
        })


    }

    fun checkEmailpassword() {

        binding.etEmail.afterTextChanged {
            viewModel.registerDataChanged(
                binding.etName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPhone.text.toString(),
                binding.etPassowrd.text.toString()
            )
        }

        binding.etName.afterTextChanged {
            viewModel.registerDataChanged(
                binding.etName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPhone.text.toString(),
                binding.etPassowrd.text.toString()
            )
        }

        binding.etPhone.afterTextChanged {
            settingsUtility.userPhone = it
            viewModel.registerDataChanged(
                binding.etName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPhone.text.toString(),
                binding.etPassowrd.text.toString()
            )
        }

        binding.etPassowrd.apply {
            afterTextChanged {
                viewModel.registerDataChanged(
                    binding.etName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPhone.text.toString(),
                    binding.etPassowrd.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        requireContext().loadingDialog {dialog->
                            viewModel.register(
                                binding.etName.text.toString(),
                                binding.etEmail.text.toString(),
                                binding.etPassowrd.text.toString()
                            ) {
                                dialog.dismiss()
                                Toast.makeText(requireContext(), it.peekContent().message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                }
                false
            }

        }
    }

    private fun updateUiWithUser(model: FirebaseUser) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        requireActivity().setResult(Activity.RESULT_OK)

        //Complete and destroy login activity once successful

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}