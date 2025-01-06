package com.kristinakoneva.androidbiometricauthentication

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * A manager for biometric authentication.
 */
object AuthManager {

    /**
     * Checks if biometric authentication is available on the device.
     *
     * @param context the context
     * @return true if biometric authentication is available, false otherwise
     */
    fun isBiometricAuthenticationAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)

        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d("AndroidBiometricAuthentication", "No biometric hardware available.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.d("AndroidBiometricAuthentication", "Biometric hardware unavailable.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.d("AndroidBiometricAuthentication", "No biometrics enrolled.")
                false
            }

            else -> false
        }
    }

    /**
     * Shows a biometric prompt for authentication.
     *
     * @param activity the activity
     * @param onSuccess the function to call when authentication is successful
     * @param onFailure the function to call when authentication fails
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt =
            BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d("AndroidBiometricAuthentication", "Authentication error: $errString")
                    onFailure()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("AndroidBiometricAuthentication", "Authentication succeeded.")
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d("AndroidBiometricAuthentication", "Authentication failed.")
                    onFailure()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate using your biometric credentials")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Prompt the user to enroll biometric credentials.
     *
     * @param context the context
     */
    fun promptEnrollBiometric(context: Context) {
        try {
            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_STRONG
                )
            }
            if (context is FragmentActivity) {
                context.startActivity(enrollIntent)
            }
        } catch (e: ActivityNotFoundException) {
            val fallbackIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
            if (context is FragmentActivity) {
                context.startActivity(fallbackIntent)
            }
        }
    }
}
