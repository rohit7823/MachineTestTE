package com.rohit.machinetestte.presentation.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rohit.machinetestte.R
import com.rohit.machinetestte.base.domain.entities.Status
import com.rohit.machinetestte.presentation.others.OnEffect
import com.rohit.machinetestte.presentation.others.openApp
import com.rohit.machinetestte.presentation.others.rememberSnackbarHostState
import com.rohit.machinetestte.presentation.ui.composables.LoadingDotsView
import com.rohit.machinetestte.presentation.ui.states.AppModel
import com.rohit.machinetestte.presentation.ui.viewmodels.AssignmentViewModel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@Composable
fun AssignmentScreen(
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val snackbarHostState = rememberSnackbarHostState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Text(
                text = stringResource(id = R.string.suvojit),
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.White)
            )
            Card(
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 7.dp
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                val networkState = viewModel.appConnectivity.collectAsStateWithLifecycle()
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (networkState.value) stringResource(id = R.string.connected) else stringResource(
                            id = R.string.not_connected
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(end = 5.dp)
                    )

                    Icon(
                        imageVector = if (networkState.value) Icons.Default.CheckCircle else Icons.Default.NotInterested,
                        contentDescription = "Checked",
                        tint = if (networkState.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                }
            }
            ViewPagerSection(
                pages = viewModel.pages,
                apps = viewModel.apps.collectAsStateWithLifecycle().value,
                loading = viewModel.loading,
                onChangeStatus = viewModel::onTapStatus,
                onSearching = viewModel::searchApps
            )
        }
    }

    LaunchedEffect(viewModel.searchQuery.collectAsStateWithLifecycle().value) {
        snapshotFlow {
            viewModel.searchQuery.value
        }.debounce(500L).onEach(action = viewModel::filterApps).launchIn(this)
    }

    viewModel.notifier.OnEffect(
        intentionalCode = {
            if (it.isNotEmpty()) {
                snackbarHostState.showSnackbar(message = it)
            }
        }, clearance = { "" })

    val context = LocalContext.current

    viewModel.openAppIntent.OnEffect(
        intentionalCode = {
            if (it.isNotEmpty()) {
                try {
                    context.openApp(it)
                } catch (ex: IllegalArgumentException) {
                    ex.message?.let {
                        snackbarHostState.showSnackbar(it)
                    }
                }

            }
        }, clearance = { "" }
    )

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewPagerSection(
    pages: List<Int>,
    apps: SnapshotStateList<AppModel>,
    loading: Boolean,
    onChangeStatus: (item: AppModel, state: Boolean) -> Unit,
    onSearching: (TextFieldValue) -> Unit
) {

    val pagerState = rememberPagerState()
    val uiScope = rememberCoroutineScope()
    val width = LocalConfiguration.current.screenWidthDp.dp

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = MaterialTheme.colorScheme.primary,
        indicator = {
            it.forEachIndexed { index, tabPosition ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPosition)
                        .height(4.dp)
                        .padding(horizontal = 28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = if (index == pagerState.currentPage) Color.White else Color.Transparent)
                )
            }
        }
    ) {
        pages.forEachIndexed { index, tabName ->
            Tab(selected = index == pagerState.currentPage, onClick = {
                uiScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }, text = {
                Text(
                    text = stringResource(tabName),
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                )
            }, modifier = Modifier.width(width * .40f))
        }
    }
    HorizontalPager(
        pageCount = pages.size,
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> ApplicationsSection(
                apps = apps,
                loading = loading,
                onChangeStatus = onChangeStatus,
                onSearching = onSearching
            )

            1 -> SettingsSection()
        }
    }
}

@Composable
fun SettingsSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.no_ui_specified),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun ApplicationsSection(
    apps: SnapshotStateList<AppModel>,
    loading: Boolean,
    onChangeStatus: (item: AppModel, state: Boolean) -> Unit,
    onSearching: (TextFieldValue) -> Unit
) {
    var searchValue by remember {
        mutableStateOf(TextFieldValue())
    }
    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .padding(all = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = searchValue,
            onValueChange = {
                if (!loading) {
                    searchValue = it
                    onSearching(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth(fraction = .95f)
                .bringIntoViewRequester(bringIntoViewRequester),
            textStyle = MaterialTheme.typography.labelLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFFE6E6E6),
            ),
            placeholder = {
                Text(
                    stringResource(R.string.search_here),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                onSearching(searchValue)
                keyboardController?.hide()
            }),
            trailingIcon = {
                when {
                    searchValue.text.isNotEmpty() -> {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "clear",
                            modifier = Modifier.clickable {
                                searchValue = TextFieldValue()
                                onSearching(searchValue)
                            })
                    }

                    else -> {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "search")
                    }
                }

            },
        )

        AnimatedContent(
            targetState = loading,
            modifier = Modifier.wrapContentSize(align = Alignment.TopCenter),
            contentAlignment = Alignment.Center,
            label = ""
        ) { state ->
            when (state) {
                true -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingDotsView(
                        dotsNumber = 5, coroutineScope = rememberCoroutineScope(),
                        loading = true,
                        back = false
                    )
                }

                false -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    items(
                        items = apps,
                        key = { it.id }
                    ) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = app.appIcon,
                                    contentDescription = "app_icon",
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    error = painterResource(id = R.drawable.fallback_image),
                                    contentScale = ContentScale.FillBounds
                                )
                                app.appName?.let {
                                    Text(
                                        text = app.appName,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                }
                            }

                            Switch(
                                checked = app.status.value == Status.Active,
                                onCheckedChange = { onChangeStatus(app, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

        }
    }
}
