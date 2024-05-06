package com.example.lingo_ai.LanguageFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.lingo_ai.ChatFragment.ChatFragment
import com.example.lingo_ai.ChatFragment.returnToLogin
import com.example.lingo_ai.Login.LoginFragment
import com.example.lingo_ai.R
import com.example.lingo_ai.databinding.FragmentLanguageBinding


class LanguageFragment : Fragment() {

    private lateinit var binding: FragmentLanguageBinding
    private lateinit var cantoButton: AppCompatButton;
    private lateinit var mandoButton: AppCompatButton;
    private var authToken: String? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(inflater)
        if (arguments != null){
            authToken = arguments?.getString("authToken")
        } else {
            returnToLogin(parentFragmentManager)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cantoButton = binding.btnCantonese;
        mandoButton = binding.btnMandarin;

        val bundle = Bundle()
        cantoButton.setOnClickListener{
            bundle.putString("lang", "zh-HK")
            transitionToChat(bundle, authToken)
        }
        mandoButton.setOnClickListener{
            bundle.putString("lang", "zh-TW")
            transitionToChat(bundle, authToken)
        }
    }

    private fun transitionToChat(bundle: Bundle, authToken:String?){
        bundle.putString("authToken", authToken)
        val chatFragment = ChatFragment()
        chatFragment.setArguments(bundle)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, chatFragment)
            .addToBackStack(null)
            .commit()
    }
}