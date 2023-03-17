package com.example.cookim.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.cookim.HomePage
import com.example.cookim.controller.Controller
import com.example.cookim.databinding.ActivityLoginBinding
import com.example.cookim.model.Model
import com.example.cookim.model.user.UserModel

class LoginKotlinActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null

    var listener: View.OnClickListener? = null
    var swLogOption: Switch? = null

    //Array de prueba
    var users: List<UserModel> = ArrayList()

    var model: Model? = null
    var controller: Controller? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        model = Model()
        initElements()
    }

    private fun showHomePage() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
    }


    /**
     * initialize all elements
     */
    private fun initElements() {
        binding?.btLogin?.setOnClickListener {
        }
    }

    /**
     * Adds all necessaries elements to listener
     */
    private fun addElementsToListener() {
        binding?.btLogin?.setOnClickListener(listener)
    }
}