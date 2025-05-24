package com.servicein.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.servicein.R
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleOrderBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onSubmitButtonClick: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = LocalDateTime.now()

    var selectedDate by remember { mutableStateOf(now.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(now.toLocalTime().withMinute((now.minute / 30) * 30).withSecond(0).withNano(0)) }

    val availableDates = remember {
        (0..6).map { now.toLocalDate().plusDays(it.toLong()) }
    }

    val availableTimes = remember {
        generateSequence(LocalTime.of(8, 0)) { time ->
            val next = time.plusMinutes(30)
            if (next.isBefore(LocalTime.of(17, 0))) next else null
        }.toList()
    }

    var isDateMenuExpanded by remember { mutableStateOf(false) }
    var isTimeMenuExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier.wrapContentHeight(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Calendar Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        stringResource(R.string.schedule_order),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date Picker Dropdown
            ExposedDropdownMenuBox(
                expanded = isDateMenuExpanded,
                onExpandedChange = { isDateMenuExpanded = !isDateMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Hari") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDateMenuExpanded) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isDateMenuExpanded,
                    onDismissRequest = { isDateMenuExpanded = false }
                ) {
                    availableDates.forEach { date ->
                        DropdownMenuItem(
                            text = { Text(date.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"))) },
                            onClick = {
                                selectedDate = date
                                isDateMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Picker Dropdown
            ExposedDropdownMenuBox(
                expanded = isTimeMenuExpanded,
                onExpandedChange = { isTimeMenuExpanded = !isTimeMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Jam") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTimeMenuExpanded) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isTimeMenuExpanded,
                    onDismissRequest = { isTimeMenuExpanded = false }
                ) {
                    availableTimes.forEach { time ->
                        DropdownMenuItem(
                            text = { Text(time.format(DateTimeFormatter.ofPattern("HH:mm"))) },
                            onClick = {
                                selectedTime = time
                                isTimeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    val result = LocalDateTime.of(selectedDate, selectedTime)
                    onSubmitButtonClick(result)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .height(42.dp)
            ) {
                Text(
                    text = "Submit",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}
