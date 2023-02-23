package com.example.jettipapp.utils

fun calculateTotalTip (totalBillState : Double, tipPercentage : Int) : Double {

    return if (totalBillState > 1 && totalBillState.toString().isNotEmpty()) (totalBillState * tipPercentage)/100 else 0.0
}

fun calculateTotalPerPerson (totalBillState: Double, tipPercentage: Int, splitPerson : Int) : Double {

    val bill = calculateTotalTip(totalBillState,tipPercentage) + totalBillState

    return (bill / splitPerson)
}