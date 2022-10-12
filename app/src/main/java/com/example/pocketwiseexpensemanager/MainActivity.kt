package com.example.pocketwiseexpensemanager

import android.app.usage.UsageEvents
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var transactions: ArrayList<TransactionRecord>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var listTransactions: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listTransactions= findViewById(R.id.mListTransactions)
        transactions= ArrayList()
        transactionAdapter= TransactionAdapter(this,transactions)

        //Instantiate the db
        val dbReference= FirebaseDatabase.getInstance().getReference().child("Transactions").orderByChild("id")
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transactions.clear()
                for (snap in snapshot.children) {
                    var trn = snap.getValue(TransactionRecord::class.java)
                    transactions.add(trn!!)
                }
                transactions.reverse()
                updateDashboard()
                transactionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "DB Locked",Toast.LENGTH_LONG).show()
            }
        })
        listTransactions.adapter= transactionAdapter


        mBtnNewTransaction.setOnClickListener {
            val intent= Intent(applicationContext, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }
    //Function to update summaries
    private fun updateDashboard(){
        var balance= transactions.map { it.amount.toDouble() }.sum()
        var budget= transactions.filter { it.amount.toDouble() >0 }.map { it.amount.toDouble() }.sum()
        val spent= balance - budget

        mTvBalance.text= "%,.2f".format(balance)
        mTvBudget.text= "%,.2f".format(budget)
        mTvExpenses.text= "%,.2f".format(spent)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater= menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account){
            var intent= Intent(applicationContext, AccountActivity::class.java);
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}