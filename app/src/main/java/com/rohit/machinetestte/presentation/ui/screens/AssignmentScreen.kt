package com.rohit.machinetestte.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rohit.machinetestte.presentation.ui.viewmodels.AssignmentViewModel

@Composable
fun AssignmentScreen(
    viewModel: AssignmentViewModel = hiltViewModel()
) {

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(it)) {

        }
    }


}