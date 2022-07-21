package com.mid.mad_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup_login.*
import org.w3c.dom.Text
import java.util.regex.Matcher
import java.util.regex.Pattern

class Signup_Login : AppCompatActivity() {

    var name: String = ""
    var age: String = ""
    var email: String = ""
    var pass: String = ""
    var fm_name: String = ""
    var gpass: String = ""
    var gemail: String = ""
    lateinit var fb: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    lateinit var fbs: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_login)
        val type: String? = intent.getStringExtra("type")
        FirebaseApp.initializeApp(this)
        fb = FirebaseAuth.getInstance()
        fbs = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        login_pane.visibility = View.VISIBLE
        signup_pane.visibility = View.GONE

        login.setOnClickListener {
            g_login()
        }

    }

    fun new_details(view: View) {
        login_pane.visibility = View.GONE
        signup_pane.visibility = View.VISIBLE
    }

    fun store_data(view: View) {
        name = new_name.text.toString().trim()
        email = new_email.text.toString().trim()
        age = new_age.text.toString().trim()
        pass = new_pass.text.toString().trim()
        fm_name = new_familyname.text.toString().trim()
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(age) && TextUtils.isEmpty(
                pass
            ) && TextUtils.isEmpty(fm_name)
        ) {

            new_name.error = "Required"
            new_email.error = "Required"
            new_age.error = "Required"
            new_pass.error = "Required"
        } else {

            if (age.toInt() < 35) {
                new_age.error = "Age must be greater than 35"
                Toast.makeText(this, "Guardian Age must be greater than 35", Toast.LENGTH_LONG)
                    .show()
            } else if (pass.length < 10) {
                new_pass.error = "Must be Greater than 10 Characters"
            } else {
                val p: Pattern = Pattern.compile("[^A-Z0-9]", Pattern.CASE_INSENSITIVE)
                val m: Matcher = p.matcher(pass)
                val b: Boolean = m.find()
                if (!b) {
                    new_pass.error = "Not matched with required"
                } else if (b) {
                    fb.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val guardian = fbs!!.child("Guardian/$fm_name")
                                val bart = guardian.child("Guardian_$name")
                                bart.child("Type").setValue("Guardian")
                                bart.child("Name").setValue(name)
                                bart.child("FM_Password").setValue(pass)

                                val bart2 = guardian.child("Family_$fm_name")
                                bart2.child("Type").setValue("Family Member")
                                bart2.child("Name").setValue(fm_name)
                                bart2.child("FM_Password").setValue(pass)
                                val intent : Intent = Intent(this,Track_location::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Unable to create user ", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }
            }

        }

    }

    fun g_login() {

        if (TextUtils.isEmpty(G_email.text.toString()) && TextUtils.isEmpty(G_password.text.toString())) {
            G_email.error = "Required"
            G_password.error = "Required"
        } else {
            gemail = G_email.text.toString().trim()
            gpass = G_password.text.toString().trim()
            if (gpass.length < 10) {
                G_password.error = "Password must greater than 10"
            } else {
                val p: Pattern = Pattern.compile("[^A-Z0-9]", Pattern.CASE_INSENSITIVE)
                val m: Matcher = p.matcher(gpass)
                val b: Boolean = m.find()
                if (!b) {
                    G_password.error = "Not matched with required"
                } else if (b) {
                    auth.signInWithEmailAndPassword(gemail, gpass)
                        .addOnCompleteListener(this@Signup_Login) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "User Created", Toast.LENGTH_LONG).show()
                                val intent : Intent = Intent(this,Track_location::class.java)
                                startActivity(intent)
                            } else if (task.isCanceled) {
                                Toast.makeText(this, "Unable to create user ", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }
            }
        }
        auth.signOut()
    }
}