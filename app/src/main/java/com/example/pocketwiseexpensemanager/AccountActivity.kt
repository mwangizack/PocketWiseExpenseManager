package com.example.pocketwiseexpensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*

class AccountActivity : AppCompatActivity() {
    private lateinit var loggedInOn:TextView
    private lateinit var name:TextView
    private lateinit var email:TextView
    private lateinit var logout:Button
    private lateinit var profileImage:CircleImageView
    private lateinit var mAuth:FirebaseAuth
    private lateinit var onlineUserId:String
    private lateinit var dbRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        loggedInOn= findViewById(R.id.mTvLoggedInOn)
        name= findViewById(R.id.mTvName)
        email= findViewById(R.id.mTvEmail)
        logout= findViewById(R.id.mBtnLogout)
        profileImage= findViewById(R.id.imgProfile)

        mAuth= FirebaseAuth.getInstance()
        onlineUserId= mAuth.currentUser?.uid.toString()
        dbRef= FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserId)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loggedInOn.setText(snapshot.child("logedinon").getValue().toString())
                name.setText(snapshot.child("name").getValue().toString())
                email.setText(snapshot.child("email").getValue().toString())

                Glide.with(applicationContext).load(snapshot.child("profilepictureurl").getValue().toString()).into(profileImage)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "DB Locked", Toast.LENGTH_LONG).show()
            }
        })
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(applicationContext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK.and(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            startActivity(intent)
            Toast.makeText(applicationContext, "Logged out successfully", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}