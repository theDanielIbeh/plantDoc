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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        }
        viewModel.insertData()
        return binding.root
    }

    private fun proceedToHomePage(
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            val user =
                withContext(Dispatchers.IO) { viewModel.getUserDetails(email = email) }
            if (user != null) {
                if (user.password == password) {
                    viewModel.savePreferences(user)
                        val intent = Intent(requireContext(), AppActivity::class.java)
                        startActivity(intent)
                        viewModel.resetLoginModel()
                } else {
                    binding.passwordLayout.error = "Incorrect password"
                }
            } else {
                binding.usernameLayout.error = "This email is not registered"
            }
        }
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