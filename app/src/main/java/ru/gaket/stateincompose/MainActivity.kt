@file:OptIn(ExperimentalMaterial3Api::class)

package ru.gaket.stateincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.gaket.stateincompose.ui.theme.StateInComposeTheme
import java.util.Stack

class MainActivity : ComponentActivity() {

    private val navigationStack = Stack<Screen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigateTo(
            Screen.Login {
                navigateTo(
                    Screen.Counter("Droid 2")
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
            CounterScreen(name = "Counter")
        }
    }

}


@Composable
fun CounterScreen(name: String, modifier: Modifier = Modifier) {
    Text(text = "Help $name!", modifier = modifier)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StateInComposeTheme {
        LoginScreen {
        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    Column(modifier = Modifier) {
        Greeting(
            name = "Android",
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        LoginForm(onLogin)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun LoginForm(onLogin: () -> Unit) {
    Column() {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Type your email") }
        )
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Login")
        }
    }
}

