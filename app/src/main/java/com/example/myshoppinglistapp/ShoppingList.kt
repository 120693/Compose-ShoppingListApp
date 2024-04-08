package com.example.myshoppinglistapp

import android.app.AlertDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false  // 편집하고 있는지
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp() {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }  // 다이얼로그 보이는지 여부
    var itemName by remember { mutableStateOf("") }  // OutlinedTextField 에 들어갈 아이템 이름
    var itemQuantity by remember { mutableStateOf("") }  // OutlinedTextField 에 들어갈 아이템 개수

    Column(
        // Column 이 화면 전체를 차지하고, 정중앙에 위치하기 위해서
        // UI 수정하려면 modifier 필수
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { showDialog = true }) {
            // content 는 대괄호에서도 작성 가능
            Text(text = "Add Item")

            // 람다함수
            // 여기서는 입력값 형식과 출력값 형식을 지정해주고 어떤 값이 출력되어야하는지 정함
//            val doubleNumber: (Int) -> Int = { it * 2 }
//            Text(text = doubleNumber(5).toString())
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sItems) {item ->
//                ShoppingListItem(item = it, onEditClick = {}, {})  // 보이는지 확인용
                if (item.isEditing) {
                    ShoppingItemEditor(item = item, onEditComplete = { editedName, editedQuantity ->
                        sItems = sItems.map { it.copy(isEditing = false) }

                        // 현재 수정중인 아이템과 같은 아이템의 세부사항을 업데이트!
                        val editedItem = sItems.find {
                            it.id == item.id
                        }

                        editedItem?.let {
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    })
                } else {
                    // 편집중이지 않으면 그냥 그대로 보여주면 됨
                    ShoppingListItem(
                        item = item,
                        onEditClick = { // 편집 버튼을 누르면 편집이 가능해야 하는데, 클릭한 아이템과 목록의 아이디가 같을 경우에만 편집이 활성화 되어야 한다.
                        sItems = sItems.map {
                            it.copy(isEditing = (it.id == item.id))
                        } },
                        onDeleteClick = { // 삭제
                        sItems = sItems - item
                    })
                }
            }
        }
    }

    // 버튼이 눌리면 다이어로그가 보인다
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    
                    Button(onClick = {
                        // 빈 객체가 아니라면 ShoppingItem에 추가
                        if (itemName.isNotBlank()) {
                            val newItem = ShoppingItem(
                                id = sItems.size + 1,
                                name = itemName,
                                quantity = itemQuantity.toInt()
                            )

                            sItems = sItems + newItem
                            showDialog = false
                            itemName = ""
                            itemQuantity = ""
                        }
                    }) {
                        Text(text = "Add")
                    }
                    
                    Button(onClick = { showDialog = false }) {
                        Text(text = "Cancel")
                    }
                }
                            },
            title = { Text(text = "Add Shopping Item") },
            text = { 
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        )
    }
}

// 쇼핑 리스트 모양 설정
@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,  // 여기서 람다함수는 입력값이 없고 Unit(출력값 없음)임 -> 그저 함수를 전달하는 용도
    onDeleteClick: () -> Unit
) {
    Row(
      modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
          .border(
              border = BorderStroke(2.dp, Color.White),
              shape = RoundedCornerShape(20)
          ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))

        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Button")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Button")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(
    item: ShoppingItem,  // 수정할 항목
    onEditComplete: (String, Int) -> Unit  // 수정 후 변경된 이름과 양을 보내야함
) {
    var editedName by remember { mutableStateOf(item.name) }  // 수정이 되면 덮어씌워야 함
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }  // 수정이 되면 덮어씌워야 함
    var isEditing by remember { mutableStateOf(item.isEditing) }  // 편집중인지

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )

            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }

        Button(onClick = {
            isEditing = false
            onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
        }) {
            Text(text = "save")
        }
    }
}

// map
/*
* val numbers = listOf(1,2,3)
* val doubled = numbers.map { it * 2 }
* ------> Result [2,4,6]
* */

// copy
/*
* data class Vase(val color: String, val design: String)
*
* fun main() {
*   val blueRoseVase = Vase(color = "blue", desing = "rose")
*   val redRoseVase = blueRoseBase.copy()
*   // 만약 속성을 바꾸고 싶다면 blueRoseBase.copy(color = "red") 이런 방식으로 사용 가능
* }
* */
