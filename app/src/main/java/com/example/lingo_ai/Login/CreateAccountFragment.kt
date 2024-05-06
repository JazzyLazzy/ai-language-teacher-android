package com.example.lingo_ai.Login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lingo_ai.LanguageFragment.LanguageFragment
import com.example.lingo_ai.R
import com.example.lingo_ai.databinding.FragmentCreateAccountBinding
import com.lazarus.cloudapi.createUser
import com.lazarus.cloudapi.loginUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateAccountFragment : Fragment() {

    private lateinit var binding:FragmentCreateAccountBinding
    private lateinit var pword: EditText;
    private lateinit var email: EditText;
    private lateinit var repword: EditText;
    private lateinit var createAccButton: AppCompatButton;
    private lateinit var nomatch: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pword = binding.pword;
        email = binding.email;
        createAccButton = binding.createAccButton;
        repword = binding.repword
        nomatch = binding.nomatch
        createAccButton.setOnClickListener {
            val usrEmail = email.text.toString();
            val usrPword = pword.text.toString();
            var authToken: String

            // non-block
            CoroutineScope(Dispatchers.IO).launch {
                authToken = createUser(usrEmail, usrPword, requireContext())
                // back to UI thread
                lifecycleScope.launch {
                    when (authToken) {
                        "403" -> {
                            ExistDialogue().show(parentFragmentManager, "exist_d")
                        }

                        "503" -> {
                            OopsDialogue().show(parentFragmentManager, "oops_d")
                        }

                        else -> {
                            val bundle = Bundle().apply {
                                putString("authToken", authToken)
                            }

                            val languageFragment = LanguageFragment()
                            languageFragment.arguments = bundle

                            // Use FragmentTransaction to replace the current fragment with languageFragment
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, languageFragment)
                                .addToBackStack(null) // Optional: Add the transaction to the back stack
                                .commit()
                        }
                    }
                }
            }
        }

        repword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val password1 = pword.text.toString()
                val password2 = s.toString()

                if (password1 == password2) {
                    pword.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                    repword.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                    createAccButton.isEnabled = true
                    nomatch.visibility = View.GONE
                } else if (password2.isEmpty()) {
                    // Set background color to white if the second password box is empty
                    pword.setBackgroundColor(resources.getColor(android.R.color.white))
                    repword.setBackgroundColor(resources.getColor(android.R.color.white))
                    createAccButton.isEnabled = false
                    nomatch.visibility = View.GONE
                } else {
                    pword.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                    repword.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                    createAccButton.isEnabled = false
                    nomatch.visibility = View.VISIBLE
                }
            }
        })
    }
}