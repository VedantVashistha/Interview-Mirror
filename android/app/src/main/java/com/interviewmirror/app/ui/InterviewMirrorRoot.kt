package com.interviewmirror.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private object Routes {
    const val Splash = "splash"
    const val Auth = "auth"
    const val Home = "home"
    const val Category = "category"
    const val Interview = "interview"
    const val Result = "result"
    const val History = "history"
    const val Profile = "profile"
    const val Settings = "settings"
}

@Composable
fun InterviewMirrorRoot(viewModel: InterviewViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.Splash) { SplashScreen { navController.navigate(Routes.Auth) } }
            composable(Routes.Auth) { AuthRoute(onDone = { navController.navigate(Routes.Home) }) }
            composable(Routes.Home) { HomeScreen(state, onStart = { navController.navigate(Routes.Category) }) }
            composable(Routes.Category) {
                CategoryScreen(
                    state = state,
                    onCategory = viewModel::chooseCategory,
                    onDifficulty = viewModel::chooseDifficulty,
                    onMode = viewModel::chooseMode,
                    onBegin = {
                        viewModel.startInterview()
                        navController.navigate(Routes.Interview)
                    }
                )
            }
            composable(Routes.Interview) {
                InterviewScreen(
                    state = state,
                    onAnswer = viewModel::updateAnswer,
                    onSubmit = viewModel::submitAnswer,
                    onResult = { navController.navigate(Routes.Result) }
                )
            }
            composable(Routes.Result) { ResultScreen(state) }
            composable(Routes.History) {
                LaunchedEffect(Unit) { viewModel.loadHistory() }
                HistoryScreen(state, onRefresh = viewModel::loadHistory)
            }
            composable(Routes.Profile) { ProfileScreen() }
            composable(Routes.Settings) { SettingsScreen() }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val entry by navController.currentBackStackEntryAsState()
    val current = entry?.destination?.route
    val items = listOf(Routes.Home, Routes.History, Routes.Profile, Routes.Settings)

    NavigationBar {
        items.forEach { route ->
            NavigationBarItem(
                selected = current == route,
                onClick = { navController.navigate(route) },
                icon = {},
                label = { Text(route.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
private fun Screen(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
private fun SplashScreen(onContinue: () -> Unit) {
    Screen("Interview Mirror") {
        HeroCard(
            title = "Practice with focus",
            subtitle = "Mock interviews, instant feedback, weak-topic tracking, and a clean progress record."
        )
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Continue") }
    }
}

@Composable
private fun AuthRoute(
    onDone: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by authViewModel.state.collectAsState()

    LaunchedEffect(state.signedIn) {
        if (state.signedIn) onDone()
    }

    AuthScreen(
        state = state,
        onEmail = authViewModel::updateEmail,
        onPassword = authViewModel::updatePassword,
        onLogin = authViewModel::login,
        onCreateAccount = authViewModel::createAccount,
        onDemo = authViewModel::useDemoAccount
    )
}

@Composable
private fun AuthScreen(
    state: AuthUiState,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
    onDemo: () -> Unit
) {
    Screen("Welcome Back") {
        HeroCard(
            title = "Interview Mirror",
            subtitle = "Create your account once, then every completed session is saved to your Firebase history."
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmail,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = onPassword,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(onClick = onLogin, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) { Text("Login") }
        Button(onClick = onCreateAccount, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) { Text("Create Account") }
        Button(onClick = onDemo, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) { Text("Use Demo Account") }
        if (state.loading) {
            CircularProgressIndicator()
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun HomeScreen(state: InterviewUiState, onStart: () -> Unit) {
    Screen("Dashboard") {
        HeroCard(
            title = if (state.overallScore > 0) "${state.overallScore}/100 latest score" else "Ready for practice",
            subtitle = "Choose a domain, answer realistic questions, and review feedback after every session."
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MiniStat("Sessions", state.history.size.toString(), Modifier.weight(1f))
            MiniStat("Streak", "3", Modifier.weight(1f))
        }
        StatRow("Selected track", "${state.category} - ${state.difficulty}")
        StatRow("Mode", state.mode)
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Start Interview") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryScreen(
    state: InterviewUiState,
    onCategory: (String) -> Unit,
    onDifficulty: (String) -> Unit,
    onMode: (String) -> Unit,
    onBegin: () -> Unit
) {
    val categories = listOf("Android Development", "Kotlin", "Java", "DSA", "HR Interview", "Web Development", "Database/SQL")
    val difficulties = listOf("Beginner", "Intermediate", "Advanced")
    val modes = listOf("Quick", "Standard", "Deep")

    Screen("Choose Interview") {
        StatRow("Current selection", "${state.category} - ${state.difficulty} - ${state.mode}")
        Text("Category")
        ChipWrap(categories, state.category, onCategory)
        Text("Difficulty")
        ChipWrap(difficulties, state.difficulty, onDifficulty)
        Text("Mode")
        ChipWrap(modes, state.mode, onMode)
        Button(onClick = onBegin, modifier = Modifier.fillMaxWidth()) { Text("Start Interview") }
    }
}

@Composable
private fun ChipWrap(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(selected = selected == option, onClick = { onSelect(option) }, label = { Text(option) })
        }
    }
}

@Composable
private fun InterviewScreen(
    state: InterviewUiState,
    onAnswer: (String) -> Unit,
    onSubmit: () -> Unit,
    onResult: () -> Unit
) {
    Screen("Mock Interview") {
        val question = state.currentQuestion
        when {
            state.loading -> CircularProgressIndicator()
            state.isComplete -> {
                HeroCard("Interview complete", "Your report is saved to Firestore and ready to review.")
                Button(onClick = onResult, modifier = Modifier.fillMaxWidth()) { Text("View Result") }
            }
            question != null -> {
                val progress = (state.currentIndex + 1).toFloat() / state.questions.size.toFloat()
                Text("Question ${state.currentIndex + 1} of ${state.questions.size}", fontWeight = FontWeight.SemiBold)
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                Text(question.text, style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = state.answer,
                    onValueChange = onAnswer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    label = { Text("Type your answer") }
                )
                Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) { Text("Submit Answer") }
            }
            else -> Text("Loading questions...")
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun ResultScreen(state: InterviewUiState) {
    Screen("Final Report") {
        ScoreCard(score = state.overallScore, saved = state.historySaved)
        state.evaluations.lastOrNull()?.let { evaluation ->
            SectionTitle("Latest Feedback")
            StatRow("Correctness", evaluation.correctness)
            StatRow("Depth", evaluation.technicalDepth)
            StatRow("Clarity", evaluation.communicationClarity)
            SectionTitle("Suggested Answer")
            Text(evaluation.suggestedAnswer)
        }
        val weakAreas = state.evaluations.flatMap { it.missingConcepts }.distinct().take(5)
        SectionTitle("Weak Areas")
        Text(if (weakAreas.isEmpty()) "No major weak areas detected." else weakAreas.joinToString())
    }
}

@Composable
private fun HistoryScreen(state: InterviewUiState, onRefresh: () -> Unit) {
    Screen("Practice History") {
        Button(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) { Text("Refresh History") }
        when {
            state.historyLoading -> CircularProgressIndicator()
            state.history.isEmpty() -> HeroCard("No saved sessions yet", "Complete one interview and it will appear here automatically.")
            else -> state.history.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.category, fontWeight = FontWeight.Bold)
                            Text("${item.score}/100", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("${item.difficulty} - ${item.mode} - ${item.dateLabel}")
                        if (item.weakAreas.isNotEmpty()) {
                            Text("Weak areas: ${item.weakAreas.take(3).joinToString()}")
                        }
                    }
                }
            }
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun ProfileScreen() {
    Screen("Profile") {
        StatRow("Name", "Demo User")
        StatRow("Experience", "Beginner")
        StatRow("College/Profession", "Student")
    }
}

@Composable
private fun SettingsScreen() {
    Screen("Settings") {
        StatRow("Theme", "System")
        StatRow("Voice input", "Coming in voice module")
        StatRow("Text to speech", "Coming in voice module")
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label)
            Spacer(modifier = Modifier.padding(8.dp))
            Text(value, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HeroCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .backgroundBrush()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(subtitle, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(92.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ScoreCard(score: Int, saved: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("$score/100", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = { score / 100f }, modifier = Modifier.fillMaxWidth())
            Text(
                if (saved) "Saved to Firebase history" else "Saving history...",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun Modifier.backgroundBrush(): Modifier {
    return this.then(
        Modifier.background(
            Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.tertiary
                )
            )
        )
    )
}
