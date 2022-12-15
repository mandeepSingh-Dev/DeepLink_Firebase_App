package com.example.deeplink_firebase_app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.deeplink_firebase_app.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        receiveDynamicLinkDetails()


        binding.createLink.setOnClickListener {

            if( binding.editText.text?.isNotBlank() == true)
            {
                val longUri = createDynamicLink(id = binding.editText.text.toString())

                Log.d("difdgdfd",longUri.toString())
                /**Shortening the longUri
                 * that can be ( https://kjdf.page.link?apn=com.example.deeplink_firebase_app&ibi=com.example.ios&link=https%3A%2F%2Fcom.example.deeplink_firebase_app%2FMainActivity3%3Fid%3D50%26text%3DHello )
                 */
                Firebase.dynamicLinks.shortLinkAsync {
                    longUri?.let { longLink = it }

                }.addOnSuccessListener {
                    binding.textview.text = it.shortLink.toString()
                }.addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            else{
                binding.textinputlayout.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                binding.textinputlayout.isErrorEnabled = true
                binding.textinputlayout.error = "Enter Id to Continue"
            }


        }

        binding.textview.setOnClickListener {
            /**Opening link here*/
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(binding.textview.text.toString())
            startActivity(intent)
           /**---------------------*/
        }
    }

    fun createDynamicLink(id: String): Uri? {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            link = Uri.parse("https://com.example.deeplink_firebase_app/MainActivity3?id=$id&text=Hello")
            domainUriPrefix = "https://kjdf.page.link"

            androidParameters { build() }

            iosParameters("com.example.ios") {}
            buildDynamicLink()

        }
        return dynamicLink.uri
    }

    fun receiveDynamicLinkDetails(){

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->

                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                /** this is for if path is MainActivity2 then start MainActivity2 */
                if (deepLink?.path?.contains("/MainActivity2") == true) {
                    startActivity(Intent(this, MainActivity2::class.java))
                }

                /**this is for receiving query parameters from uri */
                val queryp = "id"
                val receivedquery_Id = deepLink?.getQueryParameter(queryp).toString()
                if(receivedquery_Id.equals("null")) {
                }else{
                    binding.parameterTextView.text = "$queryp = $receivedquery_Id"
                }

            }
            .addOnFailureListener(this) { e -> }

    }



}
