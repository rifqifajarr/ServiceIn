package com.servicein.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import com.servicein.ui.screen.order.LocationSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlaceBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: LocationSearchViewModel = viewModel(),
    onLocationSelected: (LatLng) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val results = viewModel.searchResults

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
        modifier = modifier
            .wrapContentHeight(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    if (query.length >= 3) {
                        viewModel.searchPlaces(context, query)
                    }
                },
                label = { Text("Cari lokasi...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(results) { prediction ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.getPlaceLatLng(
                                    context,
                                    prediction.placeId
                                ) { latLng ->
                                    onLocationSelected(latLng)
                                }
                            }
                            .padding(12.dp)
                    ) {
                        Text(prediction.getPrimaryText(null).toString())
                        Text(
                            prediction.getSecondaryText(null).toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}