package com.example.pocketwiseexpensemanager

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_transaction.*

class AddTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        labelInput.addTextChangedListener {
            if (it!!.count() > 0)
                labelLayout.error= null
        }
        descriptionInput.addTextChangedListener {
            if (it!!.count() > 0)
                descriptionLayout.error= null
        }

        amountInput.addTextChangedListener {
            if (it!!.count() > 0)
                amountLayout.error= null
        }

        mBtnTransaction.setOnClickListener {
            val label= labelInput.text.toString().trim()
            val description= descriptionInput.text.toString().trim()
            val amount= amountInput.text.toString().trim()
            var time= System.currentTimeMillis()
            var progress= ProgressDialog(this)
            progress.setTitle("Saving")
            progress.setMessage("Please wait...")

            if (label.isEmpty()) {
                labelLayout.error = "Please enter a valid label"
            }else if (description.isEmpty()) {
                descriptionLayout.error = "Please enter a valid description"
            }else if (amount.isEmpty()) {
                amountLayout.error = "Please enter a valid amount"
            }else{
                //save data
                progress.show()
                var myChild= FirebaseDatabase.getInstance().reference.child("Transactions/$time")
                var data= TransactionRecord(label,description,amount)
                myChild.setValue(data).addOnCompleteListener { task->
                    progress.dismiss()
                    if (task.isSuccessful){
                        labelInput.setText(null)
                        descriptionInput.setText(null)
                        amountInput.setText(null)
                        val intent= Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Transaction Saved successfully", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Saving Failed. Please try again", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        mBtnClose.setOnClickListener {
            finish()
        }
    }
}