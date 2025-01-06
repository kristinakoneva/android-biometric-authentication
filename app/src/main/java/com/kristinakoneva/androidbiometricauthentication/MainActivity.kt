package com.kristinakoneva.androidbiometricauthentication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.kristinakoneva.androidbiometricauthentication.ui.theme.AndroidBiometricAuthenticationTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidBiometricAuthenticationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Content(modifier: Modifier) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    // State to hold the result of the biometric authentication
    var authenticationResult by remember { mutableStateOf("Not authenticated.") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Biometric Authentication",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (activity != null) {
                if (AuthManager.isBiometricAuthenticationAvailable(context)) {
                    AuthManager.promptEnrollBiometric(context)
                    AuthManager.showBiometricPrompt(
                        activity = activity,
                        onSuccess = { authenticationResult = "Authentication succeeded!" },
                        onFailure = { authenticationResult = "Authentication failed!" }
                    )
                } else {
                    AuthManager.promptEnrollBiometric(context)
                    authenticationResult = "Biometric authentication is not available or has not been configured."
                }
            }
        }) {
            Text(text = "Authenticate")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = authenticationResult,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}
