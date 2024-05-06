package com.example.lingo_ai.Login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.lingo_ai.LanguageFragment.LanguageFragment
import com.example.lingo_ai.R
import com.example.lingo_ai.databinding.FragmentLanguageBinding
import com.example.lingo_ai.databinding.FragmentLoginBinding
import com.lazarus.cloudapi.loginUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var loginButton: AppCompatButton;
    private lateinit var pword: EditText;
    private lateinit var email: EditText;
    private lateinit var createAcc: AppCompatButton;
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginButton = binding.loginButton;
        pword = binding.pword;
        email = binding.email;
        createAcc = binding.createAcc;

        loginButton.setOnClickListener {
            val usrEmail = email.text.toString();
            val usrPword = pword.text.toString();
            var authToken:String

            // non-block
            CoroutineScope(Dispatchers.IO).launch {
                authToken = loginUser(usrEmail, usrPword, requireContext())
                // back to UI thread
                withContext(Dispatchers.Main){
                    when (authToken) {
                        "401" -> {
                            CredIncDialogue().show(parentFragmentManager, "cred_inc_d")
                        }
                        "503" -> {
                            OopsDialogue().show(parentFragmentManager, "oops_d")
                        }
                        else -> {
                            startLearning(parentFragmentManager, authToken)
                        }
                    }
                }
            }
        }

        createAcc.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateAccountFragment())
                .addToBackStack(null) // Optional: Add the transaction to the back stack
                .commit()
        }
    }
}

fun startLearning(fragmentManager: FragmentManager, authToken:String){
    val bundle = Bundle().apply {
        putString("authToken", authToken)
    }
    val languageFragment = LanguageFragment()
    languageFragment.arguments = bundle

    // Use FragmentTransaction to replace the current fragment with languageFragment
    fragmentManager.beginTransaction()
        .replace(R.id.fragment_container, languageFragment)
        .addToBackStack(null) // Optional: Add the transaction to the back stack
        .commit()
}