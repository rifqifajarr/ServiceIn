package com.servicein.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.servicein.R
import com.servicein.core.util.Util

@Composable
fun WalletAndHistory(
    modifier: Modifier = Modifier,
    onWalletButtonClick: () -> Unit,
    onHistoryButtonClick: () -> Unit,
    wallet: Int
) {
    Row {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .height(62.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onWalletButtonClick() }
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    Util.formatRupiah(wallet),
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Arrow Forward"
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = { onHistoryButtonClick() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier
                .wrapContentWidth()
                .height(60.dp)
        ) {
            Text(
                text = stringResource(id = R.string.history_button),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}