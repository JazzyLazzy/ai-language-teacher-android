package com.example.lingo_ai.Login

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CredIncDialogue : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Invalid Credentials")
                .setMessage("The username or password is incorrect. Please try again.")
                .setPositiveButton("OK") { _, _ ->
                    // You can add any specific actions here if needed
                }

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}