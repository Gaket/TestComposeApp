@file:OptIn(ExperimentalMaterial3Api::class)

package ru.gaket.stateincompose


import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.gaket.stateincompose.ui.theme.StateInComposeTheme
import java.util.Stack

class MainActivity : ComponentActivity() {

    private val navigationStack = Stack<Screen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigateTo(
            Screen.Login {
                navigateTo(
                    Screen.Counter("Anonymous")
                )
            }
        )

        onBackPressedDispatcher.addCallback {
            if (navigationStack.size == 1) {
                isEnabled = false
                @Suppress("DEPRECATION")
                onBackPressed()
            } else {
                navigationStack.pop()
                val previousScreen = navigationStack.peek()
                navigateTo(previousScreen, false)
            }
        }
    }

    private fun navigateTo(screen: Screen, addToBackstack: Boolean = true) {
        setContent {
            StateInComposeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    screen.Composable()
                }
            }
        }
        if (addToBackstack) {
            navigationStack.push(screen)
        }
    }
}

sealed class Screen {

    @Composable
    abstract fun Composable(): Unit

    class Login(val onLogin: () -> Unit) : Screen() {
        @Composable
        override fun Composable() {
            LoginScreen(onLogin)
        }

    }

    class Counter(val name: String) : Screen() {
        @Composable
        override fun Composable() {
            CounterScreen(name = "artur@getsquire.com", modifier = Modifier.fillMaxSize())
        }
    }

}


@Composable
fun CounterScreen(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Greeting(
            name = name,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(40.dp))
        PeopleCounter()
    }
}

@Composable
fun PeopleCounter(modifier: Modifier = Modifier) {
    val count: MutableState<Int> = rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "${count.value}", fontSize = 60.sp)
        Text(text = "people passed by")
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = {
            count.value = count.value + 1
        }) {
            Text("Click")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StateInComposeTheme {
//        LoginScreen {
//        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit, loginViewModel: LoginViewModel = viewModel()) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier) {
        Greeting(
            name = "Android",
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        LoginForm(onLogin,  {loginViewModel.onEmailInputChanged(it)}, loginViewModel.uiState.value)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun LoginForm(onLogin: () -> Unit, onInputChange: (String) -> Unit, inputString: LoginUiModel) {
    Column() {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputString.input,
            onValueChange = onInputChange,
            isError = inputString.isError,
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Type your email") }
        )
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Login")
        }
    }
}

