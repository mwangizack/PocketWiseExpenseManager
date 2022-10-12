package com.example.pocketwiseexpensemanager

class TransactionRecord {
    var label:String= ""
    var description:String= ""
    var amount:String=""

    constructor(label: String, description: String, amount: String) {
        this.label = label
        this.description = description
        this.amount = amount
    }

    constructor()
}