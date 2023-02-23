package com.example.jettipapp

import android.graphics.Paint.Align
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat.FocusDirection
import com.example.jettipapp.components.InputField
import com.example.jettipapp.components.RoundedButtonIcon
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.utils.calculateTotalPerPerson
import com.example.jettipapp.utils.calculateTotalTip


@ExperimentalComposeApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp (content : @Composable () -> Unit) {

    androidx.compose.material.Surface (color = Color.White) {
        content()
    }
}

@Composable
fun TopHeader (totalPerPerson : Double) {

    androidx.compose.material.Surface (
            modifier = Modifier
                .padding(20.dp)
                .height(150.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colors.primary,
            shape = RoundedCornerShape(corner = CornerSize(5.dp))
            ) {
            Column (
                modifier = Modifier.padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                    ) {
                val total = "%.2f".format(totalPerPerson)
                Text(text = "Total Per Person", style = MaterialTheme.typography.h5)
                Text(text = "$total", style = MaterialTheme.typography.h4)
            }
    }
}


@Composable
fun MainContent () {
    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm() { billAmt ->
            Log.d("AMT", "Main Content: $billAmt")
        }
    }

}

@Composable
fun BillForm (modifier: Modifier = Modifier, onValChange : (String) -> Unit = {}) {

    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember (totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val focusManager = LocalFocusManager.current;

    val sliderState = remember {
        mutableStateOf(0f)
    }

    val convertSliderValue = (sliderState.value * 100).toInt();

    val splitState = remember {
         mutableStateOf(0)
    }

    val range = IntRange(start = 1, endInclusive = 10)

    val totalPerPerson = remember {
         mutableStateOf(0.0)
    }


    TopHeader(totalPerPerson = totalPerPerson.value)

    androidx.compose.material.Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 2.dp, color = Color.LightGray)
    ) {
        Column (
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
                ) {
            InputField(valueState = totalBillState, labelId = "Enter Bill", singleLine = true, isEnabled = true, keyboardActions = KeyboardActions(onDone = {
                if (!validState) return@KeyboardActions

                focusManager.clearFocus()
                onValChange(totalBillState.value.trim())
            }))

            if (validState) {
                Row (
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                        ) {
                    Text(text = "Split", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row (
                        modifier = Modifier.padding(all = 4.dp),
                        horizontalArrangement = Arrangement.End
                            ) {
                        RoundedButtonIcon(imageVector = Icons.Default.Remove, onClick = {
                            splitState.value =
                                if (splitState.value > 1) splitState.value -1
                                else 1
                                totalPerPerson.value = calculateTotalPerPerson(totalBillState.value.toDouble(), convertSliderValue, splitState.value)
                        })
                        Text(text = "${splitState.value}", modifier = Modifier
                            .align(
                                alignment = Alignment.CenterVertically
                            )
                            .padding(horizontal = 5.dp))
                        RoundedButtonIcon(imageVector = Icons.Default.Add, onClick = {
                            if (splitState.value < range.last) {
                                splitState.value = splitState.value + 1
                            }
                            totalPerPerson.value = calculateTotalPerPerson(totalBillState.value.toDouble(), convertSliderValue, splitState.value)
                        })
                    }
                }

                Row (
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                        ) {

                    Text(text = "Total", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))

                    Spacer(modifier = Modifier.width(130.dp))
                    Text(text = "Total: $ ${calculateTotalTip(totalBillState.value.toDouble(), convertSliderValue)}", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp)
                    )
                }

                Column (
                    modifier = Modifier.padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                        ) {
                    Text(text = "$convertSliderValue %", modifier = Modifier.align(
                        alignment = Alignment.CenterHorizontally
                    ), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp))

                    Spacer(modifier = Modifier.height(30.dp))

                    Slider(value = sliderState.value, onValueChange = {value ->
                        sliderState.value = value
                        totalPerPerson.value = calculateTotalPerPerson(totalBillState.value.toDouble(), convertSliderValue, splitState.value)
                    }, modifier = Modifier.padding(start = 16.dp, end = 16.dp), steps = 5)
                }

            }

        }

    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        MainContent()
    }
}