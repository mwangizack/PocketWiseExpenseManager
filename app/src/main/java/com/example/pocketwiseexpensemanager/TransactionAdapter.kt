package com.example.pocketwiseexpensemanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/*class TransactionAdapter(private var transactions:ArrayList<Transaction>):
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {
    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label:TextView= view.findViewById(R.id.mTvLabel)
        val description:TextView= view.findViewById(R.id.mTvDescription)
        val amount:TextView= view.findViewById(R.id.mTvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout,parent,false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context= holder.amount.context

        if (transaction.amount >=0){
            holder.amount.text = "+Ksh%.2f".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
        }else{
            holder.amount.text = "-Ksh%.2f".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
        holder.label.text= transaction.label
        holder.description.text= transaction.description
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}*/

//Using The custom adapter by wanyama

class TransactionAdapter(var context: Context, var data:ArrayList<TransactionRecord>):BaseAdapter() {
    private class ViewHolder(row:View?){
        var label:TextView
        var description:TextView
        var amount:TextView
        init {
            this.label = row?.findViewById(R.id.mTvLabel) as TextView
            this.description = row?.findViewById(R.id.mTvDescription) as TextView
            this.amount = row?.findViewById(R.id.mTvAmount) as TextView
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View?
        var viewHolder:ViewHolder
        if (convertView == null){
            var layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.transaction_layout,parent,false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        var item:TransactionRecord = getItem(position) as TransactionRecord
        viewHolder.label.text = item.label
        viewHolder.description.text = item.description
        viewHolder.amount.text = item.amount


        var tran= data.get(position)
        var context= viewHolder.amount.context
        if (tran.amount.toDouble() >=0){
            viewHolder.amount.text= "Ksh %,.2f".format(tran.amount.toDouble())
            viewHolder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
        }else{
            viewHolder.amount.text= "-Ksh %,.2f".format(Math.abs(tran.amount.toDouble()))
            viewHolder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        return view as View
    }

    override fun getItem(position: Int): Any {
        return  data.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.count()
    }
}