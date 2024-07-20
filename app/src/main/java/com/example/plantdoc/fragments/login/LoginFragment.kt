package com.example.plantdoc.fragments.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plantdoc.AppActivity
import com.example.plantdoc.R
import com.example.plantdoc.databinding.FragmentLoginBinding
import com.example.plantdoc.utils.FormFunctions.validateLoginEmail
import com.example.plantdoc.utils.FormFunctions.validateLoginPassword
import com.example.plantdoc.utils.HelperFunctions.hideKeyboard
import com.example.plantdoc.utils.HelperFunctions.isInternetAvailable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    companion object {
        private val TAG = LoginFragment::class.java.name
    }

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        Log.d(TAG, TAG)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.loadUsers()
        viewModel.resetLoginModel()
        with(binding) {
            usernameEditText.requestFocus()
            usernameEditText.doAfterTextChanged {
                validateLoginEmail(it.toString(), binding.usernameLayout)
            }
            passwordEditText.doAfterTextChanged {
                validateLoginPassword(it.toString(), binding.passwordLayout)
            }
            passwordEditText.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick()
                }
                false
            }
            btnLogin.setOnClickListener {
                val (email, password) = viewModel?.loginModel?.value ?: LoginModel()
                val isFormValid = validateFields(email = email, password = password)
                if (isFormValid) {
                    proceedToHomePage(email, password)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Some fields require your attention.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            newUserAction.setOnClickListener {
                navigateToRegisterScreen()
            }

            anonymousAction.setOnClickListener {
                navigateToHomeScreen()
            }
        }
//        viewModel.insertData()
        return binding.root
    }

    private fun proceedToHomePage(
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            hideKeyboard(requireActivity())
            if (!isInternetAvailable(requireContext())) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                return@launch
            }
            try {
                viewModel.login(email = email, password = password)
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                navigateToHomeScreen()
                viewModel.resetLoginModel()
            } catch (e: Exception) {
                // Handle the error
                Log.d("Exception", e.message.toString())
                if (e.message == "Wrong password") {
                    binding.passwordLayout.error = "Incorrect password"
                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (e.message == "Unregistered email") {
                    binding.usernameLayout.error = "Unregistered email"
                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(requireContext(), AppActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegisterScreen() {
        findNavController().navigate(R.id.registerFragment)
    }

    private fun validateFields(email: String, password: String): Boolean {
        val isEmailValid = validateLoginEmail(email, binding.usernameLayout)
        val isPasswordValid = validateLoginPassword(password, binding.passwordLayout)

        return isEmailValid && isPasswordValid
    }
}